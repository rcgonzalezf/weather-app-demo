package rcgonzalezf.org.weather.location

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import com.google.android.material.snackbar.Snackbar
import rcgonzalezf.org.weather.R
import rcgonzalezf.org.weather.common.BaseActivity
import rcgonzalezf.org.weather.common.PermissionChecker
import rcgonzalezf.org.weather.common.PermissionChecker.Companion.LOCATION
import rcgonzalezf.org.weather.common.PermissionResultListener
import java.lang.ref.WeakReference

open // for Mockito
class LocationManager(baseActivity: BaseActivity,
                      private val locationSearch: LocationSearch, content: View)
    : LocationRetrieverListener {
    private val baseActivityWeakReference: WeakReference<BaseActivity> = WeakReference(baseActivity)
    private val contentWeakReference: WeakReference<View> = WeakReference(content)
    private val locationRetriever: LocationRetriever = LocationRetriever(baseActivity, this)

    companion object {
        @JvmField
        @VisibleForTesting
        var CURRENT_SDK_INT = Build.VERSION.SDK_INT
    }

    @JvmField
    @VisibleForTesting
    var permissionChecker: PermissionChecker? = null

    override fun checkForPermissions() {
        val baseActivity = baseActivityWeakReference.get()
        val content = contentWeakReference.get()
        checkForPermissions(baseActivity, content)
    }

    override fun onEmptyLocation() {
        val baseActivity = baseActivityWeakReference.get()
        val content = contentWeakReference.get()
        onEmptyLocation(baseActivity, content)
    }

    override fun onLocationFound(lat: Double, lon: Double) {
        locationSearch.searchByLatLon(lat, lon)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                   grantResults: IntArray) {
        if (CURRENT_SDK_INT >= Build.VERSION_CODES.M) {
            permissionChecker?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    open fun connect() {
        locationRetriever.connect()
    }

    open fun disconnect() {
        locationRetriever.disconnect()
    }

    @get:VisibleForTesting
    val permissionResultListener: PermissionResultListener
        get() = object : PermissionResultListener {
            override fun onSuccess() {
                locationRetriever.onLocationPermissionsGranted()
            }

            override fun onFailure() {
                locationRetriever.onLocationPermissionFailure()
            }
        }

    @VisibleForTesting
    fun checkForPermissions(baseActivity: BaseActivity?, content: View?) {
        if (baseActivity != null && content != null) {
            permissionChecker = PermissionChecker(ACCESS_FINE_LOCATION, baseActivity,
                    LOCATION, content, R.string.permissions_location_granted,
                    R.string.permissions_location_not_granted,
                    R.string.permissions_location_rationale)
            permissionChecker?.let {
                if (it.hasPermission()) {
                    locationRetriever.onLocationPermissionsGranted()
                } else {
                    it.requestPermission(permissionResultListener)
                }
            }
        }
    }

    @VisibleForTesting
    fun onEmptyLocation(baseActivity: BaseActivity?, content: View?) {
        if (baseActivity != null && content != null) {
            Snackbar.make(content, baseActivity.getString(R.string.location_off_msg),
                    Snackbar.LENGTH_SHORT).show()
        }
    }
}
