package org.rcgonzalezf.weather.openweather.network;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.rcgonzalezf.weather.common.network.ApiCallback;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class) public class OpenWeatherApiRequestTest {

  private OpenWeatherApiRequest mOpenWeatherApiRequest;
  private ApiCallback<OpenWeatherApiResponse, OpenWeatherApiError> mTestApiCallback;
  private boolean mIsSuccess;
  private boolean mIsError;

  @Before public void createApiRequestBaseObject() {
    mOpenWeatherApiRequest = new OpenWeatherApiRequest("someApiKey");
    mTestApiCallback = new ApiCallback<OpenWeatherApiResponse, OpenWeatherApiError>() {

      @Override public void onSuccess(OpenWeatherApiResponse apiResponse) {
        mIsSuccess = true;
      }

      @Override public void onError(OpenWeatherApiError apiError) {
        mIsError = true;
      }
    };
  }

  @Test public void shouldNotifySuccess() throws Exception {
    givenValidRequestParameters();
    whenExecuting();
    thenCallbackShouldBeSuccess();
  }

  @Test public void shouldNotifyOnError() throws Exception {
    givenInvalidRequestParameters();
    whenExecuting();
    thenCallbackShouldBeError();
  }

  private void thenCallbackShouldBeError() {
    assertEquals(true, mIsError);
  }

  private void givenInvalidRequestParameters() {
    mOpenWeatherApiRequest.addRequestParameters(mock(OpenWeatherApiRequestParameters.class));
  }

  private void whenExecuting() {
    mOpenWeatherApiRequest.execute(mTestApiCallback);
  }

  private void thenCallbackShouldBeSuccess() {
    assertEquals(true, mIsSuccess);
  }

  private void givenValidRequestParameters() {
    mOpenWeatherApiRequest.addRequestParameters(mock(OpenWeatherApiRequestParameters.class));
  }
}