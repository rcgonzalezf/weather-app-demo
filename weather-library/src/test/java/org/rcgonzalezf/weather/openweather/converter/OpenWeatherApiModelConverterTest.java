package org.rcgonzalezf.weather.openweather.converter;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.BuildConfig;
import org.rcgonzalezf.weather.R;
import org.rcgonzalezf.weather.common.models.ForecastData;
import org.rcgonzalezf.weather.common.models.WeatherData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import org.rcgonzalezf.weather.tests.ConverterHelperTest;
import org.rcgonzalezf.weather.tests.WeatherTestLibApp;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, application = WeatherTestLibApp.class)
public class OpenWeatherApiModelConverterTest extends ConverterHelperTest {

  private OpenWeatherApiModelConverter mOpenWeatherApiModelConverter;
  private OpenWeatherForecastData openWeatherForecastData;
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
    givenPojo();

    whenGenerateModel();

    thenShouldHaveFortyForecastDataElements();
  }

  @Test public void shouldReturnModelWithValuesForGivenCountryInputStreamByCityIdMoscow()
      throws IOException {
    givenInputStreamByCityIdMoscow();
    givenPojo();

    whenGenerateModel();

    thenShouldHaveFortyForecastDataElements();
    thenShouldHaveWeatherListCountOf(40);
    thenCityNameShouldBe("Moscow");
    whenGettingTheFirstWeatherElement();
    thenSpeedShouldBe(3.69);
    thenDegShouldBe(186.002);
    thenTempShouldBe(290.55);
    thenHumidityShouldBe(37L);
    thenDateTimeShouldBe("2015-09-26 18:00:00");
  }

  @Test public void shouldReturnModelWithValuesForCountriesInputStreamFoundByName()
      throws IOException {
    givenInputStreamByCityNameLondon();
    givenPojo();

    whenGenerateModel();

    thenShouldHaveFortyForecastDataElements();
    thenShouldHaveWeatherListCountOf(40);
    thenCityNameShouldBe("London");
    whenGettingTheFirstWeatherElement();
    thenSpeedShouldBe(3.11);
    thenDegShouldBe(84.0032);
    thenTempShouldBe(281.49);
    thenHumidityShouldBe(91L);
    thenDateTimeShouldBe("2015-09-28 00:00:00");
  }

  @Test public void shouldReturnModelWithValuesForCountriesInputStreamFoundByLatLon()
      throws IOException {
    givenInputStreamByLatLon();
    givenPojo();

    whenGenerateModel();

    thenShouldHaveFortyForecastDataElements();
    thenShouldHaveWeatherListCountOf(40);
    thenCityNameShouldBe("Shuzenji");
    whenGettingTheFirstWeatherElement();
    thenSpeedShouldBe(0.78);
    thenDegShouldBe(185.0);
    thenTempShouldBe(288.84);
    thenHumidityShouldBe(99L);
    thenDateTimeShouldBe("2015-09-27 21:00:00");
    thenToStringShouldContain("Shuzenji");
  }

  @Test public void shouldReturnEmptyModelForError() throws IOException {
    givenInputStreamCityNotFound();
    givenPojo();

    whenGenerateModel();

    thenModelShouldBeEmpty();
  }

  private void thenToStringShouldContain(String expected) {
    assertTrue(mForecastData.toString().contains(expected));
  }

  private void thenDateTimeShouldBe(String dateTime) {
    assertEquals(dateTime, mWeatherData.getDateTime());
  }

  private void whenGettingTheFirstWeatherElement() {
    mWeatherData = mForecastData.getWeatherList().get(0);
  }

  private void thenShouldHaveWeatherListCountOf(int expected) {
    assertEquals(expected, mForecastData.getCount());
    assertEquals(1, mForecastData.getWeatherList().size());
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

  private void thenShouldHaveFortyForecastDataElements() {
    assertEquals(40, mModel.size());
    mForecastData = mModel.get(0);
  }

  private void whenGenerateModel() throws IOException {
    mOpenWeatherApiModelConverter.fromPojo(openWeatherForecastData);
    mModel = mOpenWeatherApiModelConverter.getModel();
  }

  private void givenInputStreamByCityIdMoscow() throws IOException {
    givenJson(R.raw.moscow_forecast);
  }

  private void givenPojo() throws UnsupportedEncodingException {
    Reader reader = new InputStreamReader(mInputStream,"UTF-8");
    openWeatherForecastData = new Gson().fromJson(reader,
        OpenWeatherForecastData.class);
  }

}