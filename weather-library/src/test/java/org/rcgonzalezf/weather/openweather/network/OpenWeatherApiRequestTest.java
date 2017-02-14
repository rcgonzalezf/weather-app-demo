package org.rcgonzalezf.weather.openweather.network;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rcgonzalezf.weather.BuildConfig;
import org.rcgonzalezf.weather.common.listeners.OnUpdateWeatherListListener;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback;
import org.rcgonzalezf.weather.openweather.model.ForecastData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import org.rcgonzalezf.weather.tests.TestExecutor;
import org.rcgonzalezf.weather.tests.WeatherTestLibApp;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, application = WeatherTestLibApp.class)
public class OpenWeatherApiRequestTest {

  private OpenWeatherApiRequest uut;
  private OpenWeatherApiCallback mTestApiCallback;
  private boolean mIsSuccess;
  private boolean mIsError;
  @Mock private ModelConverter<OpenWeatherForecastData, ForecastData> mModelConverter;

  @Before public void createApiRequestBaseObject() {
    MockitoAnnotations.initMocks(this);

    uut = new OpenWeatherApiRequest("someApiKey", mModelConverter);
    uut = spy(uut);

    mTestApiCallback = new OpenWeatherApiCallback(mock(OnUpdateWeatherListListener.class)) {

      @Override public void onSuccess(OpenWeatherApiResponse apiResponse) {
        mIsSuccess = true;
      }

      @Override public void onError(OpenWeatherApiError apiError) {
        ErrorCode.valueOf("EMPTY");
        mIsError = true;
      }
    };
    uut.addRequestParameters(mock(OpenWeatherApiRequestParameters.class));
    when(uut.getExecutor()).thenReturn(new TestExecutor());
  }

  @Test public void shouldNotifySuccess() throws Exception {
    givenValidRequestReturningModel();
    whenExecuting();
    thenCallbackShouldBeSuccess();
  }

  @Test public void shouldNotifyOnError() throws Exception {
    givenInvalidRequestReturningNull();
    whenExecuting();
    thenCallbackShouldBeError();
  }

  @Test public void shouldNotifyOnErrorWhenEmptyResults() throws Exception {
    givenInvalidRequestReturningEmpty();
    whenExecuting();
    thenCallbackShouldBeError();
  }

  @Test public void shouldNotifyOnErrorWhenIOException() throws Exception {
    givenInvalidRequestReturningException();
    whenExecuting();
    thenCallbackShouldBeError();
  }

  private void givenInvalidRequestReturningException() throws IOException {
    when(mModelConverter.getForecastModel()).thenThrow(new IOException("someExec"));
  }

  private void givenInvalidRequestReturningEmpty() throws IOException {
    when(mModelConverter.getForecastModel()).thenReturn(new ArrayList<ForecastData>());
  }

  private void thenCallbackShouldBeError() {
    assertEquals(true, mIsError);
  }

  private void givenInvalidRequestReturningNull() throws IOException {
    when(mModelConverter.getForecastModel()).thenReturn(null);
  }

  private void whenExecuting() {
    uut.execute(mTestApiCallback);
  }

  private void thenCallbackShouldBeSuccess() {
    assertEquals(true, mIsSuccess);
  }

  private void givenValidRequestReturningModel() throws IOException {
    when(mModelConverter.getForecastModel()).thenReturn(new ArrayList<ForecastData>() {{
      add(mock(ForecastData.class));
    }});
  }
}