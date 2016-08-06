package rcgonzalezf.org.weather.location;

import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.common.BaseActivity;

import static org.mockito.Mockito.mock;

@RunWith(JMockit.class) public class LocationRetrieverTest {

  private LocationRetriever uut;

  @Mocked BaseActivity mSomeBaseActivity;
  @Mocked GoogleApiClient mGoogleApiClient;
  @Mocked LocationRetrieverListener mTestLocationRetrieverListener;

  @Before public void setUp() throws Exception {

  }

  @Test public void shouldCheckForPermissionsOnConnected() {
    givenLocationRetriever();

    whenConnected();

    thenShouldCheckForPermissions();
  }

  @Test public void shouldConnectGoogleApiClient() {
    givenGoogleApiBuilder();
    givenLocationRetriever();

    whenConnect();

    thenGoogleApiConnect();
  }

  @Test public void shouldDisconnectGoogleApiClient() {
    givenGoogleApiBuilder();
    givenLocationRetriever();

    whenDisconnect();

    thenGoogleApiDisconnect();
  }

  private void thenGoogleApiDisconnect() {
    new Verifications() {{
      mGoogleApiClient.disconnect(); times = 1;
    }};
  }

  private void whenDisconnect() {
    uut.disconnect();
  }

  private void givenGoogleApiBuilder() {
    new MockUp<GoogleApiClient.Builder>() {
      @SuppressWarnings("unused") @Mock GoogleApiClient build() {
        return mGoogleApiClient;
      }
    };
  }

  private void thenGoogleApiConnect() {
    new Verifications() {{
      mGoogleApiClient.connect(); times = 1;
    }};
  }

  private void whenConnect() {
    uut.connect();
  }

  private void thenShouldCheckForPermissions() {
    new Verifications() {{
      mTestLocationRetrieverListener.checkForPermissions(); times = 1;
    }};
  }

  private void whenConnected() {
    uut.onConnected(mock(Bundle.class));
  }

  private void givenLocationRetriever() {
    uut = new LocationRetriever(mSomeBaseActivity, mTestLocationRetrieverListener);
  }
}