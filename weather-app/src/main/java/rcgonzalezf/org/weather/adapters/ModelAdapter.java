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
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import org.rcgonzalezf.weather.WeatherLibApp;
import org.rcgonzalezf.weather.common.models.WeatherViewModel;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.SettingsActivity;
import rcgonzalezf.org.weather.utils.WeatherArtUtils;
import rcgonzalezf.org.weather.utils.WeatherUtils;

import static rcgonzalezf.org.weather.utils.WeatherArtUtils.getArtResourceForWeatherCondition;
import static rcgonzalezf.org.weather.utils.WeatherUtils.formatDate;
import static rcgonzalezf.org.weather.utils.WeatherUtils.formatTemperature;
import static rcgonzalezf.org.weather.utils.WeatherWindUtils.getFormattedWind;

public class ModelAdapter<T extends WeatherViewModel>
    extends RecyclerView.Adapter<ModelAdapter.ModelViewHolder> implements View.OnClickListener {

  private final Context mContext;
  private List<T> mModels;
  private OnItemClickListener onItemClickListener;

  public ModelAdapter(List<T> models, Context context) {
    mModels = models;
    mContext = context;
  }

  @Override public ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(mContext).inflate(R.layout.weather_row, parent, false);
    return new ModelViewHolder(itemView);
  }

  @Override public void onBindViewHolder(ModelAdapter.ModelViewHolder holder, int position) {
    T mainModel = mModels.get(position);
    holder.datetimeTextView.setText(formatDate(mainModel.getDateTime()));
    holder.itemImage.setImageResource(getArtResourceForWeatherCondition(mainModel.getWeatherId()));
    holder.detailLocationNameTextView.setText(
        String.format(getString(R.string.location_display_format), mainModel.getCityName(),
            mainModel.getCountry()));

    holder.dayTextView.setText(WeatherUtils.getDayName(mContext, mainModel.getDateTime()));
    holder.humidityTextView.setText(
        String.format(getString(R.string.key_value_display_format), getString(R.string.humidity),
            mainModel.getHumidity() + " %"));
    holder.windSpeedTextView.setText(
        getFormattedWind(mContext, mainModel.getSpeed(), mainModel.getDeg()));

    populateTemperatureViews(holder, mainModel);

    holder.descriptionTextView.setText(mainModel.getDescription());
    holder.itemView.setTag(mainModel);
  }

  @VisibleForTesting
  void populateTemperatureViews(ModelAdapter.ModelViewHolder holder,
                                T mainModel) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    boolean celsiusPreferred = prefs.getBoolean(SettingsActivity.PREF_TEMPERATURE_UNITS, true);

    final String celsiusText = formatTemperature(mainModel.getTemperature(), true, "C");
    final String fahrenheitText = formatTemperature(mainModel.getTemperature(), false, "F");

    holder.primaryTempTextView.setText(celsiusPreferred ? celsiusText : fahrenheitText);
    holder.secondaryTempTextView.setText(celsiusPreferred ? fahrenheitText : celsiusText);
  }

  @Override public int getItemCount() {
    return mModels.size();
  }

  public void setItems(List<T> models) {
    mModels = models;
    notifyDataSetChanged();
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  @Override public void onClick(@NonNull final View view) {
    // Give some time to the ripple to finish the effect
    if (onItemClickListener != null) {
      new Handler().postDelayed(createClickRunnable(view), 200);
    }
  }

  @VisibleForTesting @NonNull Runnable createClickRunnable(@NonNull final View view) {
    return new Runnable() {
      @Override public void run() {
        onItemClickListener.onItemClick(view, (WeatherViewModel) view.getTag());
      }
    };
  }

  class ModelViewHolder extends RecyclerView.ViewHolder {

    TextView datetimeTextView;
    ImageView itemImage;
    TextView detailLocationNameTextView;
    TextView humidityTextView;
    TextView windSpeedTextView;
    TextView secondaryTempTextView;
    TextView primaryTempTextView;
    TextView dayTextView;
    TextView descriptionTextView;
    View itemView;

    ModelViewHolder(View itemView) {
      super(itemView);
      this.itemView = itemView;
      this.itemView.setOnClickListener(ModelAdapter.this);
      datetimeTextView = (TextView) itemView.findViewById(R.id.datetime_text_view);
      detailLocationNameTextView = (TextView) itemView.findViewById(R.id.detail_location_name);
      dayTextView = (TextView) itemView.findViewById(R.id.day_textview);
      itemImage = (ImageView) itemView.findViewById(R.id.item_image);
      humidityTextView = (TextView) itemView.findViewById(R.id.humidity_text_view);
      windSpeedTextView = (TextView) itemView.findViewById(R.id.wind_speed_text_view);
      secondaryTempTextView =
          (TextView) itemView.findViewById(R.id.secondary_temperature_text_view);
      primaryTempTextView = (TextView) itemView.findViewById(R.id.preferred_temperature_text_view);
      descriptionTextView = (TextView) itemView.findViewById(R.id.description_text_view);
    }
  }

  public interface OnItemClickListener<VM extends WeatherViewModel> {
    void onItemClick(View view, VM viewModel);
  }

  private String getString(@StringRes int stringResId) {
    return WeatherLibApp.getInstance().getString(stringResId);
  }
}
