package rcgonzalezf.org.weather.location;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import rcgonzalezf.org.weather.common.BaseActivity;
import rcgonzalezf.org.weather.common.PermissionChecker;
import rcgonzalezf.org.weather.common.PermissionResultListener;

@RunWith(JMockit.class) public class LocationManagerTest {

  @Tested private LocationManager uut;
  @SuppressWarnings("unused") @Mocked private View content;
  @SuppressWarnings("unused") @Mocked private BaseActivity baseActivity;
  @SuppressWarnings("unused") @Mocked private LocationRetriever locationRetriever;
  @SuppressWarnings("unused") @Mocked private PermissionChecker permissionChecker;
  private int requestCode;
  private String[] permissions;
  private int[] grantResults;
  private double lon;
  private double lat;
  private int environmentSdk;
  @org.mockito.Mock
  private LocationSearch locationSearch;

  @Before public void settingUpLocationManager() {
    MockitoAnnotations.initMocks(this);
    uut = new LocationManager(baseActivity, locationSearch, content);
    environmentSdk = LocationManager.CURRENT_SDK_INT;
  }

  @After public void setDefaultValueForSdk() {
    LocationManager.CURRENT_SDK_INT = environmentSdk;
  }

  @Test public void shouldNotifyOnLocationsPermissionGranted() {
    givenPermissionGranted(true);

    whenCheckingPermissions();

    thenShouldNotifyOnLocationPermissionGranted();
  }

  @Test public void shouldNotInteractOnLocationsPermissionGrantedWithNullBaseActivityAndContent() {
    givenPermissionChecker();

    whenCheckingPermissions(null, null);

    thenShouldNotInteractWith(permissionChecker);
  }

  @Test public void shouldNotInteractOnLocationsPermissionGrantedWithBaseActivityAndNullContent() {
    givenPermissionChecker();

    whenCheckingPermissions(baseActivity, null);

    thenShouldNotInteractWith(permissionChecker);
  }

  @Test public void shouldRequestPermissionsIfAppDoesNotHaveGrantedLocationPermission(
      @Mocked PermissionResultListener permissionResultListener) {
    givenPermissionGranted(false);

    whenCheckingPermissions();

    thenShouldRequestPermissions(permissionResultListener);
  }

  @Test public void shouldNotifyEmptyLocation(@Mocked Snackbar snackbar) {

    whenEmptyLocationReceived();

    thenShouldNotifyUser(snackbar);
  }

  @Test public void shouldNotInteractIfEmptyLocationAndNullBaseActivityAndContent(
      @Mocked Snackbar snackbar) {

    whenEmptyLocationReceived(null, null);

    thenShouldNotInteractWith(snackbar);
  }

  @Test public void shouldNotInteractIfEmptyLocationAndBaseActivityWithNullContent(
      @Mocked Snackbar snackbar) {

    whenEmptyLocationReceived(baseActivity, null);

    thenShouldNotInteractWith(snackbar);
  }

  @Test public void shouldSearchByLocationOnLocationFound() {
    givenLatLonLocation();

    whenFindingLocation();

    thenShouldSearchByLocation();
  }

  @Test public void shouldNotInteractIfNullBaseActivityOnLocationFound() {
    givenLatLonLocation();

    whenFindingLocation();

    thenShouldNotInteractWith(baseActivity);
  }

  @Test public void shouldDelegateRequestPermissionResultForMApiAndAbove() {
    givenSdk(23);
    givenPermissionChecker();
    givenPermissionResultParameters();

    whenRequestingPermissionResult();

    thenShouldDelegateOnRequestPermissionResult(true);
  }

  @Test public void shouldNotDelegateRequestPermissionResultForOlderApi() {
    givenSdk(22);
    givenPermissionChecker();
    givenPermissionResultParameters();

    whenRequestingPermissionResult();

    thenShouldDelegateOnRequestPermissionResult(false);
  }

  @Test public void shouldNotInteractIfPermissionCheckerIsNull() {
    givenPermissionResultParameters();

    whenRequestingPermissionResult();

    thenShouldNotInteractWith(permissionChecker);
  }

  @Test public void shouldDelegateConnect() {

    whenConnecting();

    thenShouldDelegateConnect();
  }

  @Test public void shouldDelegateDisconnect() {

    whenDisconnecting();

    thenShouldDelegateDisconnect();
  }

  @Test public void shouldNotifyLocationGrantedOnSuccessPermissionsSuccess() {
    PermissionResultListener permissionResultListener = givenPermissionResultListener();

    whenSuccessfullyGettingPermissions(permissionResultListener);

    thenShouldNotifyOnLocationPermissionGranted();
  }

  @Test public void shouldNotifyLocationFailureOnPermissionsFailure() {
    PermissionResultListener permissionResultListener = givenPermissionResultListener();

    whenGettingPermissionsFailure(permissionResultListener);

    thenShouldNotifyOnLocationPermissionFailure();
  }

  private void whenGettingPermissionsFailure(PermissionResultListener permissionResultListener) {
    permissionResultListener.onFailure();
  }

  private void whenSuccessfullyGettingPermissions(
      PermissionResultListener permissionResultListener) {
    permissionResultListener.onSuccess();
  }

  private PermissionResultListener givenPermissionResultListener() {
    return uut.getPermissionResultListener();
  }

  private void thenShouldNotInteractWith(Object mock) {
    new FullVerifications(mock) {
    };
  }

  private void whenDisconnecting() {
    uut.disconnect();
  }

  private void thenShouldDelegateDisconnect() {
    new Verifications() {{
      locationRetriever.disconnect();
    }};
  }

  private void thenShouldDelegateConnect() {
    new Verifications() {{
      locationRetriever.connect();
    }};
  }

  private void whenConnecting() {
    uut.connect();
  }

  private void givenPermissionChecker() {
    uut.permissionChecker = permissionChecker;
  }

  private void givenSdk(int sdk) {
    LocationManager.CURRENT_SDK_INT = sdk;
  }

  private void givenLatLonLocation() {
    lat = 1d;
    lon = 1d;
  }

  private void givenPermissionResultParameters() {
    this.requestCode = 1;
    this.permissions = new String[] { "" };
    this.grantResults = new int[] { 1 };
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void thenShouldDelegateOnRequestPermissionResult(boolean shouldDelegate) {
    if (shouldDelegate) {
      new Verifications() {{
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }};
    } else {
      new Verifications() {{
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
        times = 0;
      }};
    }
  }

  private void whenRequestingPermissionResult() {
    uut.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private void thenShouldSearchByLocation() {
    Mockito.verify(locationSearch, Mockito.atLeastOnce()).searchByLatLon(lat, lon);
  }

  private void whenFindingLocation() {
    uut.onLocationFound(lat, lon);
  }

  private void thenShouldNotifyUser(final Snackbar snackbar) {
    new Verifications() {{
      snackbar.show();
    }};
  }

  private void whenEmptyLocationReceived() {
    uut.onEmptyLocation();
  }

  private void whenEmptyLocationReceived(BaseActivity baseActivity, View content) {
    uut.onEmptyLocation(baseActivity, content);
  }

  private void thenShouldRequestPermissions(
      final PermissionResultListener permissionResultListener) {
    new Verifications() {{
      permissionChecker.requestPermission(withAny(permissionResultListener));
    }};
  }

  private void thenShouldNotifyOnLocationPermissionFailure() {
    new Verifications() {{
      locationRetriever.onLocationPermissionFailure();
    }};
  }

  private void thenShouldNotifyOnLocationPermissionGranted() {
    new Verifications() {{
      locationRetriever.onLocationPermissionsGranted();
    }};
  }

  private void whenCheckingPermissions(BaseActivity baseActivity, View content) {
    uut.checkForPermissions(baseActivity, content);
  }

  private void whenCheckingPermissions() {
    uut.checkForPermissions();
  }

  private void givenPermissionGranted(final boolean hasPermission) {
    new Expectations() {{
      permissionChecker.hasPermission();
      result = hasPermission;
    }};
  }
}
