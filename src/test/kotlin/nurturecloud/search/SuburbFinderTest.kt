package nurturecloud.search

import com.nurturecloud.domain.Query
import com.nurturecloud.domain.Suburb
import com.nurturecloud.search.SuburbFinder
import com.nurturecloud.search.SuburbFinder.Companion.FRINGE
import com.nurturecloud.search.SuburbFinder.Companion.NEARBY
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SuburbFinderTest {

    private var suburbFinder: SuburbFinder = SuburbFinder()

    init {
        this.suburbFinder.init()
    }

    @Before
    fun pre() {
        this.suburbFinder.clearCache()
    }

    @Test
    fun testFindSydneyCBD() {
        val sydney = this.suburbFinder.find(Query("Sydney", 2000))
        Assert.assertEquals("SYDNEY", sydney?.locality)
        Assert.assertEquals(2000, sydney?.postcode)
        Assert.assertEquals("NSW", sydney?.state)
        Assert.assertEquals(151.2099f, sydney?.longitude)
        Assert.assertEquals(-33.8697f, sydney?.latitude)
    }

    @Test
    fun testNearbyToSydneyCBD() {
        val sydney = this.suburbFinder.find(Query("Sydney", 2000))
        val nearbySubs: List<Suburb> = this.suburbFinder.find(sydney!!, NEARBY)
        Assert.assertEquals(this.suburbFinder.maxResults, nearbySubs.size)
    }

    @Test
    fun testFringeToSydneyCBD() {
        val sydney = this.suburbFinder.find(Query("Sydney", 2000))
        val nearbySubs: List<Suburb> = this.suburbFinder.find(sydney!!, FRINGE)
        Assert.assertEquals(SuburbFinder.RESULTS_LIMIT, nearbySubs.size)
    }

    @Test
    fun testSuburbNotFoundReturnsNoResults() {
        val sydney = this.suburbFinder.find(Query("wowowow", 1))
        Assert.assertNull(sydney)
    }

    @Test
    fun testNotGeolocatedSuburbWontReturnResults() {
        val hobart = this.suburbFinder.find(Query("NORTH HOBART", 7002))
        val nearbySubs: List<Suburb> = this.suburbFinder.find(hobart!!, NEARBY)
        Assert.assertEquals(0, nearbySubs.size)
    }
}