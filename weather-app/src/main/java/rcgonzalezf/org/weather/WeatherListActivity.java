package rcgonzalezf.org.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.common.listeners.OnUpdateWeatherListListener;
import org.rcgonzalezf.weather.common.models.Forecast;
import org.rcgonzalezf.weather.common.models.WeatherViewModel;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;
import rcgonzalezf.org.weather.adapters.ModelAdapter;
import rcgonzalezf.org.weather.common.BaseActivity;

public class WeatherListActivity extends BaseActivity
    implements ModelAdapter.OnItemClickListener, OnUpdateWeatherListListener {

  private static final String TAG = WeatherListActivity.class.getSimpleName();
  private RecyclerView mRecyclerView;
  private ModelAdapter<Forecast> mAdapter;
  private OpenWeatherApiCallback mOpenWeatherApiCallback;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mOpenWeatherApiCallback = new OpenWeatherApiCallback(this);
    setupRecyclerView();
  }

  @Override public void onEnterAnimationComplete() {
    super.onEnterAnimationComplete();
    mRecyclerView.scheduleLayoutAnimation();
  }

  @Override public void onItemClick(View view, WeatherViewModel viewModel) {
    Toast.makeText(this,
        String.format(getString(R.string.item_clicked_debug_format), viewModel.getId(),
            viewModel.getDateTime(), viewModel.getDescription()), Toast.LENGTH_SHORT).show();
  }

  @Override public void loadOldData(final List<Forecast> forecastList) {
    if (forecastList != null && !forecastList.isEmpty()) {
      notifyAdapter(forecastList);
    } else {
      Log.d(TAG, "No data even in offline mode :(");
    }
  }

  @Override public void updateList(List<Forecast> forecastList) {
    notifyAdapter(forecastList);
  }

  @Override public void onError(String error) {
    // TODO implement error handling
  }

  @Override protected void searchByQuery(String query, Editable userInput) {
    WeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback> weatherRepository =
        ServiceConfig.getInstance().getWeatherRepository();

    weatherRepository.findWeather(new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder()
        .withCityName(query)
        .build(), mOpenWeatherApiCallback);

    Toast.makeText(this, getString(R.string.searching) + " " + userInput + "...",
        Toast.LENGTH_SHORT).show();
  }

  @Override public void searchByLocation(double lat, double lon) {
    WeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback> weatherRepository =
        ServiceConfig.getInstance().getWeatherRepository();

    weatherRepository.findWeather(new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder()
        .withLatLon(lat, lon)
        .build(), mOpenWeatherApiCallback);
  }

  private void saveForecastList(final List<Forecast> forecastList) {
    new Thread(new Runnable() {
      @Override public void run() {
        SharedPreferences prefs = getSharedPreferences(OFFLINE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FORECASTS, new Gson().toJson(forecastList));
        editor.apply();
      }
    }).start();
  }

  private void setupRecyclerView() {
    mAdapter = new ModelAdapter<>(new ArrayList<Forecast>(), this);
    mAdapter.setOnItemClickListener(this);

    mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setAdapter(mAdapter);
  }

  private void notifyAdapter(final List<Forecast> forecastList) {
    saveForecastList(forecastList);

    runOnUiThread(new Runnable() {
      @Override public void run() {
        mAdapter.setItems(forecastList);
        mAdapter.notifyDataSetChanged();
      }
    });
  }
}
