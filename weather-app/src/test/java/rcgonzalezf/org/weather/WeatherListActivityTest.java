package rcgonzalezf.org.weather;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.common.models.WeatherInfo;
import org.rcgonzalezf.weather.common.models.WeatherViewModel;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;
import rcgonzalezf.org.weather.adapters.ModelAdapter;
import rcgonzalezf.org.weather.common.BaseActivity;
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent;

import static rcgonzalezf.org.weather.WeatherListActivity.CITY_NAME_TO_SEARCH_ON_SWIPE;
import static rcgonzalezf.org.weather.common.BaseActivity.FORECASTS;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.LOCATION_SEARCH;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED;

@RunWith(JMockit.class) public class WeatherListActivityTest {

  @Tested private WeatherListActivity uut;
  @SuppressWarnings("unused") @Mocked private ContextWrapper mContextWrapper;
  @SuppressWarnings("unused") @Mocked private SharedPreferences.Editor mSharedPreferencesEditor;
  @SuppressWarnings("unused") @Mocked private SharedPreferences mSharedPreferences;
  @SuppressWarnings("unused") @Mocked private BaseActivity mBaseActivity;
  @SuppressWarnings("unused") @Mocked private ProgressBar mProgress;
  @SuppressWarnings("unused") @Mocked private RecyclerView mRecyclerView;
  @SuppressWarnings("unused") @Mocked private ModelAdapter<WeatherInfo> mAdapter;
  @SuppressWarnings("unused") @Mocked private OpenWeatherApiCallback mOpenWeatherApiCallback;
  @SuppressWarnings("unused") @Mocked private SwipeRefreshLayout mSwipeToRefreshLayout;
  @org.mockito.Mock
  private Bundle savedInstanceState;

  @SuppressWarnings("unused") @Mocked
  private OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder
      mOpenWeatherApiRequestBuilder;
  @SuppressWarnings("unused") @Mocked
  private WeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback>
      mWeatherRepository;
  @SuppressWarnings("unused") @Mocked private ServiceConfig mServiceConfig;
  @SuppressWarnings("unused") @Mocked private AnalyticsEvent mAnalyticsEvent;

  private List<WeatherInfo> mWeatherInfoList;
  private String mQuery;
  private Runnable mNotifyAdapterRunnable;
  private SwipeRefreshLayout.OnRefreshListener mSwipeToRefreshListener;
  private Runnable runnable;
  private boolean mIsPerformingAction;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    uut = new WeatherListActivity();

    new MockUp<AppCompatActivity>() {
      @SuppressWarnings("unused") @Mock View findViewById(@IdRes int id) {
        View view = null;
        if (id == R.id.swipe_to_refresh_layout) {
          view = mSwipeToRefreshLayout;
        } else if (id == R.id.main_recycler_view) {
          view = mRecyclerView;
        } else if (id == R.id.progress_bar) {
          view = mProgress;
        }
        return view;
      }
    };

    runnable = new Runnable() {
      @Override public void run() {
        mIsPerformingAction = true;
      }
    };
  }

  @After public void clearEverything() {
    uut = null;
    mWeatherInfoList = null;
    mNotifyAdapterRunnable = null;
    mBaseActivity = null;
  }

  @Test public void shouldSetUpRecyclerViewOnCreation() {

    whenCreatingTheActivity();

    thenAdapterShouldBeInitialized();
    thenRecyclerViewShouldBeInitialized();
  }

  @Test public void shouldScheduleLayoutAnimationOnAnimationComplete() {
    givenActivityCreated(null);

    whenEnteringAnimationComplete();

    shouldScheduleLayoutAnimation();
  }

  @Test public void shouldHandleItemClick(@Mocked Toast toast, @Mocked View view,
      @Mocked WeatherViewModel weatherViewModel) {
    givenStringResource();

    whenClickingItem(view, weatherViewModel);

    thenToastShouldMakeText(toast);
  }

  @Test public void shouldLoadOldData(@Mocked Thread thread) {
    givenActivityCreated(savedInstanceState);
    givenForecastList();
    givenForecastElement("someCity");

    whenLoadingOldData();

    thenBaseActivityShouldPostRunnableOnUiThread(runnable);
    thenNotifyAdapterRunnableShouldBeCreated();
    thenShouldTrackEvent(NO_NETWORK_SEARCH, "someCity");
  }

  @Test
  public void shouldNotLoadOldForNullList(@SuppressWarnings("UnusedParameters") @Mocked Log log) {
    givenActivityCreated(savedInstanceState);

    whenLoadingOldData();

    thenLogDataShouldBeWritten("No data even in offline mode :(");
    thenShouldTrackEvent(NO_NETWORK_SEARCH, "EMPTY");
  }

  @Test
  public void shouldNotLoadOldForEmptyList(@SuppressWarnings("UnusedParameters") @Mocked Log log) {
    givenActivityCreated(null);
    givenForecastList();

    whenLoadingOldData();

    thenLogDataShouldBeWritten("No data even in offline mode :(");
    thenShouldTrackEvent(NO_NETWORK_SEARCH, "EMPTY");
  }

  @Test public void shouldNotifyAdapterOnUpdatingListWithNullCity() {
    givenActivityCreated(null);
    givenForecastList();
    givenForecastElement("someCity");
    givenTestExecutor();

    whenUpdatingList();

    thenBaseActivityShouldPostRunnableOnUiThread(runnable);
    thenNotifyAdapterRunnableShouldBeCreated();
    thenShouldTrackEvent(SEARCH_COMPLETED, "cityName: " + "someCity");
  }

  @Test public void shouldNotifyAdapterOnUpdatingListWithEmptyCityForEmptyList() {
    givenForecastList();
    givenTestExecutor();

    whenUpdatingList();

    thenShouldTrackEvent(SEARCH_COMPLETED, "cityName: " + "");
  }

  @Test public void shouldSaveIntoSharedPreferencesCache() {
    givenForecastList();
    givenTestExecutor();

    whenSavingTheList();

    thenSharedPreferencesShouldSaveTheList();
  }

  @Test public void shouldHandleError(@SuppressWarnings("UnusedParameters") @Mocked Log log) {
    givenActivityCreated(null);
    final String givenErrorString = "Some Error String";

    whenHandlingError(givenErrorString);

    thenLogDataShouldBeWritten(givenErrorString);
    thenShouldTrackEvent(SEARCH_COMPLETED, "error: " + givenErrorString);
  }

  @Test public void shouldBuildWithCityNameOnSearchingByQuery(
      @SuppressWarnings("UnusedParameters") @Mocked Toast toast,
      @SuppressWarnings("UnusedParameters") @Mocked Context context) {
    givenActivityCreated(null);
    givenQuery("Some City Name");

    whenSearchingByQuery("Some City Name");

    thenBuilderShouldAddCityName();
    thenWeatherRepositoryShouldFindWeather();
  }

  @Test public void shouldBuildWithLatLonOnSearchingByLocation(@Mocked Geocoder geocoder)
      throws IOException {
    givenActivityCreated(null);
    double givenLat = 1d;
    double givenLon = 1d;
    givenGeoCoderThrowsException(geocoder, givenLat, givenLon);

    whenSearchingByLocation(givenLat, givenLon);

    thenBuilderShouldAddLatLon(givenLat, givenLon);
    thenWeatherRepositoryShouldFindWeather();
    thenShouldTrackEvent(LOCATION_SEARCH, "Geocoder Failure");
  }

  @Test public void shouldNotifyDataSetChangeOnRunningTheNotifyRunnable() {
    givenActivityCreated(null);
    givenForecastList();
    givenNotifyRunnable();

    whenRunningNotifyRunnable();

    thenAdapterShouldNotifyDataSetChanges();
  }

  @Test public void shouldBuildWithCityNameOnSearchingByLocationWithGeoCoderCity(
      @Mocked Geocoder geocoder, @Mocked Address address) throws IOException {
    givenActivityCreated(null);
    double givenLat = 1d;
    double givenLon = 1d;
    givenQuery("Some City Name");
    givenGeoCoderCity(geocoder, address, givenLat, givenLon);

    whenSearchingByLocation(givenLat, givenLon);

    thenBuilderShouldAddCityName();
    thenWeatherRepositoryShouldFindWeather();
    thenShouldTrackEvent(LOCATION_SEARCH, mQuery);
  }

  @Test public void shouldSearchByManualInputOnRefresh() {
    givenSwipeToRefreshListener();

    whenRefreshing();

    thenShouldSearchByManualInput();
  }

  @Test public void shouldPutHelperVariablesOnSavingInstanceState(@Mocked Bundle outState) {

    whenSavingTheInstanceState(outState);

    thenVerifyIsSavingHelperVariables(outState);
  }

  @Test public void shouldStopRefreshingOnceTheItemLoadIsComplete() {
    givenActivityCreated(new Bundle());
    givenSwipeToRefreshLayoutIsRefreshing(true);

    whenItemsLoadIsComplete();

    thenShouldStopRefreshing();
  }

  @Test public void shouldEnableSwipeToRefreshLayoutIfThereIsACityName(
      @Mocked Bundle savedInstanceState) {
    givenSavedInstanceStateWithCityName(savedInstanceState);
    givenActivityCreated(savedInstanceState);

    whenItemsLoadIsComplete();

    thenSwipeToRefreshShouldBeEnabled();
  }

  @Test public void shouldShowProgressIndicatorIfToggleAndNotVisible() {
    givenActivityCreated(null);
    givenProgressBarNotVisible();

    whenToggleProgressBar();

    thenShouldSetVisible();
  }

  @Test public void shouldToggleProgressIndicatorOnError() {
    givenActivityCreated(null);
    Runnable runnable = givenToggleRunnable();

    whenRunningToggleRunnable(runnable);

    thenShouldToggleProgressIndicator();
  }

  private void thenShouldToggleProgressIndicator() {
    new Verifications() {{
      uut.toggleProgressIndicator();
    }};
  }

  private void whenRunningToggleRunnable(Runnable runnable) {
    runnable.run();
  }

  private Runnable givenToggleRunnable() {
    return uut.createRunnableToggleProgressIndicator();
  }

  private void thenSharedPreferencesShouldSaveTheList() {
    new Verifications() {{
      mSharedPreferencesEditor.putString(withEqual(FORECASTS), withAny(""));
    }};
  }

  private void whenSavingTheList() {
    Deencapsulation.invoke(uut, "saveForecastList", mWeatherInfoList);
  }

  private void givenTestExecutor() {
    Deencapsulation.setField(uut, "executor", new Executor() {
      @Override public void execute(Runnable command) {
        command.run();
      }
    });
  }

  private void thenShouldSetVisible() {
    new Verifications() {{
      //noinspection WrongConstant
      mProgress.setVisibility(withEqual(View.VISIBLE));
    }};
  }

  private void whenToggleProgressBar() {
    Deencapsulation.invoke(uut, "toggleProgressIndicator");
  }

  private void givenProgressBarNotVisible() {
    new Expectations() {{
      mProgress.getVisibility();
      result = View.GONE;
    }};
  }

  private void givenSavedInstanceStateWithCityName(final Bundle savedInstanceState) {
    new Expectations() {{
      savedInstanceState.getCharSequence(CITY_NAME_TO_SEARCH_ON_SWIPE);
      result = "";
    }};
  }

  private void thenSwipeToRefreshShouldBeEnabled() {
    new Verifications() {{
      mSwipeToRefreshLayout.setEnabled(withEqual(true));
    }};
  }

  private void thenShouldStopRefreshing() {
    new Verifications() {{
      mSwipeToRefreshLayout.setRefreshing(withEqual(false));
    }};
  }

  private void whenItemsLoadIsComplete() {
    uut.onItemsLoadComplete();
  }

  private void givenSwipeToRefreshLayoutIsRefreshing(final boolean isRefreshing) {
    new Expectations() {{
      mSwipeToRefreshLayout.isRefreshing();
      result = isRefreshing;
    }};
  }

  private void thenVerifyIsSavingHelperVariables(final Bundle outState) {
    new Verifications() {{
      outState.putCharSequence(CITY_NAME_TO_SEARCH_ON_SWIPE, withAny(""));
    }};
  }

  private void whenSavingTheInstanceState(Bundle outState) {
    uut.onSaveInstanceState(outState);
  }

  private void thenShouldSearchByManualInput() {
    new Verifications() {{
      mBaseActivity.searchByManualInput(withAny(""));
    }};
  }

  private void whenRefreshing() {
    mSwipeToRefreshListener.onRefresh();
  }

  private void givenSwipeToRefreshListener() {
    mSwipeToRefreshListener = uut.createSwipeToRefreshListener();
  }

  private void thenShouldTrackEvent(final String eventName, final String additionalDetails) {
    new Verifications() {{
      //noinspection WrongConstant
      new AnalyticsEvent(withEqual(eventName), withEqual(additionalDetails));
      mBaseActivity.trackOnActionEvent(withAny(mAnalyticsEvent));
    }};
  }

  private void givenGeoCoderCity(final Geocoder geocoder, final Address address, final double lat,
      final double lon) throws IOException {
    final List<Address> addresses = new ArrayList<>();
    addresses.add(address);
    new Expectations() {{
      geocoder.getFromLocation(lat, lon, 1);
      result = addresses;
      addresses.get(0).getLocality();
      result = mQuery;
    }};
  }

  private void givenGeoCoderThrowsException(final Geocoder geocoder, final double lat,
      final double lon) throws IOException {
    new Expectations() {{
      geocoder.getFromLocation(lat, lon, 1);
      result = new IndexOutOfBoundsException("");
    }};
  }

  private void thenNotifyAdapterRunnableShouldBeCreated() {
    new Verifications() {{
      uut.createNotifyRunnable(withAny(mWeatherInfoList));
    }};
  }

  private void thenAdapterShouldNotifyDataSetChanges() {
    new Verifications() {{
      mAdapter.notifyDataSetChanged();
    }};
  }

  private void whenRunningNotifyRunnable() {
    mNotifyAdapterRunnable.run();
  }

  private void givenNotifyRunnable() {
    mNotifyAdapterRunnable = uut.createNotifyRunnable(mWeatherInfoList);
  }

  private void thenBuilderShouldAddLatLon(final double givenLat, final double givenLon) {
    new Verifications() {{
      mOpenWeatherApiRequestBuilder.withLatLon(withEqual(givenLat), withEqual(givenLon));
      mOpenWeatherApiRequestBuilder.build();
    }};
  }

  private void whenSearchingByLocation(double givenLat, double givenLon) {
    uut.searchByLocation(givenLat, givenLon);
  }

  private void whenHandlingError(String errorString) {
    uut.onError(errorString);
  }

  private void thenWeatherRepositoryShouldFindWeather() {
    new Verifications() {{
      mWeatherRepository.findWeather(withAny(new OpenWeatherApiRequestParameters()),
          withAny(mOpenWeatherApiCallback));
    }};
  }

  private void thenBuilderShouldAddCityName() {
    new Verifications() {{
      mOpenWeatherApiRequestBuilder.withCityName(withEqual(mQuery));
    }};
  }

  private void whenSearchingByQuery(CharSequence charSequence) {
    uut.searchByQuery(mQuery, charSequence);
  }

  private void givenQuery(String query) {
    mQuery = query;
  }

  private void whenUpdatingList() {
    uut.updateList(mWeatherInfoList);
  }

  private void thenLogDataShouldBeWritten(final String message) {
    new Verifications() {{
      Log.d(withAny("string"), message);
    }};
  }

  private void thenBaseActivityShouldPostRunnableOnUiThread(final Runnable runnable) {
    new Verifications() {{
      mBaseActivity.runOnUiThread(withAny(runnable));
    }};
  }

  private void whenLoadingOldData() {
    uut.loadOldData(mWeatherInfoList);
  }

  private void givenForecastElement(String cityName) {
    WeatherInfo weatherInfo = new WeatherInfo();
    weatherInfo.setCityName(cityName);
    mWeatherInfoList.add(weatherInfo);
  }

  private void givenForecastList() {
    mWeatherInfoList = new ArrayList<>();
  }

  private void givenStringResource() {
    new Expectations() {{
      mBaseActivity.getString(R.string.item_clicked_debug_format);
      result = "Item Clicked. CountryId: %1$d:,  %2$s, %3$s";
    }};
  }

  private void thenToastShouldMakeText(final Toast toast) {
    new Verifications() {{
      toast.show();
    }};
  }

  private void whenClickingItem(View mView, WeatherViewModel mWeatherViewModel) {
    uut.onItemClick(mView, mWeatherViewModel);
  }

  private void givenActivityCreated(Bundle savedInstanceState) {
    uut.onCreate(savedInstanceState);
  }

  private void shouldScheduleLayoutAnimation() {
    new Verifications() {{
      mRecyclerView.scheduleLayoutAnimation();
    }};
  }

  private void whenEnteringAnimationComplete() {
    uut.onEnterAnimationComplete();
  }

  private void thenRecyclerViewShouldBeInitialized() {
    new Verifications() {{
      mRecyclerView.setLayoutManager(withAny(new LinearLayoutManager(uut)));
      mRecyclerView.setAdapter(withAny(mAdapter));
    }};
  }

  private void thenAdapterShouldBeInitialized() {
    new Verifications() {{
      new ModelAdapter<>(new ArrayList<WeatherInfo>(), uut);
      mAdapter.setOnItemClickListener(uut);
    }};
  }

  private void whenCreatingTheActivity() {
    uut.onCreate(null);
  }
}
