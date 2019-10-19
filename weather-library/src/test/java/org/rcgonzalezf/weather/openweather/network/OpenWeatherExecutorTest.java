package org.rcgonzalezf.weather.openweather.network;

import java.util.concurrent.Executor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rcgonzalezf.weather.BuildConfig;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback;
import org.rcgonzalezf.weather.openweather.model.ForecastData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherCurrentData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import org.rcgonzalezf.weather.tests.TestExecutor;
import org.rcgonzalezf.weather.tests.WeatherTestLibApp;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, application = WeatherTestLibApp.class)
public class OpenWeatherExecutorTest {

  private OpenWeatherExecutor uut;
  private Executor mExecutor;
  private OpenWeatherApiRequestParameters mRequestParameters;

  @Mock private ModelConverter<OpenWeatherForecastData, ForecastData, OpenWeatherCurrentData> mModelConverter;

  @Before public void createOpenWeatherExecutor() {
    MockitoAnnotations.initMocks(this);

    OpenWeatherApiCallback mApiCallBack = mock(OpenWeatherApiCallback.class);
    mExecutor = spy(new TestExecutor());
    String mSomeApiKey = "someKey";
    uut = new OpenWeatherExecutor(mApiCallBack, mExecutor, mSomeApiKey);
    uut.setModelConverter(mModelConverter);
  }

  @Test public void shouldExecuteWhenPerformingCallForCityName() {
    givenRequestParametersWithCityName();

    whenPerformingCall();

    thenShouldExecute();
  }

  @Test public void shouldExecuteWhenPerformingCallForLatLon() {
    givenRequestParametersWithLatLon();

    whenPerformingCall();

    thenShouldExecute();
  }

  private void givenRequestParametersWithLatLon() {
    mRequestParameters =
        new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withLatLon(1d, 1d)
            .build();
  }

  private void givenRequestParametersWithCityName() {
    mRequestParameters =
        new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withCityName("someCity")
            .build();
  }

  private void thenShouldExecute() {
    verify(mExecutor, times(1)).execute(any(Runnable.class));
  }

  private void whenPerformingCall() {
    uut.performRetrofitCall(mRequestParameters);
  }
}