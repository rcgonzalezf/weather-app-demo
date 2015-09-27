package org.rcgonzalezf.weather;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherProvider;
import org.rcgonzalezf.weather.common.WeatherRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OpenWeatherApiRepositoryTest {

  private WeatherRepository mWeatherRepository;
  private ServiceConfig mServiceConfig;
  private ServiceConfig mRealServiceConfig;
  private String mCityId;

  @Before
  public void setUpServiceConfig(){
    mRealServiceConfig = ServiceConfig.getInstance();
    mServiceConfig = mock(ServiceConfig.class);
    ServiceConfig.setInstance(mServiceConfig);
    when(mServiceConfig.getApiKey()).thenReturn("someString");
    when(mServiceConfig.getWeatherProvider()).thenReturn(WeatherProvider.OpenWeather);
    mWeatherRepository = mServiceConfig.getWeatherRepository();
  }

  @After
  public void rollbackServiceConfig() {
    ServiceConfig.setInstance(mRealServiceConfig);
  }

  @Test
  public void shouldReturnOneCityForFindWithCityId() {
    givenCityId("524901");
  }

  private void givenCityId(String cityId) {
    mCityId = cityId;
  }
}
