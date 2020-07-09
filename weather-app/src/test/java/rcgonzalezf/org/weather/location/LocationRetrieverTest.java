package rcgonzalezf.org.weather.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import org.junit.Test;
import org.junit.runner.RunWith;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import rcgonzalezf.org.weather.common.BaseActivity;
import rcgonzalezf.org.weather.utils.WeatherUtils;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(JMockit.class)
public class LocationRetrieverTest {

    private LocationRetriever uut;

    @SuppressWarnings("unused")
    @Mocked
    private BaseActivity baseActivity;
    @SuppressWarnings("unused")
    @Mocked
    private GoogleApiClient googleApiClient;
    @SuppressWarnings("unused")
    @Mocked
    private LocationRetrieverListener
            locationRetrieverListener;
    @SuppressWarnings("unused")
    @Mocked
    private LocationServices locationServices;
    @SuppressWarnings("unused")
    @Mocked
    private FusedLocationProviderApi fusedLocationProviderApi;
    private boolean logged;
    private Location lastLocation;

    @Test
    public void shouldCheckForPermissionsOnConnected() {
        givenLocationRetriever();

        whenConnected();

        thenShouldCheckForPermissions();
    }

    @Test
    public void shouldNotPerformActionOnNullListenerOnConnected() {
        givenNullLocationRetrieverListener();

        whenConnected();

        thenNoMoreInteractionAreExpected();
    }

    @Test
    public void shouldConnectGoogleApiClient() {
        givenGoogleApiBuilder();
        givenLocationRetriever();

        whenConnect();

        thenGoogleApiConnect();
    }

    @Test
    public void shouldDisconnectGoogleApiClient() {
        givenGoogleApiBuilder();
        givenLocationRetriever();

        whenDisconnect();

        thenGoogleApiDisconnect();
    }

    @Test
    public void shouldHandleOnConnectionSuspended() {
        givenLogger();
        givenLocationRetriever();

        whenConnectionSuspended();

        thenLogged();
    }

    @Test
    public void shouldHandleOnLocationPermissionFailure() {
        givenLogger();
        givenLocationRetriever();

        whenLocationPermissionFailure();

        thenLogged();
    }

    @Test
    public void shouldHandleOnConnectionFailed() {
        givenLogger();
        givenLocationRetriever();

        whenConnectionFailed();

        thenLogged();
    }

    @Test
    public void shouldInformNoInternet() {
        givenLocationRetriever();
        givenInternet(false);

        whenLocationPermissionsGranted();

        thenShouldInformNoInternet();
    }

    @Test
    public void shouldNotInteractIfNoBaseActivity() {
        givenLocationRetriever();
        givenInternet(false);

        whenTryingToGetTheLastKnowLocationWithBaseActivity(baseActivity);

        thenNoMoreInteractionAreExpected();
    }

    @Test
    public void shouldNotifyEmptyLocationListener() {
        givenLocationRetriever();
        givenInternet(true);
        givenLocationFound(false);

        whenLocationPermissionsGranted();

        thenShouldNotifyEmptyLocation();
    }

    @Test
    public void shouldNotifyOnLocationFoundListener() {
        givenLocationRetriever();
        givenInternet(true);
        givenLocationFound(true);

        whenLocationPermissionsGranted();

        thenShouldNotifyLocationFound();
    }

    @Test
    public void shouldNotInteractOnLocationFoundListenerNull() {
        givenNullLocationRetrieverListener();
        givenInternet(true);

        whenTryingToGetTheLastKnowLocationWithBaseActivity(baseActivity);

        thenNoMoreInteractionAreExpected();
    }

    @Test
    public void shouldGetLastLocation() {
        givenNullLocationRetrieverListener();

        whenGettingLastLocation();

        thenLastLocationShouldNotBeNull();
    }

    private void thenLastLocationShouldNotBeNull() {
        assertNotNull(lastLocation);
    }

    private void whenGettingLastLocation() {
        lastLocation = uut.getLastLocation();
    }

    @SuppressWarnings("MissingPermission")
    private void givenLocationFound(final boolean found) {

        final Location location = found ? mock(Location.class) : null;

        new Expectations(uut) {{
            uut.getLastLocation();
            result = location;
        }};
    }

    private void thenShouldNotifyLocationFound() {
        new Verifications() {{
            locationRetrieverListener.onLocationFound(anyDouble, anyDouble);
            times = 1;
        }};
    }

    private void thenShouldNotifyEmptyLocation() {
        new Verifications() {{
            locationRetrieverListener.onEmptyLocation();
            times = 1;
        }};
    }

    private void thenShouldInformNoInternet() {
        new Verifications() {{
            baseActivity.informNoInternet();
            times = 1;
        }};
    }

    private void givenInternet(final boolean hasInternet) {
        new MockUp<WeatherUtils>() {
            @SuppressWarnings("unused")
            @Mock
            boolean hasInternetConnection(Context context) {
                return hasInternet;
            }
        };
    }

    private void whenLocationPermissionsGranted() {
        uut.onLocationPermissionsGranted();
        verify(uut).onLocationPermissionsGranted();
    }

    private void whenConnectionFailed() {
        uut.onConnectionFailed(new ConnectionResult(0));
    }

    private void givenLogger() {
        new MockUp<Log>() {
            @SuppressWarnings("unused")
            @Mock
            int d(String tag, String msg) {
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

    private void whenLocationPermissionFailure() {
        uut.onLocationPermissionFailure();
    }

    private void thenGoogleApiDisconnect() {
        new Verifications() {{
            googleApiClient.disconnect();
            times = 1;
        }};
    }

    private void whenDisconnect() {
        uut.disconnect();
    }

    private void givenGoogleApiBuilder() {
        new MockUp<GoogleApiClient.Builder>() {
            @SuppressWarnings("unused")
            @Mock
            GoogleApiClient build() {
                return googleApiClient;
            }
        };
    }

    private void thenGoogleApiConnect() {
        new Verifications() {{
            googleApiClient.connect();
            times = 1;
        }};
    }

    private void whenConnect() {
        uut.connect();
    }

    private void thenShouldCheckForPermissions() {
        new Verifications() {{
            locationRetrieverListener.checkForPermissions();
            times = 1;
        }};
    }

    private void whenConnected() {
        uut.onConnected(mock(Bundle.class));
        verify(uut).onConnected(any(Bundle.class));
    }

    private void givenLocationRetriever() {
        uut = spy(new LocationRetriever(baseActivity, locationRetrieverListener));
    }

    private void givenNullLocationRetrieverListener() {
        uut = spy(new LocationRetriever(baseActivity, null));
    }

    private void thenNoMoreInteractionAreExpected() {
        verifyNoMoreInteractions(uut);
    }

    private void whenTryingToGetTheLastKnowLocationWithBaseActivity(BaseActivity baseActivity) {
        uut.tryToUseLastKnownLocation(baseActivity);
        verify(uut).tryToUseLastKnownLocation(any(BaseActivity.class));
    }
}
