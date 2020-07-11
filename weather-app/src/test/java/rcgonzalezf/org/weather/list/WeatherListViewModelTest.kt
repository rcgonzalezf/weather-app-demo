package rcgonzalezf.org.weather.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class WeatherListViewModelTest {

    lateinit var uut:WeatherListViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        uut = WeatherListViewModel()
    }

    @Test
    fun getCityNameToSearchOnSwipe() {
        val expectedValue = "mockValue"
        uut.cityNameToSearchOnSwipe.value = expectedValue

        assertThat(uut.cityNameToSearchOnSwipe.value.toString(), `is`(expectedValue))
    }

    @Test
    fun updateCityNameForSwipeToRefresh() {
        val expectedValue = "mockValue"

        uut.updateCityNameForSwipeToRefresh(expectedValue)

        assertThat(uut.cityNameToSearchOnSwipe.value.toString(), `is`(expectedValue))
    }
}
