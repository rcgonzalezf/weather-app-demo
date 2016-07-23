package org.rcgonzalezf.weather.tests;

import android.support.annotation.RawRes;
import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.BuildConfig;
import org.rcgonzalezf.weather.WeatherLibApp;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 21)
public abstract class ConverterHelperTest {

  protected InputStream mInputStream;

  @After public void releaseInputStream() {
    if (mInputStream != null) {
      try {
        mInputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  protected void givenJson(@RawRes int jsonResId) {
    mInputStream = WeatherLibApp.getInstance().getResources().openRawResource(jsonResId);
    assertNotNull(mInputStream);
  }
}
