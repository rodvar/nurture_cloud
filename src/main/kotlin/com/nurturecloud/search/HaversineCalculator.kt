package com.nurturecloud.search

import java.util.logging.Level
import java.util.logging.Logger

object HaversineCalculator {

    val log: Logger? = Logger.getLogger(HaversineCalculator::class.simpleName)

    private const val EARTH_RADIUS = 6371 //KM

    /**
     * @param lat0 in degrees
     * @param lat1 in degrees
     * @param lng0 in degrees
     * @param lng1 in degrees
     * @return distance in kms
     */
    fun distance(lat0: Double, lng0: Double, lat1: Double, lng1: Double): Float {
        return try {
            val latTetah = haversine(lat1, lat0)
            val lngTetah = haversine(lng1, lng0)
            val a = latTetah + Math.cos(toRadians(lat0)) * Math.cos(toRadians(lat1)) * lngTetah
            (2 * EARTH_RADIUS * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))).toFloat()
        } catch (e: Exception) {
            log?.log(Level.WARNING, "Failed to calulate haversine distance, returning a big number")
            Float.MAX_VALUE
        }
        // Math.asin(Math.sqrt(latTetah + Math.cos(Math.toRadians(lat0)) * Math.cos(Math.toRadians(lat1)) * lngTetah))
    }

    private fun haversine(degrees1: Double, degrees0: Double) = ((toRadians(degrees1 - degrees0)) / 2).let { Math.sin(it) * Math.sin(it) }

    private fun toRadians(value: Double): Double {
        return value * Math.PI / 180
    }
}