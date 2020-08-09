package org.rcgonzalezf.weather.common.models.db.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.rcgonzalezf.weather.db.WeatherAppDatabase

@RunWith(AndroidJUnit4::class)
class WeatherInfoDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: WeatherAppDatabase

    @Before
    fun setupDb() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                WeatherAppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun assertQueryEmptyTable() {
        val values = database.weatherInfoDao().getAll()
        assertTrue(values.value == null)
    }
}
