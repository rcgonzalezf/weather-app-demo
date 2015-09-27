package org.rcgonzalezf.weather.network;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.rcgonzalezf.weather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.CITY_ID;
import static org.rcgonzalezf.weather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.LAT;
import static org.rcgonzalezf.weather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.LON;
import static org.rcgonzalezf.weather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.CITY_NAME;
import static org.rcgonzalezf.weather.network.OpenWeatherApiParameters.OpenWeatherApiRequestBuilder.TYPE;

public class OpenWeatherApiParametersTest {

  public OpenWeatherApiParameters.OpenWeatherApiRequestBuilder mBuilder;
  private OpenWeatherApiParameters mOpenWeatherApiParametersTest;

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

  private void givenCityName(String someCityName) {
    mBuilder.withCityName(someCityName);
  }

  private void givenLatLon(Double lat, Double lon) {
    mBuilder.withLatLon(lat, lon);
  }

  private void thenQueryStringShouldContain(String expected) {
    assertTrue(mOpenWeatherApiParametersTest.getQueryString().contains(expected));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowExceptionIfTryingToGetParameterMap() {
    givenCityId(anyInt());
    whenBuildApiParameters();
    thenKeyValueParametersShouldThrowException();
  }

  private void thenKeyValueParametersShouldThrowException() {
    mOpenWeatherApiParametersTest.getKeyValueParameters();
  }

  private void thenQueryStringShouldBe(String expectedQueryString) {
    assertEquals(expectedQueryString, mOpenWeatherApiParametersTest.getQueryString());
  }

  private void givenCityId(Integer cityId) {
    mBuilder.withCityId(cityId);
  }

  private void whenBuildApiParameters() {
    mOpenWeatherApiParametersTest = mBuilder.build();
  }
}