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
import mockit.Tested;
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

  @Tested private PermissionChecker uut;

  @SuppressWarnings("unused") @Mocked private Snackbar mSnackbar;

  private int mTestRequestCode;
  private boolean mHasPermission;
  private boolean mOnSuccessCalled;
  private PermissionResultListener mPermissionListener;

  @Before public void setup() {
    Activity activityMock = mock(Activity.class);
    View containerMock = mock(View.class);

    mTestRequestCode = 1010101010;
    String somePermission = Manifest.permission.ACCESS_COARSE_LOCATION;
    int someGrantedMessageId = R.string.permissions_location_granted;
    int someNotGrantedMessageId = R.string.permissions_location_not_granted;
    int somePermissionRationaleMessageId = R.string.permissions_location_rationale;
    uut = new PermissionChecker(somePermission, activityMock, mTestRequestCode, containerMock,
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
    givenPermissionRequested(mPermissionListener);

    whenOnPermissionRequestResultGranted(true);

    thenOnSuccessShouldBeCall(true);
  }

  @Test public void shouldCallPermissionOnFailure() {
    givenPermissionResultListener();
    givenPermissionRequested(mPermissionListener);

    whenOnPermissionRequestResultGranted(false);

    thenOnSuccessShouldBeCall(false);
  }

  @Test public void shouldNotBreakTheOnRequestPermissionsResultChain() {
    givenPermissionResultListener();
    givenPermissionRequested(mPermissionListener);

    whenOnPermissionRequestResultForUnknownResultCode();

    thenOnSuccessShouldBeCall(false);
  }

  private void thenOnSuccessShouldBeCall(boolean expected) {
    assertEquals(expected, mOnSuccessCalled);
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void whenOnPermissionRequestResultForUnknownResultCode() {
    int someUnknownRequestCode = mTestRequestCode - 1;
    uut.onRequestPermissionsResult(someUnknownRequestCode, new String[] { "somePermission" },
        new int[] { PackageManager.PERMISSION_GRANTED });
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void whenOnPermissionRequestResultGranted(boolean permissionGranted) {
    uut.onRequestPermissionsResult(mTestRequestCode, new String[] { "somePermission" }, new int[] {
        permissionGranted ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED
    });
  }

  private void givenPermissionResultListener() {
    //noinspection unused
    mPermissionListener = new MockUp<PermissionResultListener>() {

      @Mock public void onSuccess() {
        mOnSuccessCalled = true;
      }

      @Mock public void onFailure() {
      }
    }.getMockInstance();
  }

  private void givenPermissionRequested(PermissionResultListener mPermissionListener) {
    uut.requestPermission(mPermissionListener);
  }

  private void thenShouldShowSnackbarWithPermissionRationaleMessage(final boolean expected) {
    new Verifications() {{
      mSnackbar.show();
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
    assertTrue(mHasPermission);
  }

  private void whenCheckingIfHasPermission() {
    mHasPermission = uut.hasPermission();
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