package rcgonzalezf.org.weather.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.rcgonzalezf.weather.common.models.WeatherViewModel
import rcgonzalezf.org.weather.R
import rcgonzalezf.org.weather.databinding.WeatherRowBinding

class WeatherModelViewHolder internal constructor(private val weatherRowBinding: WeatherRowBinding) : RecyclerView.ViewHolder(weatherRowBinding.root) {
    val itemViewRoot: View = weatherRowBinding.root
    val secondaryTempTextView: TextView
            = itemViewRoot.findViewById(R.id.secondary_temperature_text_view)
    val primaryTempTextView: TextView
            = itemViewRoot.findViewById(R.id.preferred_temperature_text_view)
    fun bind(weatherViewModel: WeatherViewModel?) {
        weatherRowBinding.weather = weatherViewModel
        weatherRowBinding.executePendingBindings()
    }
}
