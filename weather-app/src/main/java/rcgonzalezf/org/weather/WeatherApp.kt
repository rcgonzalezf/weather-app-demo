package rcgonzalezf.org.weather

import androidx.annotation.VisibleForTesting
import com.crittercism.app.Crittercism
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import org.rcgonzalezf.weather.WeatherLibApp
import rcgonzalezf.org.weather.analytics.analytics.AnalyticsManager
import rcgonzalezf.org.weather.analytics.analytics.observer.GoogleAnalyticsObserver
import rcgonzalezf.org.weather.analytics.analytics.observer.LogcatAnalyticsObserver

@HiltAndroidApp
open class WeatherApp : WeatherLibApp() {

    override fun onCreate() {
        setAppInstance(this)
        super.onCreate()
        if (sIsDebugMode) {
            Stetho.initializeWithDefaults(this)
        } else {
            Crittercism.initialize(this, getString(R.string.crittercism_api_key))
        }
    }

    override fun createOkHttpClient(): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
        if (sIsDebugMode) {
            okHttpBuilder.addInterceptor(StethoInterceptor())
        }
        return okHttpBuilder.build()
    }

    override fun addAnalyticsObservers() {
        if (sIsDebugMode) {
            analyticsManager.addObserver(rcgonzalezf.org.weather.analytics.analytics.observer.LogcatAnalyticsObserver())
        }
        analyticsManager.addObserver(rcgonzalezf.org.weather.analytics.analytics.observer.GoogleAnalyticsObserver())
    }

    companion object {
        @JvmField
        @VisibleForTesting
        var sIsDebugMode = BuildConfig.DEBUG
        private var sAnalyticsManagerInstance: rcgonzalezf.org.weather.analytics.analytics.AnalyticsManager? = null
        @JvmStatic
        val analyticsManager: rcgonzalezf.org.weather.analytics.analytics.AnalyticsManager
            get() {
                 if (sAnalyticsManagerInstance == null) {
                     sAnalyticsManagerInstance =
                         rcgonzalezf.org.weather.analytics.analytics.AnalyticsManager(getInstance())
                }
                return sAnalyticsManagerInstance as rcgonzalezf.org.weather.analytics.analytics.AnalyticsManager
            }

        fun getAppInstance() = getInstance()
    }
}
