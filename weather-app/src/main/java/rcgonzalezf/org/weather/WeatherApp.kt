package rcgonzalezf.org.weather

import androidx.annotation.VisibleForTesting
import com.crittercism.app.Crittercism
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import org.rcgonzalezf.weather.WeatherLibApp
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager
import rcgonzalezf.org.weather.common.analytics.observer.GoogleAnalyticsObserver
import rcgonzalezf.org.weather.common.analytics.observer.LogcatAnalyticsObserver

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
            analyticsManager.addObserver(LogcatAnalyticsObserver())
        }
        analyticsManager.addObserver(GoogleAnalyticsObserver())
    }

    companion object {
        @JvmField
        @VisibleForTesting
        var sIsDebugMode = BuildConfig.DEBUG
        private var sAnalyticsManagerInstance: AnalyticsManager? = null
        @JvmStatic
        val analyticsManager: AnalyticsManager
            get() {
                 if (sAnalyticsManagerInstance == null) {
                     sAnalyticsManagerInstance = AnalyticsManager(getInstance())
                }
                return sAnalyticsManagerInstance as AnalyticsManager
            }

        fun getAppInstance() = getInstance()
    }
}
