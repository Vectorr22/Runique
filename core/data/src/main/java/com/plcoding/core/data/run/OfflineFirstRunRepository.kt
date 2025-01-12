package com.plcoding.core.data.run

import android.util.Log
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.putObject
import aws.smithy.kotlin.runtime.content.ByteStream
import com.plcoding.core.data.BuildConfig
import com.plcoding.core.domain.run.LocalRunDataSource
import com.plcoding.core.domain.run.RemoteRunDataSource
import com.plcoding.core.domain.run.Run
import com.plcoding.core.domain.run.RunId
import com.plcoding.core.domain.run.RunRepository
import com.plcoding.core.domain.util.DataError
import com.plcoding.core.domain.util.EmptyResult
import com.plcoding.core.domain.util.Result
import com.plcoding.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope
) : RunRepository {

    override fun getRuns(): Flow<List<Run>> {
        return localRunDataSource.getRuns()
    }

    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when (val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyDataResult()
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(run: Run, mapPictureByteArray: ByteArray): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(run)
        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }

        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPictureByteArray
        )

        uploadToS3(
            bucketName = BuildConfig.S3_BUCKET_NAME,
            accessKey =  BuildConfig.S3_ACCESS_KEY,
            secretAccessKey = BuildConfig.S3_SECRET_ACCESS_KEY,
            runId = runWithId.id!!,
            png = mapPictureByteArray
        )

        return when (remoteResult) {
            is Result.Error -> {
                Result.Success(Unit)
            }

            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()
    }

    private suspend fun uploadToS3(
        bucketName: String,
        accessKey: String,
        secretAccessKey: String,
        runId: String,
        png: ByteArray
    ) {
        val pngKey = "map_picture".plus("_").plus(runId)
        val s3Client = S3Client {
            region = "us-east-2"
            this.credentialsProvider = StaticCredentialsProvider {
                this.accessKeyId = accessKey
                this.secretAccessKey = secretAccessKey
            }
        }
        try {
            s3Client.putObject {
                this.bucket = bucketName
                this.key = pngKey
                this.body = ByteStream.fromBytes(png)
                this.contentType = "image/png"
            }
        }catch (e: Exception){
            e.printStackTrace()
            Timber.tag("Victor").i(e.message.toString())
        }
    }
}