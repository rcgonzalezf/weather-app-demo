package org.rcgonzalezf.weather.openweather.converter;

import java.io.IOException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.BuildConfig;
import org.rcgonzalezf.weather.R;
import org.rcgonzalezf.weather.common.models.WeatherData;
import org.rcgonzalezf.weather.tests.ConverterHelperTest;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricGradleTestRunner.class) @Config(constants = BuildConfig.class, sdk = 21)
public class OpenWeatherApiModelConverterTest extends ConverterHelperTest {

  private OpenWeatherApiModelConverter mOpenWeatherApiModelConverter;
  private List<WeatherData> mModel;

  @Before public void initModelConverter() {
    mOpenWeatherApiModelConverter = new OpenWeatherApiModelConverter();
    mOpenWeatherApiModelConverter = spy(mOpenWeatherApiModelConverter);
  }

  @Test public void shouldReturnModelWithOneCountryGivenInputStreamByCityIdMoscow()
      throws IOException {
    givenInputStreamByCityIdMoscow();
    whenGenerateModel();
    thenShouldHaveOneElement();
  }

  private void thenShouldHaveOneElement() {
    assertEquals(1, mModel.size());
  }

  private void whenGenerateModel() throws IOException {
    mOpenWeatherApiModelConverter.fromInputStream(mInputStream);
     mModel = mOpenWeatherApiModelConverter.getModel();
  }

  private void givenInputStreamByCityIdMoscow() throws IOException {
    givenJson(R.raw.moscow_forecast);
  }
}