package rcgonzalezf.org.weather.utils;

import androidx.annotation.DrawableRes;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import rcgonzalezf.org.weather.R;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class WeatherArtUtilsTest {

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        { 220, R.drawable.art_storm },
        { 300, R.drawable.art_light_rain },
        { 500, R.drawable.art_rain },
        { 511, R.drawable.art_snow },
        { 520, R.drawable.art_rain },
        { 600, R.drawable.art_snow },
        { 701, R.drawable.art_fog },
        { 761, R.drawable.art_storm },
        { 781, R.drawable.art_storm },
        { 800, R.drawable.art_clear },
        { 801, R.drawable.art_light_clouds},
        { 803, R.drawable.art_clouds },
        { 805, -1 },
        { -1, -1 },
    });
  }

  @SuppressWarnings("WeakerAccess") @Parameterized.Parameter public int weatherId;

  @SuppressWarnings("WeakerAccess") @Parameterized.Parameter(value = 1) public @DrawableRes
  int expectedDrawableWeatherArtId;

  private @DrawableRes int mDrawableWeatherArtIdResult;

  @Test public void shouldGetDrawableResourceForWeatherCondition() throws Exception {

    whenGettingResourceForWeatherCondition();

    thenDrawableIdShouldBeEqualTo(expectedDrawableWeatherArtId);
  }

  private void thenDrawableIdShouldBeEqualTo(int expectedDrawableWeatherArtId) {
    assertEquals(expectedDrawableWeatherArtId, mDrawableWeatherArtIdResult);
  }

  private void whenGettingResourceForWeatherCondition() {
    new WeatherArtUtils();
    mDrawableWeatherArtIdResult = WeatherArtUtils.getArtResourceForWeatherCondition(weatherId);
  }
}