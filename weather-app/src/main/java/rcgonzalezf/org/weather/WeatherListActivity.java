package rcgonzalezf.org.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.common.listeners.OnUpdateWeatherListListener;
import org.rcgonzalezf.weather.common.models.WeatherInfo;
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
  public static final String CITY_NAME_TO_SEARCH_ON_SWIPE = "mCityNameToSearchOnSwipe";
  private RecyclerView mRecyclerView;
  private SwipeRefreshLayout mSwipeToRefreshLayout;
  private ModelAdapter<WeatherInfo> mAdapter;
  private OpenWeatherApiCallback mOpenWeatherApiCallback;
  private CharSequence mCityNameToSearchOnSwipe;
  private ProgressBar mProgress;
  private Executor mExecutor = Executors.newSingleThreadExecutor();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mOpenWeatherApiCallback = new OpenWeatherApiCallback(this);
    setupRecyclerView();

    if (savedInstanceState != null) {
      mCityNameToSearchOnSwipe = savedInstanceState.getCharSequence(CITY_NAME_TO_SEARCH_ON_SWIPE);
    }

    mProgress = (ProgressBar) findViewById(R.id.progress_bar);
    mSwipeToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh_layout);
    enableSwipeToRefreshLayout();
    mSwipeToRefreshLayout.setOnRefreshListener(createSwipeToRefreshListener());
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putCharSequence(CITY_NAME_TO_SEARCH_ON_SWIPE, mCityNameToSearchOnSwipe);
  }

  @VisibleForTesting void onItemsLoadComplete() {
    toggleProgressIndicator();
    enableSwipeToRefreshLayout();
    if (mSwipeToRefreshLayout.isRefreshing()) {
      mSwipeToRefreshLayout.setRefreshing(false);
    }
  }

  private void toggleProgressIndicator() {
    if (mProgress.getVisibility() == View.VISIBLE) mProgress.setVisibility(View.GONE);
    else mProgress.setVisibility(View.VISIBLE);
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

  @Override public void loadOldData(final List<WeatherInfo> weatherInfoList) {
    if (weatherInfoList != null && !weatherInfoList.isEmpty()) {
      notifyAdapter(weatherInfoList);
      trackOnActionEvent(new AnalyticsEvent(NO_NETWORK_SEARCH, weatherInfoList.get(0).getCityName()));
    } else {
      Log.d(TAG, "No data even in offline mode :(");
      trackOnActionEvent(new AnalyticsEvent(NO_NETWORK_SEARCH, "EMPTY"));
      //cancel swipe to refresh loading
      onItemsLoadComplete();
    }
  }

  @Override public void updateList(@NonNull List<WeatherInfo> weatherInfoList) {
    String cityName = weatherInfoList.isEmpty() ? "" : weatherInfoList.get(0).getCityName();
    trackOnActionEvent(new AnalyticsEvent(SEARCH_COMPLETED, "cityName: " + cityName));
    notifyAdapter(weatherInfoList);
  }

  @Override public void onError(String error) {
    // TODO implement error handling
    runOnUiThread(new Runnable() {
      @Override public void run() {
        toggleProgressIndicator();
      }
    });

    Log.d(TAG, error);
    trackOnActionEvent(new AnalyticsEvent(SEARCH_COMPLETED, "error: " + error));
  }

  @Override protected void searchByQuery(String query, CharSequence userInput) {
    toggleProgressIndicator();

    WeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback> weatherRepository =
        ServiceConfig.getInstance().getWeatherRepository();

    weatherRepository.findWeather(new OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder()
        .withCityName(query)
        .build(), mOpenWeatherApiCallback);

    Toast.makeText(this, getString(R.string.searching) + " " + userInput + "...",
        Toast.LENGTH_SHORT).show();
    updateCityNameForSwipeToRefresh(userInput);
  }

  @Override public void searchByLocation(double lat, double lon) {
    toggleProgressIndicator();

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
      updateCityNameForSwipeToRefresh(cityName);
    }
  }

  private void updateCityNameForSwipeToRefresh(CharSequence cityName) {
    this.mCityNameToSearchOnSwipe = cityName;
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

  private void saveForecastList(final List<WeatherInfo> weatherInfoList) {
    mExecutor.execute(new Runnable() {
      @Override public void run() {
        SharedPreferences prefs = getSharedPreferences(OFFLINE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FORECASTS, new Gson().toJson(weatherInfoList));
        editor.apply();
      }
    });
  }

  private void setupRecyclerView() {
    mAdapter = new ModelAdapter<>(new ArrayList<WeatherInfo>(), this);
    mAdapter.setOnItemClickListener(this);

    mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setAdapter(mAdapter);
  }

  private void notifyAdapter(final List<WeatherInfo> weatherInfoList) {
    saveForecastList(weatherInfoList);
    runOnUiThread(createNotifyRunnable(weatherInfoList));
  }

  @VisibleForTesting @NonNull Runnable createNotifyRunnable(final List<WeatherInfo> weatherInfoList) {
    return new Runnable() {
      @Override public void run() {
        mAdapter.setItems(weatherInfoList);
        mAdapter.notifyDataSetChanged();
        onItemsLoadComplete();
      }
    };
  }

  @VisibleForTesting @NonNull SwipeRefreshLayout.OnRefreshListener createSwipeToRefreshListener() {
    return new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        searchByManualInput(mCityNameToSearchOnSwipe);
      }
    };
  }

  private void enableSwipeToRefreshLayout() {
    mSwipeToRefreshLayout.setEnabled(mCityNameToSearchOnSwipe != null);
  }
}
