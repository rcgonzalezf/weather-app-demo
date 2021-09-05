package org.rcgonzalezf.weather.common.models.db.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.rcgonzalezf.weather.db.WeatherAppDatabase

@RunWith(AndroidJUnit4::class)
class WeatherInfoDaoTest {

    companion object {
        val ELEMENT = WeatherInfoEntity(cityId = 1, weatherId = 1, cityName = "Foo", speed = 1.1,
                temperature = 100.0, humidity = "Low", dateTime = "2020/10/12",
                country = "Bar", deg = 2.2, description = "Warm")
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: WeatherAppDatabase

    @Before
    fun setupDb() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                WeatherAppDatabase::class.java)
                //.allowMainThreadQueries()
                .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun queryEmptyTable() {
        val values = database.weatherInfoDao().getAll()
        assertNull(values)
    }

    @Test
    fun insertElement() {
        database.weatherInfoDao().insert(ELEMENT)
        val values = database.weatherInfoDao().getAll()
        assertNotNull(values)
        assertEquals(values?.first()?.cityName?:"", ELEMENT.cityName)
    }

    @Test
    fun deleteOldElements() {
        val uut = database.weatherInfoDao()
        uut.insert(ELEMENT)
        val values = uut.getAll()
        assertEquals(1, values?.size)

        uut.deleteOldWeatherInfo()
        val deletedValues = uut.getAll()
        assertEquals(0, deletedValues)
    }
}
