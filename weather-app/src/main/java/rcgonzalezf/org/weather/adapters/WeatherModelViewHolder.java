package rcgonzalezf.org.weather.adapters;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.rcgonzalezf.weather.common.models.WeatherViewModel;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.databinding.WeatherRowBinding;

public class WeatherModelViewHolder  extends RecyclerView.ViewHolder {
    private final TextView secondaryTempTextView;
    private final TextView primaryTempTextView;
    private final View rootItemView;
    private WeatherRowBinding weatherRowBinding;

    WeatherModelViewHolder(WeatherRowBinding weatherRowBinding) {
        super(weatherRowBinding.getRoot());
        this.weatherRowBinding = weatherRowBinding;
        this.rootItemView = weatherRowBinding.getRoot();
        secondaryTempTextView =
                (TextView) rootItemView.findViewById(R.id.secondary_temperature_text_view);
        primaryTempTextView = (TextView) rootItemView.findViewById(R.id.preferred_temperature_text_view);
    }

    public void bind(WeatherViewModel weatherViewModel) {
        weatherRowBinding.setWeather(weatherViewModel);
        weatherRowBinding.executePendingBindings();
    }

    public TextView getSecondaryTempTextView() {
        return secondaryTempTextView;
    }

    public TextView getPrimaryTempTextView() {
        return primaryTempTextView;
    }

    public View getItemView() {
        return rootItemView;
    }
}
