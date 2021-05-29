package rcgonzalezf.org.weather.common

import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import rcgonzalezf.org.weather.R
import java.lang.ref.WeakReference

open class PermissionChecker(private val permission: String, activity: BaseActivity,
                        private val requestCode: Int, container: View,
                        @StringRes private val permissionGrantedMessageId: Int,
                        @StringRes private val permissionsNotGrantedMessageId: Int,
                        @StringRes private val permissionRationaleMessageId: Int)
    : ActivityCompat.OnRequestPermissionsResultCallback {
    private val weakContext: WeakReference<BaseActivity> = WeakReference(activity)
    private val container: WeakReference<View> = WeakReference(container)

    companion object {
        const val LOCATION = 10
    }

    private var permissionResultListener: PermissionResultListener? = null

    fun hasPermission(): Boolean {
        weakContext.get() ?.let {
            return (ActivityCompat.checkSelfPermission(it, permission)
                    == PackageManager.PERMISSION_GRANTED)
        }
        return false
    }

    fun requestPermission(permissionResultListener: PermissionResultListener) {
        this.permissionResultListener = permissionResultListener
        weakContext.get()?.let { context ->
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                container.get()?.let {
                    Snackbar.make(it, permissionRationaleMessageId, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, snackBarClickListener)
                            .show()
                }
            } else {
                requestPermissions()
            }
        }
    }

    @get:VisibleForTesting
    val snackBarClickListener: View.OnClickListener
        get() = View.OnClickListener { requestPermissions() }

    fun requestPermissions() {
        weakContext.get()?.let {
            ActivityCompat.requestPermissions(it, arrayOf(permission), requestCode)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (this.requestCode == requestCode) {
            val messageResId: Int
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                messageResId = permissionGrantedMessageId
                handleGranted()
            } else {
                messageResId = permissionsNotGrantedMessageId
                handleRejected()
            }
            container.get()?.let {
                Snackbar.make(it, messageResId, Snackbar.LENGTH_SHORT).show()
            }

        } else {
            weakContext.get()?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @VisibleForTesting
    open fun handleRejected() {
        permissionResultListener?.onFailure()
    }

    @VisibleForTesting
    open fun handleGranted() {
        permissionResultListener?.onSuccess()
    }
}
