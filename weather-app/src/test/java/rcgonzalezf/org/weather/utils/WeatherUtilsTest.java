package rcgonzalezf.org.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.StringRes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JMockit.class) public class WeatherUtilsTest {

  private boolean mHasInternetConnection;
  private String mDateStr;
  private String mDateFormatted;
  private double mTemperature;
  private boolean mIsCelsius;
  private String mSymbol;
  private String mFormattedTemperature;
  private Date mTodayDate;
  private String mDayName;

  @Test public void shouldHaveInternetConnection(@Mocked Context context,
      @Mocked ConnectivityManager connectivityManager, @Mocked NetworkInfo networkInfo) {

    givenConnectivityManager(context, connectivityManager);
    givenIsConnected(networkInfo);

    whenCheckingForInternetConnection(context);

    thenShouldHaveInternetConnection(true);
  }


  @Test public void shouldNotHaveInternetConnectionIfNoNetworkInfo(@Mocked Context context,
      @Mocked ConnectivityManager connectivityManager) {

    givenConnectivityManager(context, connectivityManager);
    givenNullNetworkInfo(connectivityManager);

    whenCheckingForInternetConnection(context);

    thenShouldHaveInternetConnection(false);
  }

  @Test public void shouldNotHaveInternetConnectionIfIsNotConnecting(@Mocked Context context,
      @Mocked ConnectivityManager connectivityManager) {

    givenConnectivityManager(context, connectivityManager);

    whenCheckingForInternetConnection(context);

    thenShouldHaveInternetConnection(false);
  }

  @Test public void shouldReturnSameInputDateStringForInvalidFormat() {
    givenDateStr("Febrero Loco y Marzo Otro Poco");

    whenFormattingTheDateStr();

    thenFormattedDateStringShouldBeEqualToDateStr();
  }


  @Test public void shouldReturnFormattedStringForInputDateString() {
    givenDateStr("2015-09-26 18:00:00");

    whenFormattingTheDateStr();

    thenFormattedDateStringShouldBeEqualTo("Sep, 26 18:00");
  }

  @Test public void shouldReturnFormattedTemperatureInFahrenheit() {
    givenTemperature(288.79d);
    givenIsCelsius(false);
    givenSymbol("F");

    whenFormattingTemperature();

    thenFormattedTemperatureShouldBe("60° F");
  }

  @Test public void shouldReturnFormattedTemperatureInCelsius() {
    givenTemperature(288.79d);
    givenIsCelsius(true);
    givenSymbol("C");

    whenFormattingTemperature();

    thenFormattedTemperatureShouldBe("16° C");
  }

  @Test public void shouldReturnToday(@Mocked Context context) {
    givenTodayDateAs2015Sep26();
    givenDateStr("2015-09-26 18:00:00");

    whenGettingDayNamePassingTodayDate(context);

    thenShouldUseResource(R.string.today, context);
  }

  @Test public void shouldReturnTomorrow(@Mocked Context context) {
    givenTodayDateAs2015Sep26();
    givenDateStr("2015-09-27 18:00:00");

    whenGettingDayNamePassingTodayDate(context);

    thenShouldUseResource(R.string.tomorrow, context);
  }

  @Test public void shouldReturnDayOfWeek(@Mocked Context context) {
    givenTodayDateAs2015Sep26();
    givenDateStr("2012-09-24 18:00:00");

    whenGettingDayNamePassingTodayDate(context);

    thenShouldFormatWithEEEE();
    thenDayNameShouldNotBeNull();
    thenDayNameShouldEqual("Monday");
  }

  @Test public void shouldReturnSameInputForAnInvalidDate(@Mocked Context context) {
    givenTodayDateAs2015Sep26();
    givenDateStr("Febrero Loco y Marzo Otro Poco");

    whenGettingDayNamePassingTodayDate(context);

    thenDayNameShouldNotBeNull();
    thenDayNameShouldEqual("Febrero Loco y Marzo Otro Poco");
  }

  @Test public void shouldGetDayName(@Mocked Context context) {
    givenTodayDateAs2015Sep26();
    givenDateStr("Febrero Loco y Marzo Otro Poco");

    whenGettingDayName(context);

    thenDayNameShouldNotBeNull();
    thenDayNameShouldEqual("Febrero Loco y Marzo Otro Poco");
  }

  private void thenDayNameShouldEqual(String expectedDayName) {
    assertEquals(expectedDayName, mDayName);
  }

  private void thenDayNameShouldNotBeNull() {
    assertNotNull(mDayName);
  }

  private void thenShouldFormatWithEEEE() {
    new Verifications() {{
      new SimpleDateFormat(withEqual("EEEE"), Locale.getDefault());
    }};
  }

  private void thenShouldUseResource(@StringRes final int stringRes, final Context context) {
    new Verifications() {{
      context.getString(stringRes);
    }};
  }

  private void whenGettingDayName(Context context) {
    mDayName = WeatherUtils.getDayName(context, mDateStr);
  }

  private void whenGettingDayNamePassingTodayDate(Context context) {
    mDayName = WeatherUtils.getDayName(context, mDateStr, mTodayDate);
  }

  private void givenTodayDateAs2015Sep26() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2015, 8, 26);
    mTodayDate = calendar.getTime();
  }

  private void thenFormattedTemperatureShouldBe(String expected) {
    assertEquals(expected, mFormattedTemperature);
  }

  private void whenFormattingTemperature() {
    mFormattedTemperature = WeatherUtils.formatTemperature(mTemperature, mIsCelsius, mSymbol);
  }

  private void givenSymbol(String symbol) {
    mSymbol = symbol;
  }

  private void givenIsCelsius(boolean isCelsius) {
    mIsCelsius = isCelsius;
  }

  private void givenTemperature(double temperature) {
    mTemperature = temperature;
  }

  private void thenFormattedDateStringShouldBeEqualTo(String expected) {
    assertEquals(expected , mDateFormatted);
  }

  private void thenFormattedDateStringShouldBeEqualToDateStr() {
    assertEquals(mDateFormatted, mDateStr);
  }

  private void whenFormattingTheDateStr() {
    this.mDateFormatted = WeatherUtils.formatDate(mDateStr);
  }

  private void givenDateStr(String dateStr) {
    this.mDateStr = dateStr;
  }

  private void givenNullNetworkInfo(final ConnectivityManager connectivityManager) {
    new Expectations() {{
      connectivityManager.getActiveNetworkInfo(); result = null;
    }};
  }

  private void givenIsConnected(final NetworkInfo networkInfo) {
    new Expectations() {{
      networkInfo.isConnectedOrConnecting(); result = true;
    }
    };
  }

  private void givenConnectivityManager(final Context context,
      final ConnectivityManager connectivityManager) {
    new Expectations() {{
      context.getSystemService(Context.CONNECTIVITY_SERVICE);
      result = connectivityManager;
    }};
  }

  private void thenShouldHaveInternetConnection(boolean expected) {
    assertEquals(expected, mHasInternetConnection);
  }

  private void whenCheckingForInternetConnection(Context context) {
    mHasInternetConnection = WeatherUtils.hasInternetConnection(context);
  }
}