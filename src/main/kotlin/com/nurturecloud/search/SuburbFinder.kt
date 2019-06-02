package com.nurturecloud.search

import com.nurturecloud.domain.Query
import com.nurturecloud.domain.Suburb

/**
 *
 */
class SuburbFinder {

    companion object {
        const val NEARBY = 5
        const val FRINGE = 10

        const val RESULTS_LIMIT = 15
    }

    /**
     * @param query with suburb name and postcode
     * @param maxDistanceKm to limit the results area
     *
     */
    fun find(query: Query, maxDistanceKm: Int = NEARBY): List<Suburb> {

        return listOf()
    }
}