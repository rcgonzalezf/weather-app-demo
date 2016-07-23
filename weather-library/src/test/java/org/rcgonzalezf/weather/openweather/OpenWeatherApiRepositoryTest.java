package org.rcgonzalezf.weather.openweather;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.BuildConfig;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.common.network.ApiRequest;
import org.rcgonzalezf.weather.common.network.RequestParameters;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;
import org.rcgonzalezf.weather.tests.WeatherTestLibApp;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 21, application = WeatherTestLibApp.class)
public class OpenWeatherApiRepositoryTest {

  private WeatherRepository<OpenWeatherApiRequestParameters> mWeatherRepository;

  private OpenWeatherApiRequestParameters mRequestParameters;
  private ApiRequest mApiRequest;
  private boolean mRequestParametersAdded;
  private boolean mExecuted;

  @Before public void setUpServiceConfig() {
    mWeatherRepository = spy(ServiceConfig.getInstance().getWeatherRepository());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldReturnExceptionThisRepositoryDoesNotAcceptSyncCalls() {
    givenRequestParametersWithCityId(123456);
    whenGettingWeather();
  }

  @Test public void shouldAddRequestParametersBeforeExecuting() {
    givenRequestParametersWithCityId(123456);
    givenApiRequest();
    whenGettingWeatherAsync();
    thenShouldHaveRequestParametersWhenExecuting();
  }

  private void thenShouldHaveRequestParametersWhenExecuting() {
    assertTrue(mRequestParametersAdded);
    assertTrue(mExecuted);
  }

  private void givenApiRequest() {
    mApiRequest = new ApiRequest() {
      @Override public String getBaseUrl() {
        return null;
      }

      @Override public String getMethodName() {
        return null;
      }

      @Override public void execute(ApiCallback apiCallback) {
        assertTrue(mRequestParametersAdded);
        mExecuted = true;
      }

      @Override public void addRequestParameters(RequestParameters requestParameters) {
        mRequestParametersAdded = true;
      }
    };

    when(mWeatherRepository.getApiRequest()).thenReturn(mApiRequest);
  }

  private void whenGettingWeatherAsync() {
    mWeatherRepository.findWeather(mRequestParameters, mock(ApiCallback.class));
  }

  private void whenGettingWeather() {
    mWeatherRepository.findWeather(mRequestParameters);
  }

  private void givenRequestParametersWithCityId(Integer someCityId) {
    mRequestParameters =
        new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withCityId(someCityId)
            .build();
  }
}
