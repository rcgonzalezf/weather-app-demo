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
import rcgonzalezf.org.weather.common.BaseActivity;
import rcgonzalezf.org.weather.common.PermissionChecker;
import rcgonzalezf.org.weather.common.PermissionResultListener;

@RunWith(JMockit.class) public class LocationManagerTest {

  @Tested private LocationManager uut;
  @SuppressWarnings("unused") @Mocked private View mContent;
  @SuppressWarnings("unused") @Mocked private BaseActivity mBaseActivity;
  @SuppressWarnings("unused") @Mocked private LocationRetriever mLocationRetriever;
  @SuppressWarnings("unused") @Mocked private PermissionChecker mPermissionChecker;
  private int mRequestCode;
  private String[] mPermissions;
  private int[] mGrantResults;
  private double mLon;
  private double mLat;
  private int mEnvironmentSdk;

  @Before public void settingUpLocationManager() {
    uut = new LocationManager(mBaseActivity, mContent);
    mEnvironmentSdk = LocationManager.CURRENT_SDK_INT;
  }

  @After public void setDefaultValueForSdk() {
    LocationManager.CURRENT_SDK_INT = mEnvironmentSdk;
  }

  @Test public void shouldNotifyOnLocationsPermissionGranted() {
    givenPermissionGranted(true);

    whenCheckingPermissions();

    thenShouldNotifyOnLocationPermissionGranted();
  }

  @Test public void shouldNotInteractOnLocationsPermissionGrantedWithNullBaseActivityAndContent() {
    givenPermissionChecker();

    whenCheckingPermissions(null, null);

    thenShouldNotInteractWith(mPermissionChecker);
  }

  @Test public void shouldNotInteractOnLocationsPermissionGrantedWithBaseActivityAndNullContent() {
    givenPermissionChecker();

    whenCheckingPermissions(mBaseActivity, null);

    thenShouldNotInteractWith(mPermissionChecker);
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

    whenEmptyLocationReceived(mBaseActivity, null);

    thenShouldNotInteractWith(snackbar);
  }

  @Test public void shouldSearchByLocationOnLocationFound() {
    givenLatLonLocation();

    whenFindingLocation();

    thenShouldSearchByLocation();
  }

  @Test public void shouldNotInteractIfNullBaseActivityOnLocationFound() {
    givenLatLonLocation();

    whenFindingLocation(null);

    thenShouldNotInteractWith(mBaseActivity);
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

    thenShouldNotInteractWith(mPermissionChecker);
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
      mLocationRetriever.disconnect();
    }};
  }

  private void thenShouldDelegateConnect() {
    new Verifications() {{
      mLocationRetriever.connect();
    }};
  }

  private void whenConnecting() {
    uut.connect();
  }

  private void givenPermissionChecker() {
    uut.permissionChecker = mPermissionChecker;
  }

  private void givenSdk(int sdk) {
    LocationManager.CURRENT_SDK_INT = sdk;
  }

  private void givenLatLonLocation() {
    mLat = 1d;
    mLon = 1d;
  }

  private void givenPermissionResultParameters() {
    this.mRequestCode = 1;
    this.mPermissions = new String[] { "" };
    this.mGrantResults = new int[] { 1 };
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void thenShouldDelegateOnRequestPermissionResult(boolean shouldDelegate) {
    if (shouldDelegate) {
      new Verifications() {{
        mPermissionChecker.onRequestPermissionsResult(mRequestCode, mPermissions, mGrantResults);
      }};
    } else {
      new Verifications() {{
        mPermissionChecker.onRequestPermissionsResult(mRequestCode, mPermissions, mGrantResults);
        times = 0;
      }};
    }
  }

  private void whenRequestingPermissionResult() {
    uut.onRequestPermissionsResult(mRequestCode, mPermissions, mGrantResults);
  }

  private void thenShouldSearchByLocation() {
    new Verifications() {{
      mBaseActivity.searchByLocation(mLat, mLon);
    }};
  }

  private void whenFindingLocation() {
    uut.onLocationFound(mLat, mLon);
  }

  private void whenFindingLocation(BaseActivity baseActivity) {
    uut.onLocationFound(baseActivity, mLat, mLon);
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
      mPermissionChecker.requestPermission(withAny(permissionResultListener));
    }};
  }

  private void thenShouldNotifyOnLocationPermissionFailure() {
    new Verifications() {{
      mLocationRetriever.onLocationPermissionFailure();
    }};
  }

  private void thenShouldNotifyOnLocationPermissionGranted() {
    new Verifications() {{
      mLocationRetriever.onLocationPermissionsGranted();
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
      mPermissionChecker.hasPermission();
      result = hasPermission;
    }};
  }
}
