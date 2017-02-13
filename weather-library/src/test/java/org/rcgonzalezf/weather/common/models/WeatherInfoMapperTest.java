package org.rcgonzalezf.weather.common.models;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.rcgonzalezf.weather.openweather.model.City;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class) public class WeatherInfoMapperTest {

  ForecastMapper uut;

  private List<ForecastData> mData;
  private List<WeatherInfo> mDataMaped;

  @Before public void initMapper() {
    uut = new ForecastMapper();

    mData = new ArrayList<>();
    mData.add(new ForecastData(new City(), 3));
  }

  @Test public void shouldMapForecastDataWithEmptyWeatherList() {
    givenData();

    whenMapping();

    thenDataMappedShouldNotBeNull();
    thenWeatherItemsExpected(0);
  }

  @Test public void shouldMapForecastData() {
    int expectedSize = 3;
    givenWeatherData(expectedSize);
    givenData();

    whenMapping();

    thenDataMappedShouldNotBeNull();
    thenWeatherItemsExpected(expectedSize);
  }

  @Test public void shouldMapForecastDataWithHowManyLessThanAll() {
    int expectedSize = 3;
    int howMany = 2;
    givenWeatherData(expectedSize);
    givenData();

    whenMappingHowMany(howMany);

    thenDataMappedShouldNotBeNull();
    thenWeatherItemsExpected(howMany);
  }

  private void thenWeatherItemsExpected(int howManyExpected) {
    assertEquals(howManyExpected, mDataMaped.size());
  }

  private void whenMappingHowMany(int howMany) {
    mDataMaped = uut.map(howMany);
  }

  private void givenWeatherData(int times) {
    for (int i = 0; i < times; ++i) {
      WeatherData weatherData = new WeatherData();
      mData.get(0).addWeatherItem(weatherData);
    }
  }

  private void thenDataMappedShouldNotBeNull() {
    assertNotNull(mDataMaped);
  }

  private void whenMapping() {
    mDataMaped = uut.map();
  }

  private void givenData() {
    uut.withData(mData);
  }
}