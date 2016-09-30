package org.rcgonzalezf.weather.openweather.network;

import org.junit.Test;
import org.meanbean.test.BeanTester;

public class OpenWeatherApiResponseTest {

  @Test
  public void getterAndSetterCorrectness() throws Exception {
    new BeanTester().testBean(OpenWeatherApiResponse.class);
  }
}