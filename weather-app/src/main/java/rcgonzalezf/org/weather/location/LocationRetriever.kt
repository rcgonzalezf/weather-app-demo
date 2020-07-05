package rcgonzalezf.org.weather.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationServices
import rcgonzalezf.org.weather.common.BaseActivity
import rcgonzalezf.org.weather.utils.WeatherUtils
import java.lang.ref.WeakReference

internal open class LocationRetriever(baseActivity: BaseActivity,
                                      locationRetrieverListener: LocationRetrieverListener?) : ConnectionCallbacks, OnConnectionFailedListener {
    private val googleApiClient: GoogleApiClient
            = GoogleApiClient.Builder(baseActivity).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
    private val weakBaseActivity: WeakReference<BaseActivity> = WeakReference(baseActivity)
    private val weakLocationRetrieverListener: WeakReference<LocationRetrieverListener?>
            = WeakReference(locationRetrieverListener)

    companion object {
        private val TAG = LocationRetriever::class.java.simpleName
    }

    override fun onConnected(bundle: Bundle?) {
        val locationRetrieverListener = weakLocationRetrieverListener.get()
        locationRetrieverListener?.checkForPermissions()
    }

    override fun onConnectionSuspended(i: Int) {
        Log.d(TAG, "Google Location onConnectionSuspended $i")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "Google Location onConnectionFailed ${connectionResult.errorMessage}")
    }

    fun onLocationPermissionFailure() {
        Log.d(TAG, "Location permission failure")
    }

    fun connect() {
        googleApiClient.connect()
    }

    fun disconnect() {
        googleApiClient.disconnect()
    }

    open fun onLocationPermissionsGranted() {
        val baseActivity = weakBaseActivity.get()
        tryToUseLastKnownLocation(baseActivity)
    }

    @VisibleForTesting
    open fun tryToUseLastKnownLocation(baseActivity: BaseActivity?) {
        val locationRetrieverListener = weakLocationRetrieverListener.get()
        if (baseActivity != null) {
            if (!WeatherUtils.hasInternetConnection(baseActivity)) {
                baseActivity.informNoInternet()
            } else locationRetrieverListener?.let { useLastLocation(it) }
        }
    }

    private fun useLastLocation(locationRetrieverListener: LocationRetrieverListener) {
        val mLastLocation = lastLocation
        @Suppress("SENSELESS_COMPARISON")
        if (mLastLocation != null) {
            val lat = mLastLocation.latitude
            val lon = mLastLocation.longitude
            locationRetrieverListener.onLocationFound(lat, lon)
        } else {
            locationRetrieverListener.onEmptyLocation()
        }
    }

    @get:VisibleForTesting
    val lastLocation: Location
        // The app is handling the potential missing permission
        @SuppressLint("MissingPermission")
        get() = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
}
