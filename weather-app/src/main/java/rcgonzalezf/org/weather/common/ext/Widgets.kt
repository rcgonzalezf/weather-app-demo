package rcgonzalezf.org.weather.common.ext

import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible

fun ProgressBar.toggleVisibility() {
    this.visibility = if (this.isVisible) {
        View.GONE
    } else {
        View.VISIBLE
    }
}
