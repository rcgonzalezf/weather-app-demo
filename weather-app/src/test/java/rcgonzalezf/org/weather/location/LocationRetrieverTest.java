package rcgonzalezf.org.weather.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.common.BaseActivity;
import rcgonzalezf.org.weather.utils.WeatherUtils;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(JMockit.class) public class LocationRetrieverTest {

  private LocationRetriever uut;

  @SuppressWarnings("unused") @Mocked private BaseActivity mSomeBaseActivity;
  @SuppressWarnings("unused") @Mocked private GoogleApiClient mGoogleApiClient;
  @SuppressWarnings("unused") @Mocked private LocationRetrieverListener
      mTestLocationRetrieverListener;
  @SuppressWarnings("unused") @Mocked private LocationServices mLocationServices;
  @SuppressWarnings("unused") @Mocked private FusedLocationProviderApi mFusedLocationProviderApi;
  private boolean logged;

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

  @Test public void shouldHandleOnConnectionSuspended() {
    givenLogger();
    givenLocationRetriever();

    whenConnectionSuspended();

    thenLogged();
  }

  @Test public void shouldHandleOnConnectionFailed() {
    givenLogger();
    givenLocationRetriever();

    whenConnectionFailed();

    thenLogged();
  }

  @Test public void shouldInformNoInternet() {
    givenLocationRetriever();
    givenInternet(false);

    whenLocationPermissionsGranted();

    thenShouldInformNoInternet();
  }

 @Test public void shouldNotifyEmptyLocationListener() {
    givenLocationRetriever();
    givenInternet(true);
    givenLocationFound(false);

    whenLocationPermissionsGranted();

    thenShouldNotifyEmptyLocation();
  }

  @Test public void shouldNotifyOnLocationFoundListener() {
    givenLocationRetriever();
    givenInternet(true);
    givenLocationFound(true);

    whenLocationPermissionsGranted();

    thenShouldNotifyLocationFound();
  }

  @SuppressWarnings("MissingPermission") private void givenLocationFound(final boolean found) {

    final Location location = found ? mock(Location.class) : null;

    new Expectations(uut) {{
      uut.getLastLocation(); result = location;
    }};
  }

  private void thenShouldNotifyLocationFound() {
    new Verifications() {{
      mTestLocationRetrieverListener.onLocationFound(anyDouble, anyDouble);
      times = 1;
    }};
  }

  private void thenShouldNotifyEmptyLocation() {
    new Verifications() {{
      mTestLocationRetrieverListener.onEmptyLocation();
      times = 1;
    }};
  }

  private void thenShouldInformNoInternet() {
    new Verifications() {{
      mSomeBaseActivity.informNoInternet();
      times = 1;
    }};
  }

  private void givenInternet(final boolean hasInternet) {
    new MockUp<WeatherUtils>() {
      @SuppressWarnings("unused") @Mock boolean hasInternetConnection(Context context) {
        return hasInternet;
      }
    };
  }

  private void whenLocationPermissionsGranted() {
    uut.onLocationPermissionsGranted();
  }

  private void whenConnectionFailed() {
    uut.onConnectionFailed(new ConnectionResult(0));
  }

  private void givenLogger() {
    new MockUp<Log>() {
      @SuppressWarnings("unused") @Mock int d(String tag, String msg) {
        logged = true;
        return 0;
      }
    };
  }

  private void thenLogged() {
    assertTrue(logged);
  }

  private void whenConnectionSuspended() {
    uut.onConnectionSuspended(0);
  }

  private void thenGoogleApiDisconnect() {
    new Verifications() {{
      mGoogleApiClient.disconnect();
      times = 1;
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
      mGoogleApiClient.connect();
      times = 1;
    }};
  }

  private void whenConnect() {
    uut.connect();
  }

  private void thenShouldCheckForPermissions() {
    new Verifications() {{
      mTestLocationRetrieverListener.checkForPermissions();
      times = 1;
    }};
  }

  private void whenConnected() {
    uut.onConnected(mock(Bundle.class));
  }

  private void givenLocationRetriever() {
    uut = new LocationRetriever(mSomeBaseActivity, mTestLocationRetrieverListener);
  }
}
