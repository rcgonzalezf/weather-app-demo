package rcgonzalezf.org.weather.utils;

import android.content.Context;
import java.util.Arrays;
import java.util.Collection;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import rcgonzalezf.org.weather.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class) public class WeatherWindUtilsTest {

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        { 0, "Wind: 3.0 km/h N" },
        { 337.5, "Wind: 3.0 km/h N" },
        { 22.5, "Wind: 3.0 km/h NE" },
        { 67.5, "Wind: 3.0 km/h E" },
        { 112.5, "Wind: 3.0 km/h SE" },
        { 157.5, "Wind: 3.0 km/h S" },
        { 202.5, "Wind: 3.0 km/h SW" },
        { 247.5, "Wind: 3.0 km/h W" },
        { 292.5, "Wind: 3.0 km/h NW" },
        { 337.4, "Wind: 3.0 km/h NW" },
        { -1d, "Wind: 3.0 km/h N" },
        { 361d, "Wind: 3.0 km/h N" },
    });
  }

  @Parameterized.Parameter public double windDirStr;

  @Parameterized.Parameter(value = 1) public String expectedWindFormattedStr;

  @Mocked private Context mContext;

  private double mWindSpeedStr = 3d;
  private String mFormattedWindStr;

  @Test public void shouldGetFormattedWindDirectionString() throws Exception {
    givenWindResourcesString();

    whenGettingFormattedWind();

    thenFormattedStringShouldNotBeNull();
    thenFormattedStringShouldBeExpected();
  }

  private void givenWindResourcesString() {
    new Expectations() {{
      mContext.getString(R.string.wind); result = "Wind";
    }};
  }

  private void thenFormattedStringShouldBeExpected() {
    assertEquals(expectedWindFormattedStr, mFormattedWindStr);
  }

  private void thenFormattedStringShouldNotBeNull() {
    assertNotNull(mFormattedWindStr);
  }

  private void whenGettingFormattedWind() {
    new WeatherWindUtils();
    mFormattedWindStr = WeatherWindUtils.getFormattedWind(mContext, mWindSpeedStr, windDirStr);
  }
}
