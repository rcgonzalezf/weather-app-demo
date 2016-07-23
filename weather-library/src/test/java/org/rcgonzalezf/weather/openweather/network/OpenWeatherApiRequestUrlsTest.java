package org.rcgonzalezf.weather.openweather.network;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.BuildConfig;
import org.rcgonzalezf.weather.tests.WeatherTestLibApp;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, application = WeatherTestLibApp.class)
public class OpenWeatherApiRequestUrlsTest {

  private final String mExpectedUrl;
  private final OpenWeatherApiRequestParameters mOpenWeatherApiRequestParameters;
  private String mApiKey;
  private OpenWeatherApiRequest mOpenWeatherApiRequest;
  private String mUrl;

  @Before public void createApiRequestBaseObject() {
    mApiKey = "someApiKey";
    mOpenWeatherApiRequest = new OpenWeatherApiRequest(mApiKey);
  }

  public OpenWeatherApiRequestUrlsTest(String expectedUrl,
      OpenWeatherApiRequestParameters openWeatherApiRequestParameters) {
    mExpectedUrl = expectedUrl;
    mOpenWeatherApiRequestParameters = openWeatherApiRequestParameters;
  }

  @ParameterizedRobolectricTestRunner.Parameters public static Collection data() {

    return Arrays.asList(new Object[][] {
        {
            "http://api.openweathermap.org/data/2.5/forecast?q=London&type=like&APPID=someApiKey",
            createRequestParameterForCityName("London")
        }, {
        "http://api.openweathermap.org/data/2.5/forecast?lon=139.0&lat=35.0&APPID=someApiKey",
        createRequestParameterForLatLon(35d, 139d)
    }, {
        "http://api.openweathermap.org/data/2.5/forecast?id=524901&APPID=someApiKey",
        createRequestParameterForCityId(524901)
    }, {
        "http://api.openweathermap.org/data/2.5/forecast?units=metric&id=524901&APPID=someApiKey",
        createRequestParameterForCityIdAndMetricUnits(524901)
    },
    });
  }

  private static OpenWeatherApiRequestParameters createRequestParameterForCityIdAndMetricUnits(
      int cityId) {
    return new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withUnits(
        Units.METRIC).withCityId(cityId).build();
  }

  private static OpenWeatherApiRequestParameters createRequestParameterForCityId(int cityId) {
    return new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withCityId(cityId)
        .build();
  }

  private static OpenWeatherApiRequestParameters createRequestParameterForLatLon(Double lat,
      Double lon) {
    return new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withLatLon(lat, lon)
        .build();
  }

  private static OpenWeatherApiRequestParameters createRequestParameterForCityName(
      String cityName) {
    return new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withCityName(cityName)
        .build();
  }

  @Test public void testCurrentUri() {
    givenRequestParameters(mOpenWeatherApiRequestParameters);
    whenGetUrl();
    thenUrlShouldBeExpected();
  }

  private void thenUrlShouldBeExpected() {
    assertEquals(mExpectedUrl, mUrl);
  }

  private void whenGetUrl() {
    mUrl = mOpenWeatherApiRequest.url();
  }

  private void givenRequestParameters(
      OpenWeatherApiRequestParameters mOpenWeatherApiRequestParameters) {
    mOpenWeatherApiRequest.addRequestParameters(mOpenWeatherApiRequestParameters);
  }
}