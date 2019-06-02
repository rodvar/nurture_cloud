package com.nurturecloud.search

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import com.nurturecloud.domain.Query
import com.nurturecloud.domain.Suburb
import java.net.URL
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.thread


/**
 * Suburb search engine
 */
class SuburbFinder {

    companion object {
        const val NEARBY = 5
        const val FRINGE = 10
        const val RESULTS_LIMIT = 15
        const val DB_PATH = "aus_suburbs.json"

        val log: Logger? = Logger.getLogger(SuburbFinder::class.simpleName)
    }

    var resource: URL? = null
    val db = arrayListOf<Suburb>()
    var maxResults = RESULTS_LIMIT

    /**
     * @param query with suburb name and postcode
     * @param maxDistanceKm to limit the results area
     *
     */
    fun find(suburb: Suburb, maxDistanceKm: Int): List<Suburb> {
        synchronized(db) {
            return if (suburb.hasGeoLocation()) {
                this.db.filter {
                    if (it.hasGeoLocation())
                        HaversineCalculator.distance(suburb.latitude!!.toDouble(),
                                suburb.longitude!!.toDouble(),
                                it.latitude!!.toDouble(),
                                it.longitude!!.toDouble()) <= maxDistanceKm
                    else
                        false
                }.take(this.maxResults)
            } else {
                listOf()
            }
        }
    }

    /**
     * @return the suburb associated with the query. Unique result.
     */
    fun find(query: Query): Suburb? {
        synchronized(db) {
            val list = db.filter { it.locality == query.suburb.toUpperCase() && it.postcode == query.postcode }
            return if (list.isEmpty()) null else list[0]
        }
    }

    fun init() {
        Configuration.setDefaults(object : Configuration.Defaults {

            private val jsonProvider = JacksonJsonProvider()
            private val mappingProvider = JacksonMappingProvider()

            override fun jsonProvider(): JsonProvider {
                return jsonProvider
            }

            override fun mappingProvider(): MappingProvider {
                return mappingProvider
            }

            override fun options(): Set<Option> {
                return EnumSet.noneOf(Option::class.java)
            }
        })
        val list: List<Map<String, Any?>> = JsonPath.parse(this.dbResource().readText()).read("$..*")
        thread {
            synchronized(db) {
                this.toSuburb(list)
            }
        }
    }

    /**
     * @param the list of hashmap strings to be converted into the POJO
     */
    private fun toSuburb(list: List<Map<String, Any?>>) {
        try {
            list.forEach {
                toSuburb(it)?.let { suburb ->
                    db.add(suburb)
                }
            }
        } catch (e: Exception) {
            log?.log(Level.FINE, "Failed to parse list")
        }
    }

    /**
     * @param the list of hashmap strings to be converted into the POJO
     */
    private fun toSuburb(map: Map<String, Any?>) = try {
        Suburb(map["Pcode"] as Int,
                map["Locality"] as String,
                map["State"] as String,
                map["Comments"] as String,
                (map["Latitude"]?.toString()?.toFloat()),
                (map["Longitude"]?.toString()?.toFloat()))
    } catch (e: Exception) {
        log?.log(Level.SEVERE, "${map["Pcode"]}, ${map["Locality"]} failed to be parsed")
        null
    }

    private fun dbResource(): URL {
        if (this.resource == null)
            this.resource = Thread.currentThread().contextClassLoader.getResource(DB_PATH)
        return this.resource!!
    }
}