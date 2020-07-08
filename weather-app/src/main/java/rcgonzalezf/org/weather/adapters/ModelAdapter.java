package rcgonzalezf.org.weather.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import org.rcgonzalezf.weather.common.models.WeatherViewModel;
import java.util.List;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.SettingsActivity;
import rcgonzalezf.org.weather.databinding.WeatherRowBinding;
import static rcgonzalezf.org.weather.utils.WeatherUtils.formatTemperature;

// TODO Revisit this class to do an upgrade to Kotlin
public class ModelAdapter<T extends WeatherViewModel>
        extends RecyclerView.Adapter<ModelAdapter.ModelViewHolder> implements View.OnClickListener {

    private List<T> models;
    private OnItemClickListener<WeatherViewModel> onItemClickListener;

    public ModelAdapter(List<T> models) {
        this.models = models;
    }

    @NonNull
    @Override
    public ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        WeatherRowBinding weatherRowBinding =
                WeatherRowBinding.inflate(layoutInflater, parent, false);
        return new ModelViewHolder(weatherRowBinding);
    }

    @Override
    public void onBindViewHolder(ModelAdapter.ModelViewHolder holder, int position) {
        T mainModel = models.get(position);
        holder.bind(mainModel);
        populateTemperatureViews(holder.itemView.getContext(), holder, mainModel);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(mainModel);
    }

    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @VisibleForTesting
    void populateTemperatureViews(Context context,
            ModelAdapter.ModelViewHolder holder,
                                  T mainModel) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean celsiusPreferred = prefs.getBoolean(SettingsActivity.PREF_TEMPERATURE_UNITS, true);

        final String celsiusText = formatTemperature(mainModel.getTemperature(), true, "C");
        final String fahrenheitText = formatTemperature(mainModel.getTemperature(), false, "F");

        holder.primaryTempTextView.setText(celsiusPreferred ? celsiusText : fahrenheitText);
        holder.secondaryTempTextView.setText(celsiusPreferred ? fahrenheitText : celsiusText);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setItems(List<T> models) {
        this.models = models;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<WeatherViewModel> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(@NonNull final View view) {
        // Give some time to the ripple to finish the effect
        if (onItemClickListener != null) {
            new Handler().postDelayed(createClickRunnable(view), 200);
        }
    }

    @VisibleForTesting
    @NonNull
    Runnable createClickRunnable(@NonNull final View view) {
        return new Runnable() {
            @Override
            public void run() {
                onItemClickListener.onItemClick(view, (WeatherViewModel) view.getTag());
            }
        };
    }

    static class ModelViewHolder extends RecyclerView.ViewHolder {

        TextView secondaryTempTextView;
        TextView primaryTempTextView;
        final View itemView;
        private WeatherRowBinding weatherRowBinding;

        ModelViewHolder(WeatherRowBinding weatherRowBinding) {
            super(weatherRowBinding.getRoot());
            this.weatherRowBinding = weatherRowBinding;
            this.itemView = weatherRowBinding.getRoot();
            secondaryTempTextView =
                    (TextView) itemView.findViewById(R.id.secondary_temperature_text_view);
            primaryTempTextView = (TextView) itemView.findViewById(R.id.preferred_temperature_text_view);
        }

        public void bind(WeatherViewModel weatherViewModel) {
            weatherRowBinding.setWeather(weatherViewModel);
            weatherRowBinding.executePendingBindings();
        }
    }

    public interface OnItemClickListener<VM extends WeatherViewModel> {
        void onItemClick(View view, VM viewModel);
    }
}
