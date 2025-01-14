package com.vector.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.plcoding.core.database.dao.RunPendingSyncDao
import com.plcoding.core.database.mappers.toRun
import com.plcoding.core.domain.run.RemoteRunDataSource
import com.plcoding.core.domain.util.Result

class CreateRunWorker(
    context: Context,
    private val parameters: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingSyncDao: RunPendingSyncDao
): CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        if(runAttemptCount >=5){
            return Result.failure()
        }
        val pendingRunId = parameters.inputData.getString(RUN_ID) ?: return Result.failure()
        val pendingRunEntity = pendingSyncDao.getARunPendingSyncEntity(pendingRunId) ?: return Result.failure()

        val run = pendingRunEntity.run.toRun()
        return when(val result = remoteRunDataSource.postRun(run, pendingRunEntity.mapPicturesBytes)){
            is com.plcoding.core.domain.util.Result.Error -> {
                result.error.ToWorkerResult()
            }
            is com.plcoding.core.domain.util.Result.Success -> {
                pendingSyncDao.deleteRunPendingSyncEntity(pendingRunId)
                Result.success()
            }
        }
    }
    companion object{
        const val RUN_ID = "RUN_ID"
    }
}