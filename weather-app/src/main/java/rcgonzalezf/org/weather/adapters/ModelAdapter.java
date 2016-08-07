package rcgonzalezf.org.weather.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import org.rcgonzalezf.weather.WeatherLibApp;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.SettingsActivity;
import rcgonzalezf.org.weather.models.WeatherViewModel;

import static rcgonzalezf.org.weather.utils.WeatherUtils.formatDate;
import static rcgonzalezf.org.weather.utils.WeatherUtils.formatTemperature;
import static rcgonzalezf.org.weather.utils.WeatherUtils.getArtResourceForWeatherCondition;
import static rcgonzalezf.org.weather.utils.WeatherUtils.getDayName;
import static rcgonzalezf.org.weather.utils.WeatherUtils.getFormattedWind;

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

    holder.dayTextView.setText(getDayName(mContext, mainModel.getDateTime()));
    holder.humidityTextView.setText(
        String.format(getString(R.string.key_value_display_format), getString(R.string.humidity),
            mainModel.getHumidity() + " %"));
    holder.windSpeedTextView.setText(
        getFormattedWind(mContext, mainModel.getSpeed(), mainModel.getDeg()));

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    boolean celsiusPreferred = prefs.getBoolean(SettingsActivity.PREF_TEMPERATURE_UNITS, true);
    if (celsiusPreferred) {
      holder.primaryTempTextView.setText(formatTemperature(mainModel.getTemperature(), false, "C"));
      holder.secondaryTempTextView.setText(
          formatTemperature(mainModel.getTemperature(), true, "F"));
    } else {
      holder.secondaryTempTextView.setText(
          formatTemperature(mainModel.getTemperature(), false, "C"));
      holder.primaryTempTextView.setText(formatTemperature(mainModel.getTemperature(), true, "F"));
    }

    holder.descriptionTextView.setText(mainModel.getDescription());
    holder.itemView.setTag(mainModel);
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
      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          onItemClickListener.onItemClick(view, (WeatherViewModel) view.getTag());
        }
      }, 200);
    }
  }

  public class ModelViewHolder extends RecyclerView.ViewHolder {

    public TextView datetimeTextView;
    public ImageView itemImage;
    public TextView detailLocationNameTextView;
    public TextView humidityTextView;
    public TextView windSpeedTextView;
    public TextView secondaryTempTextView;
    public TextView primaryTempTextView;
    public TextView dayTextView;
    public TextView descriptionTextView;
    public View itemView;

    public ModelViewHolder(View itemView) {
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
