package org.rcgonzalezf.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.rcgonzalezf.weather.common.models.db.weather.WeatherInfoDao
import org.rcgonzalezf.weather.common.models.db.weather.WeatherInfoEntity

@Database(entities = [WeatherInfoEntity::class], version = 1)
abstract class WeatherAppDatabase : RoomDatabase() {

    abstract fun weatherInfoDao(): WeatherInfoDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherAppDatabase? = null

        fun getInstance(context: Context): WeatherAppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        WeatherAppDatabase::class.java, "WeatherInfo.db")
                        .fallbackToDestructiveMigration()
                        .build()
    }
}
