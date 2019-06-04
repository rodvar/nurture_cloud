package com.nurturecloud.search

import com.jayway.jsonpath.JsonPath
import com.nurturecloud.domain.Query
import com.nurturecloud.domain.Suburb
import java.net.URL
import java.util.concurrent.Executors
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.thread


/**
 * Suburb search engine
 */
class SuburbFinder {

    interface Listener {
        fun onResults(results : List<Suburb>)
        fun onError(error : String)
    }

    companion object {
        const val NEARBY = 5
        const val FRINGE = 10
        const val RESULTS_LIMIT = 15
        const val CACHE_MAX_KEYS = 100
        const val WORKERS_QTY = 10
        const val DB_PATH = "aus_suburbs.json"

        val log: Logger? = Logger.getLogger(SuburbFinder::class.simpleName)
    }

    private var resource: URL? = null
    private val db = arrayListOf<Suburb>()
    private val lastResults = HashMap<Suburb, List<Suburb>>() // cache last results for the FRINGE case and reuse
    private var initialized = false

    var maxResults = RESULTS_LIMIT

    val executor = Executors.newFixedThreadPool(WORKERS_QTY)

    /**
     * @param query with suburb name and postcode
     * @param maxDistanceKm to limit the results area
     *
     */
    fun find(suburb: Suburb, maxDistanceKm: Int, callback : (List<Suburb>) -> Unit) {
        this.waitForInit()
        this.executor.execute {
//            synchronized(this.db) {
                val startTime = System.currentTimeMillis()
                this.clearCacheKeeping(suburb)
                val found = if (suburb.hasGeoLocation()) {
                    val results: List<Suburb> = if (this.lastResults.containsKey(suburb)) {
                        this.lastResults[suburb]!!
                    } else {
                        this.filterByDistance(this.db, suburb, maxDistanceKm).let {
                            this.lastResults[suburb] = it
                            it
                        }
                    }
//                this.filterByDistance(results, suburb, maxDistanceKm).shuffled().take(this.maxResults) // TO GIVE MORE VARIETY IN THE RESPONSE
                    this.filterByDistance(results, suburb, maxDistanceKm).take(this.maxResults)
                } else {
                    listOf()
                }
                log?.log(Level.INFO, "Search took ${System.currentTimeMillis() - startTime}ms")
                callback(found)
//            }
        }
    }

    /**
     * @return the suburb associated with the query. Unique result.
     */
    fun find(query: Query): Suburb? {
        this.waitForInit()
        synchronized(db) {
            val list = db.filter { it.locality == query.suburb.toUpperCase() && it.postcode == query.postcode }
            return if (list.isEmpty()) null else list[0]
        }
    }

    /**
     * Filter the given list by distance
     * @param list of data results
     * @param suburb as a reference point (middle of the circle)
     * @param maxDistanceKm to limit results
     * @returntthe filtered list
     */
    private fun filterByDistance(list: List<Suburb>, suburb: Suburb, maxDistanceKm: Int): List<Suburb> {
        return list.filter {
            if (it != suburb && it.hasGeoLocation())
                HaversineCalculator.distance(suburb.latitude!!.toDouble(),
                        suburb.longitude!!.toDouble(),
                        it.latitude!!.toDouble(),
                        it.longitude!!.toDouble()) <= maxDistanceKm
            else
                false
        }
    }

    fun init() {
        // If DB is too big JSonPath can be used to parse over the file directly without needed to upload it completely to memory
        val list: List<Map<String, Any?>> = JsonPath.parse(this.dbResource().readText()).read("$..*")
        thread {
            Thread.currentThread().priority = Thread.MAX_PRIORITY
            this.toSuburb(list)
            this.initialized = true
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
     * TODO use Gson or Jackson
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

    private fun clearCacheKeeping(suburb: Suburb) {
        if (this.lastResults.keys.size >= CACHE_MAX_KEYS) {
            this.lastResults[suburb].let {
                this.clearCache()
                if (it != null)
                    this.lastResults[suburb] = it
            }
        }
    }

    fun clearCache() {
        this.lastResults.clear()
    }

    private fun waitForInit() {
        while (!this.initialized)
            Thread.sleep(10L)
    }
}