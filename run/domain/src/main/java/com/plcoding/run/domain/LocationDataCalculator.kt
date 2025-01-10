package com.plcoding.run.domain

import com.plcoding.core.domain.location.LocationTimeStamp
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

object LocationDataCalculator {

    fun getTotalDistanceInMeters(locations: List<List<LocationTimeStamp>>): Int {
        return locations
            .sumOf { timestampsPerLine ->
                timestampsPerLine.zipWithNext { location1, location2 ->
                    location1.location.location.distanceTo(location2.location.location)
                }.sum().roundToInt()
            }
    }

    fun getMaxSpeedKmh(locations: List<List<LocationTimeStamp>>): Double {
        return locations.maxOf { locationsSet ->
            locationsSet.zipWithNext { location1, location2 ->
                val distance = location1.location.location.distanceTo(
                    location2.location.location
                )
                val timeDifference = (location2.durationTimestamp - location1.durationTimestamp)
                    .toDouble(DurationUnit.HOURS)

                if (timeDifference == 0.0)
                     0.0
                else {
                    (distance / 1000.0) / timeDifference
                }
            }.maxOrNull() ?: 0.0
        }
    }

    fun getTotalElevationMeters(locations: List<List<LocationTimeStamp>>): Int {
        return locations.sumOf { locationSet ->
            locationSet.zipWithNext{location1, location2 ->
                val altitude1 = location1.location.altitude
                val altitude2 = location2.location.altitude
                (altitude2 - altitude1).coerceAtLeast(0.0)
            }.sum().roundToInt()
        }
    }
}