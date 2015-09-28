package org.rcgonzalezf.weather.common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class) public class ServiceConfigTest {

  private WeatherRepository mWeatherRepository;
  private ServiceConfig mRealServiceConfig;
  private ServiceConfig mServiceConfig;
  private WeatherProvider mDefaultProvider;

  @After public void rollbackServiceConfig() {
    ServiceConfig.setInstance(mRealServiceConfig);
  }

  @Before public void createRepository() {
    mRealServiceConfig = ServiceConfig.getInstance();
    mServiceConfig = new ServiceConfig();
    ServiceConfig.setInstance(mServiceConfig);
    mServiceConfig.setApiKey("someKey");
    mDefaultProvider = WeatherProvider.OpenWeather;
  }

  @Test(expected = IllegalStateException.class) public void shouldThrowExceptionIfNoApiKeyIsSet() {
    givenNullApiKey();
    givenSomeProvider();
    whenRetrievingRepository();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfNoProviderIsSet() {
    whenRetrievingRepository();
  }

  @Test public void shouldGetRepositoryInstanceForValidConfig() {
    givenSomeProvider();
    whenRetrievingRepository();
    thenShouldHaveRepositoryInstance();
  }

  private void thenShouldHaveRepositoryInstance() {
    assertNotNull(mWeatherRepository);
  }

  private void whenRetrievingRepository() {
    mWeatherRepository = mServiceConfig.getWeatherRepository();
  }

  private void givenSomeProvider() {
    mServiceConfig.setWeatherProvider(mDefaultProvider);
  }

  private void givenNullApiKey() {
    mServiceConfig.setApiKey(null);
  }
}
