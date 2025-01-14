package com.vector.analytics.data

import com.plcoding.core.database.dao.AnalyticsDao
import com.vector.analytics.domain.AnalyticsRepository
import com.vector.analytics.domain.AnalyticsValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class RoomAnalyticsRepository(
    private val analyticsDao: AnalyticsDao,

):AnalyticsRepository {
    override suspend fun getAnalyticsValues(): AnalyticsValues {
        return withContext(Dispatchers.IO){
            val totalDistance = async {
                analyticsDao.getTotalDistance()
            }
            val totalTimeMillis = async {
                analyticsDao.getTotalTimeRun()
            }
            val maxRunSpeed = async {
                analyticsDao.getMaxRunSpeed()
            }
            val avgDistancePerRun = async {
                analyticsDao.getAvgDistancePerRun()
            }
            val avgPacePerRun = async {
                analyticsDao.getAvgPacePerRun()
            }
            AnalyticsValues(
                totalDistanceRun = totalDistance.await(),
                totalTimeRun = totalTimeMillis.await().milliseconds,
                fastestEverRun = maxRunSpeed.await(),
                avgDistanceRun = avgDistancePerRun.await(),
                avgPacePerRun = avgPacePerRun.await()
            )
        }
    }

}