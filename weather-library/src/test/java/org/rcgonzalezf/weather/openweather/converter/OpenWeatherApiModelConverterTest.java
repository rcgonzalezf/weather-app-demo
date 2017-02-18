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
import org.rcgonzalezf.weather.openweather.model.ForecastData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherCurrentData;
import org.rcgonzalezf.weather.openweather.model.OpenWeatherForecastData;
import org.rcgonzalezf.weather.openweather.model.WeatherData;
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

  private OpenWeatherApiModelConverter uut;
  private OpenWeatherForecastData mOpenWeatherForecastData;
  private OpenWeatherCurrentData mOpenWeatherCurrentData;
  private List<ForecastData> mModel;
  private ForecastData mForecastData;
  private WeatherData mWeatherData;

  @Before public void initModelConverter() {
    uut = new OpenWeatherApiModelConverter();
    uut = spy(uut);
  }

  @Test public void shouldReturnModelWithOneCountryGivenInputStreamByCityIdMoscow()
      throws IOException {
    givenInputStreamByCityIdMoscow();
    givenForecastPojo();

    whenGenerateForecastModel();

    thenShouldHaveFortyForecastDataElements();
  }

  @Test public void shouldReturnModelWithValuesForGivenCountryInputStreamByCityIdMoscow()
      throws IOException {
    givenInputStreamByCityIdMoscow();
    givenForecastPojo();

    whenGenerateForecastModel();

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
    givenForecastPojo();

    whenGenerateForecastModel();

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
    givenForecastPojo();

    whenGenerateForecastModel();

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

  @Test public void shouldReturnEmptyForecastModelForError() throws IOException {
    givenInputStreamCityNotFound();
    givenForecastPojo();

    whenGenerateForecastModel();

    thenModelShouldBeEmpty();
  }

  @Test public void shouldReturnEmptyForecastListForNullPojo() throws IOException {
    givenInputStreamCityNotFound();

    whenGenerateForecastModel();

    thenModelShouldBeEmpty();
  }

  @Test public void shouldReturnEmptyForecastListForEmptyListValidCity() throws IOException {
    givenInputStreamCityWithEmptyForecastList();
    givenForecastPojo();

    whenGenerateForecastModel();

    thenModelShouldBeEmpty();
  }

  @Test public void shouldReturnEmptyForecastListForHttp400ListValidCity() throws IOException {
    givenInputStreamCityForecastWithHttp400();
    givenForecastPojo();

    whenGenerateForecastModel();

    thenModelShouldBeEmpty();
  }

  @Test public void shouldReturnWeatherModelWithOneCountryGivenInputStreamByCityIdPortland()
      throws IOException {
    givenInputStreamWeatherByCityIdPortland();
    givenWeatherPojo();

    whenGenerateWeatherModel();

    thenShouldHaveOneCurrentWeatherElement();
  }

  @Test public void shouldReturnEmptyCurrentWeatherListForNullPojo() throws IOException {
    givenInputStreamCityNotFound();

    whenGenerateWeatherModel();

    thenModelShouldBeEmpty();
  }

  @Test public void shouldReturnEmptyListForHttp400CurrentWeatherListValidCity()
      throws IOException {
    givenInputStreamCityCurrentWeatherWithHttp400();
    givenWeatherPojo();

    whenGenerateWeatherModel();

    thenModelShouldBeEmpty();
  }

  @Test public void shouldReturnEmptyListForEmptyCurrentWeatherListValidCity() throws IOException {
    givenInputStreamCityWithCurrentWeatherEmptyList();
    givenWeatherPojo();

    whenGenerateWeatherModel();

    thenModelShouldBeEmpty();
  }

  @Test public void shouldReturnEmptyCurrentWeatherModelForError() throws IOException {
    givenInputStreamCityNotFound();
    givenWeatherPojo();

    whenGenerateWeatherModel();

    thenModelShouldBeEmpty();
  }

  private void thenShouldHaveOneCurrentWeatherElement() {
    assertEquals(1, mModel.size());
    mForecastData = mModel.get(0);
  }

  private void givenInputStreamCityForecastWithHttp400() {
    givenJson(R.raw.moscow_forecast_non_http_400);
  }

  private void givenInputStreamCityCurrentWeatherWithHttp400() {
    givenJson(R.raw.portland_current_weather_http_400);
  }

  private void givenInputStreamCityWithEmptyForecastList() {
    givenJson(R.raw.moscow_forecast_empty_list);
  }

  private void givenInputStreamCityWithCurrentWeatherEmptyList() {
    givenJson(R.raw.portland_current_weather_empty);
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
    assertTrue(mForecastData.toString().contains(expected));
    assertEquals(expected, mForecastData.getCity().getName());
  }

  private void thenShouldHaveFortyForecastDataElements() {
    assertEquals(40, mModel.size());
    mForecastData = mModel.get(0);
  }

  private void whenGenerateForecastModel() throws IOException {
    uut.fromForecastPojo(mOpenWeatherForecastData);
    mModel = uut.getForecastModel();
  }

  private void whenGenerateWeatherModel() throws IOException {
    uut.fromWeatherPojo(mOpenWeatherCurrentData);
    mModel = uut.getWeatherModel();
  }

  private void givenInputStreamByCityIdMoscow() throws IOException {
    givenJson(R.raw.moscow_forecast);
  }

  private void givenInputStreamWeatherByCityIdPortland() throws IOException {
    givenJson(R.raw.portland_current_weather);
  }

  private void givenForecastPojo() throws UnsupportedEncodingException {
    Reader reader = new InputStreamReader(mInputStream, "UTF-8");
    mOpenWeatherForecastData = new Gson().fromJson(reader, OpenWeatherForecastData.class);
  }

  private void givenWeatherPojo() throws UnsupportedEncodingException {
    Reader reader = new InputStreamReader(mInputStream, "UTF-8");
    mOpenWeatherCurrentData = new Gson().fromJson(reader, OpenWeatherCurrentData.class);
  }
}