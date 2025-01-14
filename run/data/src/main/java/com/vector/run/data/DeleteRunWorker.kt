package com.vector.run.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.plcoding.core.database.dao.RunPendingSyncDao
import com.plcoding.core.domain.run.RemoteRunDataSource
import com.plcoding.core.domain.run.RunRepository
import com.plcoding.core.domain.util.Result

class DeleteRunWorker(
    context: Context,
    private val parameters: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingSyncDao: RunPendingSyncDao
): CoroutineWorker(context,parameters) {
    override suspend fun doWork(): Result {
        if(runAttemptCount >=5){
            return Result.failure()
        }
        val runId = parameters.inputData.getString(RUN_ID) ?: return Result.failure()
        Log.i("victor",runId)
        return when(val result = remoteRunDataSource.deleteRun(runId)){
            is com.plcoding.core.domain.util.Result.Error -> {
                result.error.ToWorkerResult()
            }
            is com.plcoding.core.domain.util.Result.Success -> {
                pendingSyncDao.deleteDeletedRunSyncEntity(runId)
                Result.success()
            }
        }
    }

    companion object{
        const val RUN_ID = "RUN_ID"
    }
}