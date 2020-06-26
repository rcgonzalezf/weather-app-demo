package rcgonzalezf.org.weather.common;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import androidx.core.app.ActivityCompat;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(JMockit.class) public class PermissionCheckerTest {

  @Tested private PermissionChecker uut;

  @SuppressWarnings("unused") @Mocked private Snackbar mSnackbar;

  private int mTestRequestCode;
  private boolean mHasPermission;
  private boolean mOnSuccessCalled;
  private PermissionResultListener mPermissionListener;
  private View.OnClickListener mOkClickListener;
  private boolean mOnFailureCalled;

  @Before public void setup() {
    BaseActivity activityMock = mock(BaseActivity.class);
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

    thenShouldHavePermissions(true);
  }

  @Test public void shouldReturnFalseIfAppHasPermissions() {
    givenActivityCompatWithRejectedPermission();

    whenCheckingIfHasPermission();

    thenShouldHavePermissions(false);
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

    thenShouldCallOnSuccess(true);
    thenShouldCallOnFailure(false);
  }

  @Test public void shouldCallFailureOnEmptyGrantedResults() {
    givenPermissionResultListener();
    givenPermissionRequested(mPermissionListener);

    whenOnPermissionRequestWithEmptyGrantedResults();

    thenShouldCallOnSuccess(false);
    thenShouldCallOnFailure(true);
  }

  @Test public void shouldCallPermissionOnFailure() {
    givenPermissionResultListener();
    givenPermissionRequested(mPermissionListener);

    whenOnPermissionRequestResultGranted(false);

    thenShouldCallOnSuccess(false);
    thenShouldCallOnFailure(true);
  }

  @Test public void shouldNotBreakTheOnRequestPermissionsResultChain() {
    givenPermissionResultListener();
    givenPermissionRequested(mPermissionListener);

    whenOnPermissionRequestResultForUnknownResultCode();

    thenShouldCallOnSuccess(false);
    thenShouldCallOnFailure(false);
  }

  @Test public void shouldRequestPermissionsWhenSnackBarIsClickedOnOk() {
    givenSnackBarOkClickListener();

    whenClickingOk();

    thenShouldRequestPermissions();
  }

  @Test public void shouldNotNotifyFailureOnNullListener() {
    givenUutSpied();

    whenHandlingRejectedPermissions();

    thenNoMoreInteractionsIfListenerIsNull();
  }

  @Test public void shouldNotNotifySuccessOnNullListener() {
    givenUutSpied();

    whenHandlingGrantedPermissions();

    thenNoMoreInteractionsIfListenerIsNull();
  }

  private void whenHandlingGrantedPermissions() {
    uut.handleGranted();
    verify(uut).handleGranted();
  }

  private void givenUutSpied() {
    uut = spy(uut);
  }

  private void thenNoMoreInteractionsIfListenerIsNull() {
    verifyNoMoreInteractions(uut);
  }

  private void whenHandlingRejectedPermissions() {
    uut.handleRejected();
    verify(uut).handleRejected();
  }

  private void thenShouldRequestPermissions() {
    new Verifications() {{
      uut.requestPermissions();
    }};
  }

  private void whenClickingOk() {
    mOkClickListener.onClick(null);
  }

  private void givenSnackBarOkClickListener() {
    mOkClickListener = uut.getSnackBarClickListener();
  }

  private void thenShouldCallOnSuccess(boolean expected) {
    assertEquals(expected, mOnSuccessCalled);
  }

  private void thenShouldCallOnFailure(boolean expected) {
    assertEquals(expected, mOnFailureCalled);
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

  @TargetApi(Build.VERSION_CODES.M)
  private void whenOnPermissionRequestWithEmptyGrantedResults() {
    uut.onRequestPermissionsResult(mTestRequestCode, new String[] { "somePermission" }, new int[] {});
  }

  private void givenPermissionResultListener() {
    //noinspection unused
    mPermissionListener = new MockUp<PermissionResultListener>() {

      @Mock public void onSuccess() {
        mOnSuccessCalled = true;
      }

      @Mock public void onFailure() {
        mOnFailureCalled = true;
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

  private void thenShouldHavePermissions(boolean expected) {
    assertEquals(expected, mHasPermission);
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

  private void givenActivityCompatWithRejectedPermission() {
    new MockUp<ActivityCompat>() {
      @SuppressWarnings("unused") @Mock int checkSelfPermission(Context context,
          String permission) {
        return PackageManager.PERMISSION_DENIED;
      }
    };
  }

}