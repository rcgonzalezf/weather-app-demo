package rcgonzalezf.org.weather

import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceActivity
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import rcgonzalezf.org.weather.common.analytics.Analytics

/**
 * A [android.preference.PreferenceActivity] which implements and proxies the necessary calls
 * to be used with AppCompat.
 */
abstract class AppCompatPreferenceActivity : PreferenceActivity() {
    private var appCompatDelegate: AppCompatDelegate? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.installViewFactory()
        delegate.onCreate(savedInstanceState)
        trackOnScreen()
        super.onCreate(savedInstanceState)
    }

    public override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delegate.onPostCreate(savedInstanceState)
    }

    val supportActionBar: ActionBar?
        get() = delegate.supportActionBar

    override fun getMenuInflater(): MenuInflater {
        return delegate.menuInflater
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        delegate.setContentView(layoutResID)
    }

    override fun setContentView(view: View) {
        delegate.setContentView(view)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        delegate.setContentView(view, params)
    }

    override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
        delegate.addContentView(view, params)
    }

    public override fun onPostResume() {
        super.onPostResume()
        delegate.onPostResume()
    }

    public override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        delegate.setTitle(title)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        delegate.onConfigurationChanged(newConfig)
    }

    public override fun onStop() {
        super.onStop()
        delegate.onStop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    override fun invalidateOptionsMenu() {
        delegate.invalidateOptionsMenu()
    }

    private val delegate: AppCompatDelegate
        private get() {
            if (appCompatDelegate == null) {
                appCompatDelegate = AppCompatDelegate.create(this, null)
            }
            return appCompatDelegate as AppCompatDelegate
        }

    fun trackOnScreen() {
        Analytics().trackOnScreen(this.javaClass.simpleName)
    }
}