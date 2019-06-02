package nurturecloud.search

import com.nurturecloud.search.HaversineCalculator
import org.junit.Assert
import org.junit.Test

class HaversineCalculatorTest {

    @Test
    fun haversineCloseTowns() {
        val place0 = Pair(-33.8697, 151.2099)
        val place1 = Pair(-33.751, 151.289)
        Assert.assertTrue(HaversineCalculator.distance(place0.first, place0.second, place1.first, place1.second) < 20f)
    }

    @Test
    fun haversineOthersideOfTheWorld() {
        val place0 = Pair(-33.8697, 151.2099)
        val place1 = Pair(-34.501, -56.289)
        Assert.assertTrue(HaversineCalculator.distance(place0.first, place0.second, place1.first, place1.second) > 11000f)
    }
}