package org.rcgonzalezf.weather.openweather.network;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.CITY_ID;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.LAT;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.LON;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.CITY_NAME;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.TYPE;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.UNITS;
import static org.rcgonzalezf.weather.openweather.network.Units.IMPERIAL;
import static org.rcgonzalezf.weather.openweather.network.Units.METRIC;

public class OpenWeatherApiParametersTest {

  public OpenWeatherApiParameters.OpenWeatherApiRequestBuilder mBuilder;
  private OpenWeatherApiParameters mOpenWeatherApiParameters;

  @Before public void createBaseBuilder() throws Exception {
    mBuilder = new OpenWeatherApiParameters.OpenWeatherApiRequestBuilder();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfBuildWithoutParameters() {
    whenBuildApiParameters();
  }

  @Test public void shouldBuildQueryWithIdParameter() {
    Integer someId = 123456;
    givenCityId(someId);
    whenBuildApiParameters();
    thenQueryStringShouldBe(CITY_ID + "=" + someId);
  }

  @Test public void shouldBuildQueryWithLatLon() {
    Double lat = 12.13;
    Double lon = 31.21;
    givenLatLon(lat, lon);
    whenBuildApiParameters();
    thenQueryStringShouldContain(LAT + "=" + lat);
    thenQueryStringShouldContain("&");
    thenQueryStringShouldContain(LON + "=" + lon);
  }

  @Test public void shouldBuildQueryByCityName() {
    String someCityName = "someCity";
    givenCityName(someCityName);
    whenBuildApiParameters();
    thenQueryStringShouldContain(CITY_NAME + "=" + someCityName);
    thenQueryStringShouldContain("&");
    thenQueryStringShouldContain(TYPE + "=like");
  }

  @Test public void shouldBuildQueryByCityNameAndImperialUnits() {
    String someCityName = "someCity";
    givenCityName(someCityName);
    givenUnits(IMPERIAL);
    whenBuildApiParameters();
    thenQueryStringShouldContain(CITY_NAME + "=" + someCityName);
    thenQueryStringShouldContain("&");
    thenQueryStringShouldContain(TYPE + "=like");
    thenQueryStringShouldContain(UNITS + "=" + IMPERIAL.getUnitName());
  }

  @Test public void shouldBuildQueryWithIdParameterAndMetricUnits() {
    Integer someId = 123456;
    givenCityId(someId);
    givenUnits(METRIC);
    whenBuildApiParameters();
    thenQueryStringShouldContain(CITY_ID + "=" + someId);
    thenQueryStringShouldContain("&");
    thenQueryStringShouldContain(UNITS + "=" + METRIC.getUnitName());
  }

  private void givenUnits(Units imperial) {
    mBuilder.withUnits(imperial);
  }

  private void givenCityName(String someCityName) {
    mBuilder.withCityName(someCityName);
  }

  private void givenLatLon(Double lat, Double lon) {
    mBuilder.withLatLon(lat, lon);
  }

  private void thenQueryStringShouldContain(String expected) {
    assertTrue(mOpenWeatherApiParameters.getQueryString().contains(expected));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowExceptionIfTryingToGetParameterMap() {
    givenCityId(anyInt());
    whenBuildApiParameters();
    thenKeyValueParametersShouldThrowException();
  }

  private void thenKeyValueParametersShouldThrowException() {
    mOpenWeatherApiParameters.getKeyValueParameters();
  }

  private void thenQueryStringShouldBe(String expectedQueryString) {
    assertEquals(expectedQueryString, mOpenWeatherApiParameters.getQueryString());
  }

  private void givenCityId(Integer cityId) {
    mBuilder.withCityId(cityId);
  }

  private void whenBuildApiParameters() {
    mOpenWeatherApiParameters = mBuilder.build();
  }
}