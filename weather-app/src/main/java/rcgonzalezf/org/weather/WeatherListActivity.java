package rcgonzalezf.org.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.common.listeners.OnUpdateWeatherListListener;
import org.rcgonzalezf.weather.common.models.Forecast;
import org.rcgonzalezf.weather.common.models.WeatherViewModel;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;
import rcgonzalezf.org.weather.adapters.ModelAdapter;
import rcgonzalezf.org.weather.common.BaseActivity;
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent;

import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.LOCATION_SEARCH;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED;

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
      trackOnActionEvent(new AnalyticsEvent(NO_NETWORK_SEARCH, forecastList.get(0).getCityName()));
    } else {
      Log.d(TAG, "No data even in offline mode :(");
      trackOnActionEvent(new AnalyticsEvent(NO_NETWORK_SEARCH, "EMPTY"));
    }
  }

  @Override public void updateList(@NonNull List<Forecast> forecastList) {
    String cityName = forecastList.isEmpty() ? "" : forecastList.get(0).getCityName();
    trackOnActionEvent(new AnalyticsEvent(SEARCH_COMPLETED, "cityName: " + cityName));
    notifyAdapter(forecastList);
  }

  @Override public void onError(String error) {
    // TODO implement error handling
    Log.d(TAG, error);
    trackOnActionEvent(new AnalyticsEvent(SEARCH_COMPLETED, "error: " + error));
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

    final String cityName = cityNameFromLatLon(lat, lon);

    if (cityName == null) {
      weatherRepository.findWeather(
          new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withLatLon(lat, lon)
              .build(), mOpenWeatherApiCallback);
      trackOnActionEvent(new AnalyticsEvent(LOCATION_SEARCH, "Geocoder Failure"));
    } else {
      weatherRepository.findWeather(
          new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder().withCityName(cityName)
              .build(), mOpenWeatherApiCallback);

      trackOnActionEvent(new AnalyticsEvent(LOCATION_SEARCH, cityName));
    }
  }

  private @Nullable String cityNameFromLatLon(double lat, double lon) {
    String cityName = null;
    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

    List<Address> addresses;
    try {
      addresses = geocoder.getFromLocation(lat, lon, 1);
      cityName = addresses.get(0).getLocality();
    } catch (Exception e) {
      Log.d(TAG, "error retrieving the cityName with Geocoder");
    }
    return cityName;
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
    runOnUiThread(createNotifyRunnable(forecastList));
  }

  @VisibleForTesting @NonNull Runnable createNotifyRunnable(final List<Forecast> forecastList) {
    return new Runnable() {
      @Override public void run() {
        mAdapter.setItems(forecastList);
        mAdapter.notifyDataSetChanged();
      }
    };
  }
}
