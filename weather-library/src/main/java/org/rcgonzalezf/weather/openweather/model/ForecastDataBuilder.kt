package org.rcgonzalezf.weather.openweather.model

class ForecastDataBuilder {
    private lateinit var city: City
    private var count = 0
    fun setCity(city: City): ForecastDataBuilder {
        this.city = city
        return this
    }

    fun setCount(count: Int): ForecastDataBuilder {
        this.count = count
        return this
    }

    fun createForecastData(): ForecastData {
        return ForecastData(city, count)
    }
}
