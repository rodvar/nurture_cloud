package com.nurturecloud.domain

/**
 *
 */
data class Suburb(val postcode: Int,
                  val locality: String,
                  val state: String,
                  val comments: String,
                  val latitude: Float?,
                  val longitude: Float?) {

    fun hasGeoLocation() = this.latitude != null && this.longitude != null
}