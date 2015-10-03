package org.rcgonzalezf.weather.openweather.network;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.BuildConfig;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.CITY_ID;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.CITY_NAME;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.LAT;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.LON;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.TYPE;
import static org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder.UNITS;
import static org.rcgonzalezf.weather.openweather.network.Units.IMPERIAL;
import static org.rcgonzalezf.weather.openweather.network.Units.METRIC;

@RunWith(RobolectricGradleTestRunner.class) @Config(constants = BuildConfig.class, sdk = 21)
public class OpenWeatherApiRequestParametersTest {

  public OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder mBuilder;
  private OpenWeatherApiRequestParameters mOpenWeatherApiRequestParameters;

  @Before public void createBaseBuilder() throws Exception {
    mBuilder = new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder();
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

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowExceptionIfTryingToGetParameterMap() {
    givenCityId(123456);
    whenBuildApiParameters();
    thenKeyValueParametersShouldThrowException();
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
    assertTrue(mOpenWeatherApiRequestParameters.getQueryString().contains(expected));
  }

  private void thenKeyValueParametersShouldThrowException() {
    mOpenWeatherApiRequestParameters.getKeyValueParameters();
  }

  private void thenQueryStringShouldBe(String expectedQueryString) {
    assertEquals(expectedQueryString, mOpenWeatherApiRequestParameters.getQueryString());
  }

  private void givenCityId(Integer cityId) {
    mBuilder.withCityId(cityId);
  }

  private void whenBuildApiParameters() {
    mOpenWeatherApiRequestParameters = mBuilder.build();
  }
}