package org.rcgonzalezf.weather.openweather.converter;

import java.io.IOException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.BuildConfig;
import org.rcgonzalezf.weather.R;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.models.WeatherData;
import org.rcgonzalezf.weather.tests.ConverterHelperTest;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricGradleTestRunner.class) @Config(constants = BuildConfig.class, sdk = 21)
public class OpenWeatherApiModelConverterTest extends ConverterHelperTest {

  private OpenWeatherApiModelConverter mOpenWeatherApiModelConverter;
  private List<ForecastData> mModel;
  private ForecastData mForecastData;
  private WeatherData mWeatherData;

  @Before public void initModelConverter() {
    mOpenWeatherApiModelConverter = new OpenWeatherApiModelConverter();
    mOpenWeatherApiModelConverter = spy(mOpenWeatherApiModelConverter);
  }

  @Test public void shouldReturnModelWithOneCountryGivenInputStreamByCityIdMoscow()
      throws IOException {
    givenInputStreamByCityIdMoscow();
    whenGenerateModel();
    thenShouldHaveOneForecastDataElement();
  }

  @Test public void shouldReturnModelWithValuesForGivenCountryInputStreamByCityIdMoscow()
      throws IOException {
    givenInputStreamByCityIdMoscow();
    whenGenerateModel();
    thenShouldHaveOneForecastDataElement();
    thenShouldHaveWeatherListCountOf(40);
    thenCityNameShouldBe("Moscow");
    whenGettingTheFirstWeatherElement();
    thenSpeedShouldBe(3.69);
    thenDegShouldBe(186.002);
    thenTempShouldBe(290.55);
    thenHumidityShouldBe(37L);
  }

  private void whenGettingTheFirstWeatherElement() {
    mWeatherData = mForecastData.getWeatherList().get(0);
  }

  private void thenShouldHaveWeatherListCountOf(int expected) {
    assertEquals(expected, mForecastData.getCount());
    assertEquals(expected, mForecastData.getWeatherList().size());
  }

  @Test public void shouldReturnModelWithValuesForCountriesInputStreamFoundByName()
      throws IOException {
    givenInputStreamByCityNameLondon();
    whenGenerateModel();
    thenShouldHaveOneForecastDataElement();
    thenShouldHaveWeatherListCountOf(40);
    thenCityNameShouldBe("London");
    whenGettingTheFirstWeatherElement();
    thenSpeedShouldBe(3.11);
    thenDegShouldBe(84.0032);
    thenTempShouldBe(281.49);
    thenHumidityShouldBe(91L);
  }

  @Test public void shouldReturnModelWithValuesForCountriesInputStreamFoundByLatLon()
      throws IOException {
    givenInputStreamByLatLon();
    whenGenerateModel();
    thenShouldHaveOneForecastDataElement();
    thenShouldHaveWeatherListCountOf(40);
    thenCityNameShouldBe("Shuzenji");
    whenGettingTheFirstWeatherElement();
    thenSpeedShouldBe(0.78);
    thenDegShouldBe(185.0);
    thenTempShouldBe(288.84);
    thenHumidityShouldBe(99L);
  }

  @Test public void shouldReturnEmptyModelForError()
      throws IOException {
    givenInputStreamCityNotFound();
    whenGenerateModel();
    thenModelShouldBeEmpty();
  }

  private void thenModelShouldBeEmpty() {
    assertTrue(mModel.isEmpty());
  }

  private void givenInputStreamCityNotFound() {
    givenJson(R.raw.error_no_city);
  }

  private void givenInputStreamByLatLon() {
    givenJson(R.raw.shunzenji_forecast_by_latlon);
  }

  private void givenInputStreamByCityNameLondon() {
    givenJson(R.raw.london_forecast_by_name_type_like);
  }

  private void thenHumidityShouldBe(long expected) {
    assertEquals(expected, mWeatherData.getHumidity());
  }

  private void thenTempShouldBe(double expected) {
    assertEquals(expected, mWeatherData.getTemp(), 0.1);
  }

  private void thenDegShouldBe(double expected) {
    assertEquals(expected, mWeatherData.getDeg(), 0.1);
  }

  private void thenSpeedShouldBe(double expected) {
    assertEquals(expected, mWeatherData.getSpeed(), 0.1);
  }

  private void thenCityNameShouldBe(String expected) {
    assertEquals(expected, mForecastData.getCity().getName());
  }

  private void thenShouldHaveOneForecastDataElement() {
    assertEquals(1, mModel.size());
    mForecastData = mModel.get(0);
  }

  private void whenGenerateModel() throws IOException {
    mOpenWeatherApiModelConverter.fromInputStream(mInputStream);
    mModel = mOpenWeatherApiModelConverter.getModel();
  }

  private void givenInputStreamByCityIdMoscow() throws IOException {
    givenJson(R.raw.moscow_forecast);
  }
}