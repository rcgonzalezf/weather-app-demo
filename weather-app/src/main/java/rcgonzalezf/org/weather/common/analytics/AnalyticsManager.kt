package rcgonzalezf.org.weather.common.analytics

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.VisibleForTesting
import rcgonzalezf.org.weather.BuildConfig
import rcgonzalezf.org.weather.utils.WeatherUtils.isXLargeTablet
import java.util.HashSet

class AnalyticsManager(private val mContext: Context) {
    private val analyticsObservers: MutableSet<AnalyticsObserver> = HashSet()
    private val analyticsBaseData: AnalyticsBaseData = AnalyticsBaseData()
    private var screenName: String? = null

    init {
        analyticsBaseData.data()[ANDROID_VERSION] = BUILD_ANDROID_VERSION
        analyticsBaseData.data()[APP_VERSION] = BUILD_APP_VERSION
        analyticsBaseData.data()[MULTIPANE] = java.lang.Boolean.toString(isMultipane)
    }

    companion object {
        const val MOBILE = "Mobile"
        const val UNKNOWN = "Unknown"
        const val NONE = "None"
        const val WIFI = "Wifi"
        const val ANDROID_VERSION = "ANDROID_VERSION"
        const val APP_VERSION = "APP_VERSION"
        const val NETWORK = "NETWORK"
        const val MULTIPANE = "MULTIPANE"

        @JvmField
        @VisibleForTesting
        var BUILD_ANDROID_VERSION = Build.VERSION.RELEASE

        @JvmField
        @VisibleForTesting
        var BUILD_APP_VERSION = BuildConfig.VERSION_NAME
    }

    fun addObserver(analyticsObserver: AnalyticsObserver) {
        analyticsObservers.add(analyticsObserver)
    }

    private val isMultipane: Boolean
        get() = isXLargeTablet(mContext)

    fun notifyOnScreenLoad(screenName: String) {
        this.screenName = screenName
        addBaseData()
        for (analyticsObserver in analyticsObservers) {
            analyticsObserver.onScreen(screenName, analyticsBaseData)
        }
    }

    @get:NetworkType
    private val networkType: String
        get() {
            val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            val networkType: String
            networkType = if (activeNetwork != null) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                    WIFI
                } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    MOBILE
                } else {
                    UNKNOWN
                }
            } else {
                NONE
            }
            return networkType
        }

    fun removeObserver(analyticsObserver: AnalyticsObserver?) {
        analyticsObservers.remove(analyticsObserver)
    }

    fun notifyOnAction(analyticsEvent: AnalyticsEvent?) {
        addBaseData()
        for (analyticsObserver in analyticsObservers) {
            analyticsObserver.onAction(analyticsEvent!!, screenName!!, analyticsBaseData)
        }
    }

    private fun addBaseData() {
        analyticsBaseData.data()[NETWORK] = networkType
    }
}
