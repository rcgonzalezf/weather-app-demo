package org.rcgonzalezf.weather.common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class) public class ServiceConfigTest {

  private ServiceConfig uut;
  private WeatherRepository mWeatherRepository;
  private ServiceConfig mRealServiceConfig;
  private WeatherProvider mDefaultProvider;

  @After public void rollbackServiceConfig() {
    ServiceConfig.setInstance(mRealServiceConfig);
  }

  @Before public void createRepository() {
    mRealServiceConfig = ServiceConfig.getInstance();
    uut = new ServiceConfig();
    ServiceConfig.setInstance(uut);
    uut.setApiKey("someKey");
    mDefaultProvider = WeatherProvider.OpenWeather;
  }

  @Test(expected = IllegalStateException.class) public void shouldThrowExceptionIfNoApiKeyIsSet() {
    givenNullApiKey();

    givenSomeKnownProvider();

    whenRetrievingRepository();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfNoProviderIsSet() {
    whenRetrievingRepository();
  }

  @Test public void shouldGetRepositoryInstanceForValidConfig() {
    givenSomeKnownProvider();

    whenRetrievingRepository();

    thenShouldHaveRepositoryInstance();
  }

  private void thenShouldHaveRepositoryInstance() {
    assertNotNull(mWeatherRepository);
  }

  private void whenRetrievingRepository() {
    mWeatherRepository = uut.getWeatherRepository();
  }

  private void givenSomeKnownProvider() {
    uut.setWeatherProvider(mDefaultProvider);
  }

  private void givenNullApiKey() {
    uut.setApiKey(null);
  }
}
