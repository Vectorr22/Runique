package com.plcoding.core.data.run

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.deleteObject
import aws.sdk.kotlin.services.s3.listObjectsV2
import com.plcoding.core.data.BuildConfig
import com.plcoding.core.data.networking.get
import com.plcoding.core.database.dao.RunPendingSyncDao
import com.plcoding.core.database.mappers.toRun
import com.plcoding.core.domain.SessionStorage
import com.plcoding.core.domain.run.LocalRunDataSource
import com.plcoding.core.domain.run.RemoteRunDataSource
import com.plcoding.core.domain.run.Run
import com.plcoding.core.domain.run.RunId
import com.plcoding.core.domain.run.RunRepository
import com.plcoding.core.domain.run.SyncRunScheduler
import com.plcoding.core.domain.util.DataError
import com.plcoding.core.domain.util.EmptyResult
import com.plcoding.core.domain.util.Result
import com.plcoding.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val syncRunScheduler: SyncRunScheduler,
    private val client: HttpClient
) : RunRepository {

    override fun getRuns(): Flow<List<Run>> {
        return localRunDataSource.getRuns()
    }

    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when (val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyDataResult()
            is Result.Success -> {

                val listOfImages = getImagesFromBucket(
                    bucketName = BuildConfig.S3_BUCKET_NAME,
                    accessKey = BuildConfig.S3_ACCESS_KEY,
                    secretAccessKey = BuildConfig.S3_SECRET_ACCESS_KEY
                )
                val newList: MutableList<Run> = mutableListOf()
                repeat(result.data.size) {
                    newList.add(
                        result.data[it].copy(
                            mapPictureUrl = listOfImages[it]
                        )
                    )
                }
                applicationScope.async {
                    localRunDataSource.upsertRuns(newList).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(
        run: Run,
        mapPictureByteArray: ByteArray
    ): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(run)
        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }

        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPictureByteArray
        )

        return when (remoteResult) {
            is Result.Error -> {
                applicationScope.launch {
                    syncRunScheduler.scheduleSync(
                        type = SyncRunScheduler.SyncType.CreateRun(
                            run = runWithId,
                            mapPictureBytes = mapPictureByteArray
                        )
                    )
                }.join()
                Result.Success(Unit)
            }

            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId, imgName: String) {
        localRunDataSource.deleteRun(id)
        val isPendingSync = runPendingSyncDao.getARunPendingSyncEntity(id) != null
        if (isPendingSync) {
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }
        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()

        deleteImageObject(
            bucketName = BuildConfig.S3_BUCKET_NAME,
            accessKey = BuildConfig.S3_ACCESS_KEY,
            secretAccessKey = BuildConfig.S3_SECRET_ACCESS_KEY,
            imageName = imgName
        )

        if (remoteResult is Result.Error) {
            applicationScope.launch {
                syncRunScheduler.scheduleSync(
                    type = SyncRunScheduler.SyncType.DeleteRun(runId = id)
                )
            }.join()
        }
    }

    override suspend fun deleteAllRuns() {
        localRunDataSource.deleteAllRuns()
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext
            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }
            val deletedRuns = async {
                runPendingSyncDao.getAllDeletedRunSyncEntities(userId)
            }

            val createJobs = createdRuns
                .await()
                .map {
                    launch {
                        val run = it.run.toRun()
                        when (remoteRunDataSource.postRun(run, it.mapPicturesBytes)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteRunPendingSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            val deleteJobs = deletedRuns
                .await()
                .map {
                    launch {
                        when (remoteRunDataSource.deleteRun(it.runId)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteDeletedRunSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            createJobs.forEach { it.join() }
            deleteJobs.forEach { it.join() }

        }
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val result = client.get<Unit>(
            route = "/logout"
        ).asEmptyDataResult()

        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
            .firstOrNull()
            ?.clearToken()
        return result
    }

    private suspend fun getImagesFromBucket(
        bucketName: String,
        accessKey: String,
        secretAccessKey: String
    ): List<String?> {
        val s3Client = S3Client {
            region = "us-east-2"
            this.credentialsProvider = StaticCredentialsProvider {
                this.accessKeyId = accessKey
                this.secretAccessKey = secretAccessKey
            }
        }

        val resp = s3Client.listObjectsV2 {
            this.bucket = bucketName
        }
        return resp.contents?.map { "https://$bucketName.s3.us-east-2.amazonaws.com/${it.key}" }
            ?: emptyList<String>()
    }

    private suspend fun deleteImageObject(
        bucketName: String,
        accessKey: String,
        secretAccessKey: String,
        imageName: String
    ) {
        val s3Client = S3Client {
            region = "us-east-2"
            this.credentialsProvider = StaticCredentialsProvider {
                this.accessKeyId = accessKey
                this.secretAccessKey = secretAccessKey
            }
        }
        val key = imageName.substringAfter(".com/")
        try {
            if (imageName.isNotEmpty()) {
                s3Client.deleteObject {
                    this.bucket = bucketName
                    this.key = key
                }
            }
        }catch (e: Exception){
            Timber.e(e.toString())
        }
        finally {
            withContext(Dispatchers.IO) {
                s3Client.close()
            }
        }
    }

}