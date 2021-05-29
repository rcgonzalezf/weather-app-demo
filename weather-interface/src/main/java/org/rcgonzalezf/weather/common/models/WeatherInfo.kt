package org.rcgonzalezf.weather.common.models

import android.os.Parcel
import android.os.Parcelable

class WeatherInfo : WeatherViewModel {
    constructor()

    override var weatherId = 0
    override var id = 0
        private set
    override var cityName: String? = null
    override var speed = 0.0
    override var temperature = 0.0
    override var humidity: String? = null
    override var dateTime: String? = null
    override var country: String? = null
    override var deg = 0.0
    override var description: String? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(cityName)
        dest.writeDouble(speed)
        dest.writeDouble(temperature)
        dest.writeString(humidity)
        dest.writeString(dateTime)
        dest.writeInt(weatherId)
        dest.writeString(country)
        dest.writeDouble(deg)
        dest.writeString(description)
    }

    private constructor(`in`: Parcel) {
        id = `in`.readInt()
        cityName = `in`.readString()
        speed = `in`.readDouble()
        temperature = `in`.readDouble()
        humidity = `in`.readString()
        dateTime = `in`.readString()
        weatherId = `in`.readInt()
        country = `in`.readString()
        deg = `in`.readDouble()
        description = `in`.readString()
    }

    fun setCityId(cityId: Int) {
        id = cityId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val weatherInfo = other as WeatherInfo
        if (weatherId != weatherInfo.weatherId) return false
        if (id != weatherInfo.id) return false
        if (java.lang.Double.compare(weatherInfo.speed, speed) != 0) return false
        if (java.lang.Double.compare(weatherInfo.temperature, temperature) != 0) return false
        if (java.lang.Double.compare(weatherInfo.deg, deg) != 0) return false
        if (cityName != weatherInfo.cityName) return false
        if (if (humidity != null) humidity != weatherInfo.humidity else weatherInfo.humidity != null) {
            return false
        }
        if (if (dateTime != null) dateTime != weatherInfo.dateTime else weatherInfo.dateTime != null) {
            return false
        }
        if (country != weatherInfo.country) return false
        return if (description != null) description == weatherInfo.description else weatherInfo.description == null
    }

    override fun hashCode(): Int {
        var result: Int
        var temp: Long
        result = weatherId
        result = 31 * result + id
        result = 31 * result + cityName.hashCode()
        temp = java.lang.Double.doubleToLongBits(speed)
        result = 31 * result + (temp xor (temp ushr 32)).toInt()
        temp = java.lang.Double.doubleToLongBits(temperature)
        result = 31 * result + (temp xor (temp ushr 32)).toInt()
        result = 31 * result + if (humidity != null) humidity.hashCode() else 0
        result = 31 * result + if (dateTime != null) dateTime.hashCode() else 0
        result = 31 * result + country.hashCode()
        temp = java.lang.Double.doubleToLongBits(deg)
        result = 31 * result + (temp xor (temp ushr 32)).toInt()
        result = 31 * result + if (description != null) description.hashCode() else 0
        return result
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<WeatherInfo> = object : Parcelable.Creator<WeatherInfo> {
            override fun createFromParcel(`in`: Parcel): WeatherInfo? {
                return WeatherInfo(`in`)
            }

            override fun newArray(size: Int): Array<WeatherInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}