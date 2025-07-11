package com.plcoding.core.domain.run

import com.plcoding.core.domain.util.DataError
import com.plcoding.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    fun getRuns(): Flow<List<Run>>
    suspend fun fetchRuns(): EmptyResult<DataError>
    suspend fun upsertRun(run: Run, mapPictureByteArray: ByteArray): EmptyResult<DataError>
    suspend fun deleteRun(id: RunId, imageName: String)
    suspend fun deleteAllRuns()
    suspend fun syncPendingRuns()
    suspend fun logout(): EmptyResult<DataError.Network>
}