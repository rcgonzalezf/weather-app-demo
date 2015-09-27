package org.rcgonzalezf.weather.openweather.network;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.BuildConfig;
import org.rcgonzalezf.weather.common.models.WeatherData;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.tests.TestExecutor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class) @Config(constants = BuildConfig.class, sdk = 21)
public class OpenWeatherApiRequestTest {

  private OpenWeatherApiRequest mOpenWeatherApiRequest;
  private ApiCallback<OpenWeatherApiResponse, OpenWeatherApiError> mTestApiCallback;
  private boolean mIsSuccess;
  private boolean mIsError;
  private ModelConverter mModelConverter;

  @Before public void createApiRequestBaseObject() {
    mModelConverter = mock(ModelConverter.class);
    mOpenWeatherApiRequest = new OpenWeatherApiRequest("someApiKey", mModelConverter);
    mOpenWeatherApiRequest = spy(mOpenWeatherApiRequest);

    mTestApiCallback = new ApiCallback<OpenWeatherApiResponse, OpenWeatherApiError>() {

      @Override public void onSuccess(OpenWeatherApiResponse apiResponse) {
        mIsSuccess = true;
      }

      @Override public void onError(OpenWeatherApiError apiError) {
        mIsError = true;
      }
    };
    mOpenWeatherApiRequest.addRequestParameters(mock(OpenWeatherApiRequestParameters.class));
    when(mOpenWeatherApiRequest.getExecutor()).thenReturn(new TestExecutor());
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
    when(mModelConverter.getModel()).thenThrow(new IOException("someExec"));
  }

  private void givenInvalidRequestReturningEmpty() throws IOException {
    when(mModelConverter.getModel()).thenReturn(new ArrayList<WeatherData>());
  }

  private void thenCallbackShouldBeError() {
    assertEquals(true, mIsError);
  }

  private void givenInvalidRequestReturningNull() throws IOException {
    when(mModelConverter.getModel()).thenReturn(null);
  }

  private void whenExecuting() {
    mOpenWeatherApiRequest.execute(mTestApiCallback);
  }

  private void thenCallbackShouldBeSuccess() {
    assertEquals(true, mIsSuccess);
  }

  private void givenValidRequestReturningModel() throws IOException {
    when(mModelConverter.getModel()).thenReturn(new ArrayList<WeatherData>() {{
      add(mock(WeatherData.class));
    }});
  }
}