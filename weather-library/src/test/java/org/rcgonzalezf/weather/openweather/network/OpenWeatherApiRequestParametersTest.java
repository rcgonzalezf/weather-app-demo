package org.rcgonzalezf.weather.openweather.network;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.tests.WeatherTestLibApp;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class) @Config(sdk = 23, application = WeatherTestLibApp.class)
public class OpenWeatherApiRequestParametersTest {

  private OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder mBuilder;
  private OpenWeatherApiRequestParameters uut;

  @Before public void createBaseBuilder() throws Exception {
    mBuilder = new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder();
  }

  @Test public void shouldBuildQueryWithLatLon() {
    Double lat = 12.13;
    Double lon = 31.21;
    givenLatLon(lat, lon);

    whenBuildApiParameters();

    thenParameterShouldContainLat(lat);
    thenParameterShouldContainLon(lon);
  }

  @Test public void shouldBuildQueryByCityName() {
    String someCityName = "someCity";
    givenCityName(someCityName);

    whenBuildApiParameters();

    thenParameterShouldContainCityName(someCityName);
  }

  private void givenCityName(String someCityName) {
    mBuilder.withCityName(someCityName);
  }

  private void givenLatLon(Double lat, Double lon) {
    mBuilder.withLatLon(lat, lon);
  }

  private void whenBuildApiParameters() {
    uut = mBuilder.build();
  }

  private void thenParameterShouldContainLat(Double lat) {
    assertNotNull(uut.getLat());
    assertEquals(String.valueOf(lat), uut.getLat());
  }

  private void thenParameterShouldContainLon(Double lon) {
    assertNotNull(uut.getLon());
    assertEquals(String.valueOf(lon), uut.getLon());
  }

  private void thenParameterShouldContainCityName(String cityName) {
    assertNotNull(uut.getCityName());
    assertEquals(cityName, uut.getCityName());
  }

}