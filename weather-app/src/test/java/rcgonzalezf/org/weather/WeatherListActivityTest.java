package rcgonzalezf.org.weather;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.io.IOException;
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
import rcgonzalezf.org.weather.common.analytics.AnalyticsEvent;

import static org.mockito.Mockito.mock;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.LOCATION_SEARCH;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.NO_NETWORK_SEARCH;
import static rcgonzalezf.org.weather.common.analytics.AnalyticsDataCatalog.WeatherListActivity.SEARCH_COMPLETED;

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
  @SuppressWarnings("unused") @Mocked AnalyticsEvent mAnalyticsEvent;
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
    givenForecastElement("someCity");

    whenLoadingOldData();

    thenBaseActivityShouldPostRunnableOnUiThread();
    thenNotifyAdapterRunnableShouldBeCreated();
    thenShouldTrackEvent(NO_NETWORK_SEARCH, "someCity");
  }

  @Test
  public void shouldNotLoadOldForNullList(@SuppressWarnings("UnusedParameters") @Mocked Log log) {
    whenLoadingOldData();

    thenLogDataShouldBeWritten("No data even in offline mode :(");
    thenShouldTrackEvent(NO_NETWORK_SEARCH, "EMPTY");
  }

  @Test
  public void shouldNotLoadOldForEmptyList(@SuppressWarnings("UnusedParameters") @Mocked Log log) {
    givenForecastList();

    whenLoadingOldData();

    thenLogDataShouldBeWritten("No data even in offline mode :(");
    thenShouldTrackEvent(NO_NETWORK_SEARCH, "EMPTY");
  }

  @Test public void shouldNotifyAdapterOnUpdatingListWithNullCity() {
    givenForecastList();
    givenForecastElement("someCity");

    whenUpdatingList();

    thenBaseActivityShouldPostRunnableOnUiThread();
    thenNotifyAdapterRunnableShouldBeCreated();
    thenShouldTrackEvent(SEARCH_COMPLETED, "cityName: " + "someCity");
  }

  @Test public void shouldNotifyAdapterOnUpdatingListWithEmptyCityForEmptyList() {
    givenForecastList();

    whenUpdatingList();

    thenBaseActivityShouldPostRunnableOnUiThread();
    thenNotifyAdapterRunnableShouldBeCreated();
    thenShouldTrackEvent(SEARCH_COMPLETED, "cityName: " + "");
  }

  @Test public void shouldHandleError(@SuppressWarnings("UnusedParameters") @Mocked Log log) {

    final String givenErrorString = "Some Error String";

    whenHandlingError(givenErrorString);

    thenLogDataShouldBeWritten(givenErrorString);
    thenShouldTrackEvent(SEARCH_COMPLETED, "error: " + givenErrorString);
  }

  @Test public void shouldBuildWithCityNameOnSearchingByQuery(@Mocked Editable editable,
      @SuppressWarnings("UnusedParameters") @Mocked Toast toast) {
    givenQuery("Some City Name");

    whenSearchingByQuery(editable);

    thenBuilderShouldAddCityName();
    thenWeatherRepositoryShouldFindWeather();
  }

  @Test public void shouldBuildWithLatLonOnSearchingByLocation(@Mocked Geocoder geocoder)
      throws IOException {
    double givenLat = 1d;
    double givenLon = 1d;
    givenGeocoderThrowsException(geocoder, givenLat, givenLon);

    whenSearchingByLocation(givenLat, givenLon);

    thenBuilderShouldAddLatLon(givenLat, givenLon);
    thenWeatherRepositoryShouldFindWeather();
    thenShouldTrackEvent(LOCATION_SEARCH, "Geocoder Failure");
  }

  @Test public void shouldNotifyDataSetChangeOnRunningTheNotifyRunnable() {
    givenActivityCreated();
    givenForecastList();
    givenNotifyRunnable();

    whenRunningNotifyRunnable();

    thenAdapterShouldNotifyDataSetChanges();
  }

  @Test public void shouldBuildWithCityNameOnSearchingByLocationWithGeocoderCity(
      @Mocked Geocoder geocoder, @Mocked Address address) throws IOException {
    double givenLat = 1d;
    double givenLon = 1d;
    givenQuery("Some City Name");
    givenGeocoderCity(geocoder, address, givenLat, givenLon);

    whenSearchingByLocation(givenLat, givenLon);

    thenBuilderShouldAddCityName();
    thenWeatherRepositoryShouldFindWeather();
    thenShouldTrackEvent(LOCATION_SEARCH, mQuery);
  }

  private void thenShouldTrackEvent(final String eventName, final String additionalDetails) {
    new Verifications() {{
      //noinspection WrongConstant
      new AnalyticsEvent(withEqual(eventName), withEqual(additionalDetails));
      mBaseActivity.trackOnActionEvent(withAny(mAnalyticsEvent));
    }};
  }

  private void givenGeocoderCity(final Geocoder geocoder, final Address address, final double lat,
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

  private void givenGeocoderThrowsException(final Geocoder geocoder, final double lat,
      final double lon) throws IOException {
    new Expectations() {{
      geocoder.getFromLocation(lat, lon, 1);
      result = new IndexOutOfBoundsException("");
    }};
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

  private void givenForecastElement(String cityName) {
    Forecast forecast = new Forecast();
    forecast.setCityName(cityName);
    mForecastList.add(forecast);
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