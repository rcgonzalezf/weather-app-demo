package rcgonzalezf.org.weather.adapters;

import android.content.Context;
import android.os.Handler;
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
import rcgonzalezf.org.weather.models.WeatherViewModel;
import rcgonzalezf.org.weather.utils.ForecastUtils;

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
    holder.cityNameTextView.setText(mainModel.getCityName());
    holder.datetimeTextView.setText(mainModel.getDateTime());
    holder.itemImage.setImageResource(
        ForecastUtils.getArtResourceForWeatherCondition(mainModel.getWeatherId()));
    holder.detailLocationNameTextView.setText(
        String.format(getString(R.string.location_display_format), mainModel.getCityName(),
            mainModel.getCountry()));

    holder.dayTextView.setText(ForecastUtils.getDayName(mContext, mainModel.getDateTime()));

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
    public TextView cityNameTextView;
    public TextView datetimeTextView;
    public ImageView itemImage;
    public TextView detailLocationNameTextView;
    public TextView dayTextView;
    public View itemView;

    public ModelViewHolder(View itemView) {
      super(itemView);
      this.itemView = itemView;
      this.itemView.setOnClickListener(ModelAdapter.this);
      cityNameTextView = (TextView) itemView.findViewById(R.id.city_name_text_view);
      datetimeTextView = (TextView) itemView.findViewById(R.id.datetime_text_view);
      detailLocationNameTextView = (TextView) itemView.findViewById(R.id.detail_location_name);
      dayTextView =  (TextView) itemView.findViewById(R.id.day_textview);
      itemImage = (ImageView) itemView.findViewById(R.id.item_image);
    }
  }

  public interface OnItemClickListener<VM extends WeatherViewModel> {
    void onItemClick(View view, VM viewModel);
  }

  private String getString(@StringRes int stringResId) {
    return WeatherLibApp.getInstance().getString(stringResId);
  }
}
