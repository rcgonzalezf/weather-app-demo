package rcgonzalezf.org.weather.common;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(JMockit.class) public class PermissionCheckerTest {

  private Activity activityMock;
  private int testRequestCode;
  private View containerMock;

  @Mocked private Snackbar snackbar;
  private int someGrantedMessageId = R.string.permissions_location_granted;
  private int someNotGrantedMessageId = R.string.permissions_location_not_granted;
  private int somePermissionRationaleMessageId = R.string.permissions_location_rationale;
  private PermissionChecker uut;
  private String somePermission;
  private boolean hasPermission;
  private boolean onSuccessCalled;

  @Before public void setup() {
    activityMock = mock(Activity.class);
    containerMock = mock(View.class);

    testRequestCode = 1010101010;
    somePermission = Manifest.permission.ACCESS_COARSE_LOCATION;
    uut = new PermissionChecker(somePermission, activityMock, testRequestCode, containerMock,
        someGrantedMessageId, someNotGrantedMessageId, somePermissionRationaleMessageId);
  }

  @Test public void shouldReturnTrueIfAppHasPermissions() {
    givenActivityCompatWithSomeGrantedPermission();

    whenCheckingIfHasPermission();

    thenShouldHavePermissions();
  }

  @Test public void shouldShowPermissionRationale() {
    givenActivityCompatShouldShowPermissionRationale(true);

    whenRequestingPermission();

    thenShouldShowSnackbarWithPermissionRationaleMessage(true);
  }

  @Test public void shouldRequestPermissionDirectlyIfNotRationaleShouldBeShow() {
    givenActivityCompatShouldShowPermissionRationale(false);

    whenRequestingPermission();

    thenShouldShowSnackbarWithPermissionRationaleMessage(false);
  }

  @Test public void shouldCallPermissionOnSuccess() {
    givenPermissionResultListener();

    whenOnPermissionRequestResultGranted(true);

    thenOnSuccessShouldBeCall(true);
  }

  @Test public void shouldCallPermissionOnFailure() {
    givenPermissionResultListener();

    whenOnPermissionRequestResultGranted(false);

    thenOnSuccessShouldBeCall(false);
  }

  @Test public void shouldNotBreakTheOnRequestPermissionsResultChain() {
    givenPermissionResultListener();

    whenOnPermissionRequestResultForUnknownResultCode();

    thenOnSuccessShouldBeCall(false);
  }

  private void thenOnSuccessShouldBeCall(boolean expected) {
    assertEquals(expected, onSuccessCalled);
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void whenOnPermissionRequestResultForUnknownResultCode() {
    int someUnknownRequestCode = testRequestCode - 1;
    uut.onRequestPermissionsResult(someUnknownRequestCode, null,
        new int[] { PackageManager.PERMISSION_GRANTED });
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void whenOnPermissionRequestResultGranted(boolean permissionGranted) {
    uut.onRequestPermissionsResult(testRequestCode, null, new int[] {
        permissionGranted ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED
    });
  }

  private void givenPermissionResultListener() {
    uut.permissionResultListener = new PermissionResultListener() {
      @Override public void onSuccess() {
        onSuccessCalled = true;
      }

      @Override public void onFailure() {

      }
    };
  }

  private void thenShouldShowSnackbarWithPermissionRationaleMessage(final boolean expected) {
    new Verifications() {{
      snackbar.show();
      times = expected ? 1 : 0;
    }};
  }

  private void whenRequestingPermission() {
    uut.requestPermission(null);
  }

  private void givenActivityCompatShouldShowPermissionRationale(final boolean shouldShowRationale) {
    new MockUp<ActivityCompat>() {
      @SuppressWarnings("unused") @Mock boolean shouldShowRequestPermissionRationale(
          Activity activity, String permission) {
        return shouldShowRationale;
      }
    };
  }

  private void thenShouldHavePermissions() {
    assertTrue(hasPermission);
  }

  private void whenCheckingIfHasPermission() {
    hasPermission = uut.hasPermission();
  }

  private void givenActivityCompatWithSomeGrantedPermission() {
    new MockUp<ActivityCompat>() {
      @SuppressWarnings("unused") @Mock int checkSelfPermission(Context context,
          String permission) {
        return PackageManager.PERMISSION_GRANTED;
      }
    };
  }
}