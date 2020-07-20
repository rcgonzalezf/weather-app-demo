package rcgonzalezf.org.weather.list

import android.app.Application
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.rcgonzalezf.weather.common.ServiceConfig
import org.rcgonzalezf.weather.common.WeatherRepository
import org.rcgonzalezf.weather.common.models.WeatherInfo
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters
import rcgonzalezf.org.weather.common.ToggleBehavior
import rcgonzalezf.org.weather.list.WeatherListViewModel.Companion.FORECASTS
import rcgonzalezf.org.weather.list.WeatherListViewModel.Companion.OFFLINE_FILE
import rcgonzalezf.org.weather.location.CityFromLatLongRetriever
import rcgonzalezf.org.weather.utils.UserNotifier

class WeatherListViewModelTest {

    private var storedData: String? = null
    lateinit var uut: WeatherListViewModel
    private var weatherInfoList: ArrayList<WeatherInfo>? = null

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    @Mock
    lateinit var userNotifier: UserNotifier

    @Mock
    lateinit var openWeatherApiCallback: OpenWeatherApiCallback

    @Mock
    lateinit var toggleBehavior: ToggleBehavior

    @Mock
    lateinit var cityFromLatLongRetriever: CityFromLatLongRetriever

    @Mock
    lateinit var app: Application

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        uut = WeatherListViewModel(openWeatherApiCallback,
                cityFromLatLongRetriever, toggleBehavior, app, userNotifier)
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

    @Test
    fun shouldGetSavedStoredData() {
        givenEmptyForecastListStoredData();
        givenSharedPreferenceWithStoredData();

        val storedData = whenGettingTheSavedData();

        thenShouldHaveStoredData(storedData);
    }

    @Test
    fun shouldLoadOldData() {
        givenForecastList()
        givenForecastElement("someCity")

        whenLoadingOldData()

        assertNotNull(uut.weatherInfoList.value)
        assertTrue(uut.offline.value as Boolean)
    }

    @Test
    fun shouldLoadOldDataOfflineForNullList() {
        weatherInfoList = null

        whenLoadingOldData()

        assertNull(uut.weatherInfoList.value)
        assertTrue(uut.offline.value as Boolean)
    }

    @Test
    fun willGetCityNameFromLatLon() {
        val lat = 0.0
        val lon = 0.0
        val someCityName = "someCityName"
        Mockito.`when`(cityFromLatLongRetriever.getFromLatLong(Mockito.eq(lat), Mockito.eq(lon)))
                .thenReturn(someCityName)

        val cityName = uut.cityNameFromLatLon(lat, lon)

        assertEquals(cityName, someCityName)
    }

    @Test
    fun willSearchByQuery() {
        val someCityName = "someCityName"
        val serviceConfig = Mockito.mock(ServiceConfig::class.java)
        val weatherRepository: WeatherRepository<*, *>? = Mockito.mock(WeatherRepository::class.java)
        Mockito.`when`(serviceConfig
                .getWeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback?>())
                .thenReturn(weatherRepository as WeatherRepository
                <OpenWeatherApiRequestParameters, OpenWeatherApiCallback?>?)
        uut = WeatherListViewModel(openWeatherApiCallback,
                cityFromLatLongRetriever, toggleBehavior, app, userNotifier, serviceConfig)

        uut.searchByQuery(someCityName, someCityName)

        Mockito.verify(toggleBehavior).toggle()
        Mockito.verify(userNotifier).notify(Mockito.anyString())
        Mockito.verify(weatherRepository)?.findWeather(Mockito.any(OpenWeatherApiRequestParameters::class.java),
                Mockito.eq(openWeatherApiCallback))
    }

    ///// Given-When-Then section

    fun givenEmptyForecastListStoredData() {
        val dataToStore = ArrayList<WeatherInfo>()
        dataToStore.add(WeatherInfo());
        storedData = Gson().toJson(dataToStore);
    }

    private fun givenSharedPreferenceWithStoredData() {
        Mockito.`when`(app.getSharedPreferences(OFFLINE_FILE, 0)).thenReturn(sharedPreferences)
        Mockito.`when`(sharedPreferences.getString(FORECASTS, null)).thenReturn(storedData)
    }

    private fun whenGettingTheSavedData(): List<WeatherInfo>? =
            uut.previousForecastList

    private fun thenShouldHaveStoredData(storedData: List<WeatherInfo>?) =
            assertNotNull(storedData)

    private fun givenForecastList() {
        weatherInfoList = ArrayList()
    }

    private fun givenForecastElement(cityName: String) {
        val weatherInfo = WeatherInfo()
        weatherInfo.cityName = cityName
        weatherInfoList?.add(weatherInfo)
    }

    private fun whenLoadingOldData() {
        uut.loadOldData(weatherInfoList);
    }

}
