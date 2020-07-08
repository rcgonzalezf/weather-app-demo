package rcgonzalezf.org.weather.adapters

import android.content.Context
import android.os.Handler
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rcgonzalezf.weather.common.models.WeatherViewModel
import rcgonzalezf.org.weather.SettingsActivity
import rcgonzalezf.org.weather.databinding.WeatherRowBinding
import rcgonzalezf.org.weather.utils.WeatherUtils.formatTemperature

class ModelAdapter<T : WeatherViewModel>(private var models: List<T>)
    : RecyclerView.Adapter<WeatherModelViewHolder>(), View.OnClickListener {
    private var onItemClickListener: OnItemClickListener<WeatherViewModel>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherModelViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val weatherRowBinding = WeatherRowBinding.inflate(layoutInflater, parent, false)
        return WeatherModelViewHolder(weatherRowBinding)
    }

    override fun onBindViewHolder(holder: WeatherModelViewHolder, position: Int) {
        val mainModel = models[position]
        holder.bind(mainModel)
        populateTemperatureViews(holder.itemViewRoot.context, holder, mainModel)
        holder.itemViewRoot.setOnClickListener(this)
        holder.itemViewRoot.tag = mainModel
    }

    @VisibleForTesting
    fun populateTemperatureViews(context: Context, holder: WeatherModelViewHolder,
                                 mainModel: T) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val celsiusPreferred = prefs.getBoolean(SettingsActivity.PREF_TEMPERATURE_UNITS, true)
        val celsiusText = formatTemperature(mainModel.temperature, true, "C")
        val fahrenheitText = formatTemperature(mainModel.temperature, false, "F")
        holder.primaryTempTextView.text = if (celsiusPreferred) celsiusText else fahrenheitText
        holder.secondaryTempTextView.text = if (celsiusPreferred) fahrenheitText else celsiusText
    }

    override fun getItemCount(): Int {
        return models.size
    }

    fun setItems(models: List<T>) {
        this.models = models
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<WeatherViewModel>?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onClick(view: View) {
        // Give some time to the ripple to finish the effect
        if (onItemClickListener != null) {
            Handler().postDelayed(createClickRunnable(view), 200)
        }
    }

    @VisibleForTesting
    fun createClickRunnable(view: View): Runnable {
        return Runnable { onItemClickListener?.onItemClick(view, view.tag as WeatherViewModel) }
    }

    interface OnItemClickListener<VM : WeatherViewModel> {
        fun onItemClick(view: View, viewModel: VM)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("android:src")
        fun setImageResource(imageView: ImageView, resource: Int) {
            imageView.setImageResource(resource)
        }
    }
}
