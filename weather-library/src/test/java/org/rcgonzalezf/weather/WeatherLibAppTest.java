package org.rcgonzalezf.weather;

import java.io.File;
import okhttp3.OkHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class WeatherLibAppTest {

  private WeatherLibApp uut;

  @Before public void setUp() {
    uut = new WeatherLibApp(){
      @Override public OkHttpClient createOkHttpClient() {
        return mock(OkHttpClient.class);
      }

      @Override public File getCacheDir() {
        throw new RuntimeException("Testing");
      }
    };
  }

  @Test public void shouldNotCrashEnableHttpResponseCacheWithRuntimeException() {
    uut.enableHttpResponseCache();
  }
}
