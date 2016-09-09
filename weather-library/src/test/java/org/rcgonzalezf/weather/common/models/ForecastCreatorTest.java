package org.rcgonzalezf.weather.common.models;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.BuildConfig;
import org.rcgonzalezf.weather.tests.WeatherTestLibApp;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, application = WeatherTestLibApp.class)
public class ForecastCreatorTest {
  Parcelable.Creator<Forecast> uut = Forecast.CREATOR;
  private Forecast forecast;
  private Forecast[] forecastArray;
  private Parcel someParcel;
  private Forecast forecastCreated;

  @Before public void setup() {
    forecast = new Forecast();
  }

  @Test public void shouldCreateNewArrayWithCreator() {

    int givenExpectedSize = 2;

    whenCreatingNewArrayWithSize(givenExpectedSize);

    thenShouldCreateAnArrayWithSize(givenExpectedSize);
  }

  @Test public void shouldWriteFromParcel() {
    givenForecastWithValues(1, 1, "someCity", 1d, 1d, "someHumidity", "someDate", "someCountry", 1d,
        "someDesc");
    givenWritingToParcel();

    whenCreatingFromParcel();

    thenForecastCreatedShouldBeEqualTo(forecast);
  }

  @Test public void shouldWriteFromParcelWithNullValues() {
    givenForecastWithValues(1, 1, "someCity", 1d, 1d, null, null, "someCountry", 1d,
        "someDesc");
    givenWritingToParcel();

    whenCreatingFromParcel();

    thenForecastCreatedShouldBeEqualTo(forecast);
  }

  @Test public void shouldNotMatchGivenDifferentHumidity() {
    givenForecastWithValues(1, 1, "someCity", 1d, 1d, "someHumidity", "someDate", "someCountry", 1d,
        "someDesc");
    givenWritingToParcel();
    givenNewHumidity("newHumidity");

    whenCreatingFromParcel();

    thenForecastCreatedShouldNotBeEqualTo(forecast);
  }

  @Test public void shouldNotMatchGivenDifferentDate() {
    givenForecastWithValues(1, 1, "someCity", 1d, 1d, "someHumidity", "someDate", "someCountry", 1d,
        "someDesc");
    givenWritingToParcel();
    givenNewDate("newDate");

    whenCreatingFromParcel();

    thenForecastCreatedShouldNotBeEqualTo(forecast);
  }

  private void givenNewDate(String newDate) {
    forecast.setDateTime(newDate);
  }

  private void thenForecastCreatedShouldNotBeEqualTo(Forecast forecast) {
    assertNotEquals(forecast, forecastCreated);
  }

  private void givenNewHumidity(String newHumidity) {
    forecast.setHumidity(newHumidity);
  }

  private void thenForecastCreatedShouldBeEqualTo(Forecast expectedForecast) {
    assertEquals(expectedForecast.hashCode(), forecastCreated.hashCode());
    assertEquals(expectedForecast, forecastCreated);
  }

  private void whenCreatingFromParcel() {
    forecastCreated = uut.createFromParcel(someParcel);
    someParcel.recycle();
  }

  @TargetApi(Build.VERSION_CODES.M) private void givenWritingToParcel() {
    someParcel = Parcel.obtain();
    forecast.writeToParcel(someParcel, 0);
    someParcel.setDataPosition(0);
  }

  private void givenForecastWithValues(int weatherId, int cityId, String cityName, double speed,
      double temp, String humidity, String date, String country, double deg, String desc) {
    forecast.setWeatherId(weatherId);
    forecast.setCityId(cityId);
    forecast.setCityName(cityName);
    forecast.setSpeed(speed);
    forecast.setTemperature(temp);
    forecast.setHumidity(humidity);
    forecast.setDateTime(date);
    forecast.setCountry(country);
    forecast.setDeg(deg);
    forecast.setDescription(desc);
  }

  private void thenShouldCreateAnArrayWithSize(int expectedSize) {
    assertNotNull(expectedSize);
    assertEquals(expectedSize, forecastArray.length);
  }

  private void whenCreatingNewArrayWithSize(int givenExpectedSize) {
    forecastArray = uut.newArray(givenExpectedSize);
  }
}