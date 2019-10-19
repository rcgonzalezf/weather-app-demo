package org.rcgonzalezf.weather.common.models;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
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
public class WeatherInfoCreatorTest {
  Parcelable.Creator<WeatherInfo> uut = WeatherInfo.CREATOR;
  private WeatherInfo weatherInfo;
  private WeatherInfo[] weatherInfoArray;
  private Parcel someParcel;
  private WeatherInfo weatherInfoCreated;
  private int describeContentValue;

  @Before public void setup() {
    weatherInfo = new WeatherInfo();
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

    thenForecastCreatedShouldBeEqualTo(weatherInfo);
  }

  @Test public void shouldWriteFromParcelWithNullValues() {
    givenForecastWithValues(1, 1, "someCity", 1d, 1d, null, null, "someCountry", 1d,
        "someDesc");
    givenWritingToParcel();

    whenCreatingFromParcel();

    thenForecastCreatedShouldBeEqualTo(weatherInfo);
  }

  @Test public void shouldNotMatchGivenDifferentHumidity() {
    givenForecastWithValues(1, 1, "someCity", 1d, 1d, "someHumidity", "someDate", "someCountry", 1d,
        "someDesc");
    givenWritingToParcel();
    givenNewHumidity("newHumidity");

    whenCreatingFromParcel();

    thenForecastCreatedShouldNotBeEqualTo(weatherInfo);
  }

  @Test public void shouldNotMatchGivenDifferentDate() {
    givenForecastWithValues(1, 1, "someCity", 1d, 1d, "someHumidity", "someDate", "someCountry", 1d,
        "someDesc");
    givenWritingToParcel();
    givenNewDate("newDate");

    whenCreatingFromParcel();

    thenForecastCreatedShouldNotBeEqualTo(weatherInfo);
  }

  @Test public void shouldNotDescribeContent() {
    givenForecastWithValues(1, 1, "someCity", 1d, 1d, "someHumidity", "someDate", "someCountry", 1d,
        "someDesc");

    whenDescribingContentForParcelable();

    thenDescriptorShouldBe(0);
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(WeatherInfo.class)
        .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
        .verify();
  }

  private void thenDescriptorShouldBe(int expected) {
    assertEquals(expected, describeContentValue);
  }

  private void whenDescribingContentForParcelable() {
    describeContentValue = weatherInfo.describeContents();
  }

  private void givenNewDate(String newDate) {
    weatherInfo.setDateTime(newDate);
  }

  private void thenForecastCreatedShouldNotBeEqualTo(WeatherInfo weatherInfo) {
    assertNotEquals(weatherInfo, weatherInfoCreated);
  }

  private void givenNewHumidity(String newHumidity) {
    weatherInfo.setHumidity(newHumidity);
  }

  private void thenForecastCreatedShouldBeEqualTo(WeatherInfo expectedWeatherInfo) {
    assertEquals(expectedWeatherInfo.hashCode(), weatherInfoCreated.hashCode());
    assertEquals(expectedWeatherInfo, weatherInfoCreated);
  }

  private void whenCreatingFromParcel() {
    weatherInfoCreated = uut.createFromParcel(someParcel);
    someParcel.recycle();
  }

  @TargetApi(Build.VERSION_CODES.M) private void givenWritingToParcel() {
    someParcel = Parcel.obtain();
    weatherInfo.writeToParcel(someParcel, 0);
    someParcel.setDataPosition(0);
  }

  private void givenForecastWithValues(int weatherId, int cityId, String cityName, double speed,
      double temp, String humidity, String date, String country, double deg, String desc) {
    weatherInfo.setWeatherId(weatherId);
    weatherInfo.setCityId(cityId);
    weatherInfo.setCityName(cityName);
    weatherInfo.setSpeed(speed);
    weatherInfo.setTemperature(temp);
    weatherInfo.setHumidity(humidity);
    weatherInfo.setDateTime(date);
    weatherInfo.setCountry(country);
    weatherInfo.setDeg(deg);
    weatherInfo.setDescription(desc);
  }

  private void thenShouldCreateAnArrayWithSize(int expectedSize) {
    assertNotNull(expectedSize);
    assertEquals(expectedSize, weatherInfoArray.length);
  }

  private void whenCreatingNewArrayWithSize(int givenExpectedSize) {
    weatherInfoArray = uut.newArray(givenExpectedSize);
  }
}