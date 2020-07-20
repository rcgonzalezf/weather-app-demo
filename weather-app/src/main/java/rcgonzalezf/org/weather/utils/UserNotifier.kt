package rcgonzalezf.org.weather.utils

import android.content.Context
import android.widget.Toast

interface UserNotifier {
    fun notify(message: String)
}

class ToastUserNotifier(val context: Context) : UserNotifier {
    override fun notify(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
