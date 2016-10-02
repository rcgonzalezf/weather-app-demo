package rcgonzalezf.org.weather;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.common.ServiceConfig;
import org.rcgonzalezf.weather.common.WeatherRepository;
import org.rcgonzalezf.weather.common.models.Forecast;
import org.rcgonzalezf.weather.common.models.WeatherViewModel;
import org.rcgonzalezf.weather.openweather.OpenWeatherApiCallback;
import org.rcgonzalezf.weather.openweather.network.OpenWeatherApiRequestParameters;
import rcgonzalezf.org.weather.adapters.ModelAdapter;
import rcgonzalezf.org.weather.common.BaseActivity;

import static org.mockito.Mockito.mock;

@RunWith(JMockit.class) public class WeatherListActivityTest {

  @Tested private WeatherListActivity uut;

  @SuppressWarnings("unused") @Mocked private BaseActivity mBaseActivity;
  @SuppressWarnings("unused") @Mocked private RecyclerView mRecyclerView;
  @SuppressWarnings("unused") @Mocked private ModelAdapter<Forecast> mAdapter;
  @SuppressWarnings("unused") @Mocked private OpenWeatherApiCallback mOpenWeatherApiCallback;

  @SuppressWarnings("unused") @Mocked
  private OpenWeatherApiRequestParameters.OpenWeatherApiRequestBuilder
      mOpenWeatherApiRequestBuilder;
  @SuppressWarnings("unused") @Mocked
  private WeatherRepository<OpenWeatherApiRequestParameters, OpenWeatherApiCallback>
      mWeatherRepository;
  @SuppressWarnings("unused") @Mocked private ServiceConfig mServiceConfig;
  private List<Forecast> mForecastList;
  private String mQuery;
  private Runnable mNotifyAdapterRunnable;

  @Before public void setUp() throws Exception {
    uut = new WeatherListActivity();
  }

  @Test public void shouldSetUpRecyclerViewOnCreation() {

    whenCreatingTheActivity();

    thenAdapterShouldBeInitialized();
    thenRecyclerViewShouldBeInitialized();
  }

  @Test public void shouldScheduleLayoutAnimationOnAnimationComplete() {
    givenActivityCreated();

    whenEnteringAnimationComplete();

    shouldScheduleLayoutAnimation();
  }

  @Test public void shouldHandleItemClick(@Mocked Toast toast) {
    givenStringResource();

    whenClickingItem();

    thenToastShouldMakeText(toast);
  }

  @Test public void shouldLoadOldData() {
    givenForecastList();
    givenForecastElement();

    whenLoadingOldData();

    thenBaseActivityShouldPostRunnableOnUiThread();
    thenNotifyAdapterRunnableShouldBeCreated();
  }

  @Test
  public void shouldNotLoadOldForNullList(@SuppressWarnings("UnusedParameters") @Mocked Log log) {
    whenLoadingOldData();

    thenLogDataShouldBeWritten("No data even in offline mode :(");
  }

  @Test
  public void shouldNotLoadOldForEmptyList(@SuppressWarnings("UnusedParameters") @Mocked Log log) {
    givenForecastList();

    whenLoadingOldData();

    thenLogDataShouldBeWritten("No data even in offline mode :(");
  }

  @Test public void shouldNotifyAdapterOnUpdatingList() {
    givenForecastList();
    givenForecastElement();

    whenUpdatingList();

    thenBaseActivityShouldPostRunnableOnUiThread();
    thenNotifyAdapterRunnableShouldBeCreated();
  }

  @Test public void shouldHandleError(@SuppressWarnings("UnusedParameters") @Mocked Log log) {

    final String givenErrorString = "Some Error String";

    whenHandlingError(givenErrorString);

    thenLogDataShouldBeWritten(givenErrorString);
  }

  @Test public void shouldBuildWithCityNameOnSearchingByQuery(@Mocked Editable editable,
      @SuppressWarnings("UnusedParameters") @Mocked Toast toast) {
    givenQuery("Some City Name");

    whenSearchingByQuery(editable);

    thenBuilderShouldAddCityName();
    thenWeatherRepositoryShouldFindWeather();
  }

  @Test public void shouldBuildWithLatLonOnSearchingByLocation() {
    double givenLat = 1d;
    double givenLon = 1d;

    whenSearchingByLocation(givenLat, givenLon);

    thenBuilderShouldAddLatLon(givenLat, givenLon);
    thenWeatherRepositoryShouldFindWeather();
  }

  @Test public void shouldNotifyDataSetChangeOnRunningTheNotifyRunnable() {
    givenActivityCreated();
    givenForecastList();
    givenNotifyRunnable();

    whenRunningNotifyRunnable();

    thenAdapterShouldNotifyDataSetChanges();
  }

  private void thenNotifyAdapterRunnableShouldBeCreated() {
    new Verifications() {{
      uut.createNotifyRunnable(withAny(mForecastList));
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
    mNotifyAdapterRunnable = uut.createNotifyRunnable(mForecastList);
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
      mWeatherRepository.findWeather(withAny(mock(OpenWeatherApiRequestParameters.class)),
          withAny(mOpenWeatherApiCallback));
    }};
  }

  private void thenBuilderShouldAddCityName() {
    new Verifications() {{
      mOpenWeatherApiRequestBuilder.withCityName(withEqual(mQuery));
      mOpenWeatherApiRequestBuilder.build();
    }};
  }

  private void whenSearchingByQuery(Editable editable) {
    uut.searchByQuery(mQuery, editable);
  }

  private void givenQuery(String query) {
    mQuery = query;
  }

  private void whenUpdatingList() {
    uut.updateList(mForecastList);
  }

  private void thenLogDataShouldBeWritten(final String message) {
    new Verifications() {{
      Log.d(withAny("string"), message);
    }};
  }

  private void thenBaseActivityShouldPostRunnableOnUiThread() {
    new Verifications() {{
      mBaseActivity.runOnUiThread(withAny(mock(Runnable.class)));
    }};
  }

  private void whenLoadingOldData() {
    uut.loadOldData(mForecastList);
  }

  private void givenForecastElement() {
    mForecastList.add(new Forecast());
  }

  private void givenForecastList() {
    mForecastList = new ArrayList<>();
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

  private void whenClickingItem() {
    uut.onItemClick(mock(View.class), mock(WeatherViewModel.class));
  }

  private void givenActivityCreated() {
    uut.onCreate(new Bundle());
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
      new ModelAdapter<>(new ArrayList<Forecast>(), uut);
      mAdapter.setOnItemClickListener(uut);
    }};
  }

  private void whenCreatingTheActivity() {
    uut.onCreate(new Bundle());
  }
}