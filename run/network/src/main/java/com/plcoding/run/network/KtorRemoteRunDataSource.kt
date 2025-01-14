package com.plcoding.run.network

import android.util.Log
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.deleteObject
import aws.sdk.kotlin.services.s3.listObjectsV2
import aws.sdk.kotlin.services.s3.putObject
import aws.smithy.kotlin.runtime.content.ByteStream
import com.plcoding.core.data.BuildConfig
import com.plcoding.core.data.networking.constructRoute
import com.plcoding.core.data.networking.delete
import com.plcoding.core.data.networking.get
import com.plcoding.core.data.networking.safeCall
import com.plcoding.core.domain.run.RemoteRunDataSource
import com.plcoding.core.domain.run.Run
import com.plcoding.core.domain.util.DataError
import com.plcoding.core.domain.util.EmptyResult
import com.plcoding.core.domain.util.Result
import com.plcoding.core.domain.util.map
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorRemoteRunDataSource(
    private val httpClient: HttpClient
): RemoteRunDataSource {

    override suspend fun getRuns(): Result<List<Run>, DataError.Network> {
        return httpClient.get<List<RunDto>>(
            route = "/runs",
        ).map { runDtos ->
            runDtos.map { it.toRun() }
        }
    }

    override suspend fun postRun(run: Run, mapPicture: ByteArray): Result<Run, DataError.Network> {
        val createRunRequestJson = Json.encodeToString(run.toCreateRunRequest())
        val result = safeCall<RunDto> {
            httpClient.submitFormWithBinaryData(
                url = constructRoute("/run"),
                formData = formData {
                    append("MAP_PICTURE", mapPicture, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=mappicture.jpg")
                    })
                    append("RUN_DATA", createRunRequestJson, Headers.build {
                        append(HttpHeaders.ContentType, "text/plain")
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"RUN_DATA\"")
                    })
                }
            ) {
                method = HttpMethod.Post
            }

        }
        uploadToS3(
            bucketName = BuildConfig.S3_BUCKET_NAME,
            accessKey = BuildConfig.S3_ACCESS_KEY,
            secretAccessKey = BuildConfig.S3_SECRET_ACCESS_KEY,
            runId = run.id!!,
            png = mapPicture
        )
        return result.map {
            it.toRun()
        }
    }

    override suspend fun deleteRun(id: String): EmptyResult<DataError.Network> {

        return httpClient.delete(
            route = "/run",
            queryParameters = mapOf(
                "id" to id
            )
        )
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
        } catch (e: Exception) {
            e.printStackTrace()
            //Timber.tag("Victor").i(e.message.toString())
        }
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
            Log.e("Victor",e.toString())
        }
        finally {
            withContext(Dispatchers.IO) {
                s3Client.close()
            }
        }
    }
}