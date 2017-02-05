package rcgonzalezf.org.weather.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcgonzalezf.weather.WeatherLibApp;
import org.rcgonzalezf.weather.common.models.Forecast;
import org.rcgonzalezf.weather.common.models.WeatherViewModel;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.SettingsActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JMockit.class) public class ModelAdapterTest {

  @Tested private ModelAdapter<Forecast> uut;

  private List<Forecast> mModels;
  @Mocked WeatherLibApp mWeatherLipApp;
  @Mocked private Context mContext;
  @Mocked private RecyclerView.ViewHolder mRecyclerViewHolder;
  @Mocked private TextView mTextView;
  @Mocked private SharedPreferences mSharedPreferences;
  @Mocked private PreferenceManager mPreferenceManager;
  private ModelAdapter.ModelViewHolder mModelViewHolder;
  private int mItemCount;
  private Runnable mItemClickListenerRunnable;
  private ModelAdapter.OnItemClickListener mItemClickListener;

  @Before public void setUp() throws Exception {
    mModels = new ArrayList<>();
    uut = new ModelAdapter<>(mModels, mContext);
  }

  @SuppressWarnings("UnusedParameters") @Test
  public void shouldCreateViewHolder(@Mocked LayoutInflater layoutInflater, @Mocked View view) {
    givenViewsFound(view);

    whenCreatingViewHolder();

    thenModelViewHolderIsNotNull();
    thenItemViewShouldListenToClicks(view);
  }

  @SuppressWarnings("UnusedParameters") @Test
  public void shouldBindViewHolder(@Mocked Forecast forecast, @Mocked View view,
      @Mocked LayoutInflater layoutInflater) {
    givenViewHolder(view);
    givenModel(forecast);
    givenDateTime(forecast);
    givenResourceStrings();
    givenSharedPreferenceForCelsius(true);

    whenBindingViewHolder();

    thenShouldBindModelDataToViewHolder(forecast);
  }

  @SuppressWarnings("UnusedParameters") @Test
  public void shouldPopulateTemperatureViewInFahrenheit(@Mocked Forecast forecast,
      @Mocked View view, @Mocked LayoutInflater layoutInflater) {
    givenViewHolder(view);
    givenSharedPreferenceForCelsius(false);

    whenPopulatingTheTemperatureViews(forecast);

    thenShouldBindModelTemperatureToViewHolder(forecast);
  }

  @Test public void shouldGetItemCount(@Mocked Forecast forecast) {
    givenModel(forecast);

    whenGettingItemCount();

    thenItemCountShouldBe(1);
  }

  @SuppressWarnings("UnusedParameters") @Test
  public void shouldNotifyDataSetChangedWhenSettingItems(
      @Mocked RecyclerView.Adapter<ModelAdapter.ModelViewHolder> adapter) {

    whenSettingItemsList();

    thenNotifyDataSetChanged();
  }

  @Test public void shouldPostDelayedItemClickOnClickForNonNullItemClickListener(
      @SuppressWarnings("UnusedParameters") @Mocked Handler handler) {
    givenItemClickListener();

    whenClicking();

    thenHandlerShouldPostRunnable();
  }

  @Test public void shouldNotPostDelayedItemClickOnClickForNullItemClickListener(
      @SuppressWarnings("UnusedParameters") @Mocked Handler handler) {

    whenClicking();

    thenNoInteractionsOnHandler(handler);
  }

  @Test public void shouldCallItemClickListenerOnItemClick() {
    givenItemClickListener();
    givenRunnable();

    whenRunning();

    thenItemClickListenerShouldHandleItemClick();
  }

  private void thenItemClickListenerShouldHandleItemClick() {
    //noinspection unchecked
    verify(mItemClickListener, times(1)).onItemClick(eq(mTextView), any(WeatherViewModel.class));
  }

  private void whenRunning() {
    mItemClickListenerRunnable.run();
  }

  private void givenRunnable() {
    mItemClickListenerRunnable = uut.createClickRunnable(mTextView);
  }

  private void thenNoInteractionsOnHandler(Handler handler) {
    new FullVerifications(handler) {
    };
  }

  private void thenHandlerShouldPostRunnable() {
    new Verifications() {{
      new Handler().postDelayed(withAny(mock(Runnable.class)), 200);
    }};
  }

  private void whenClicking() {
    uut.onClick(mTextView);
  }

  private void givenItemClickListener() {
    mItemClickListener = mock(ModelAdapter.OnItemClickListener.class);
    uut.setOnItemClickListener(mItemClickListener);
  }

  @Test public void shouldSetItemClickListener() {
    whenSettingOnItemClickListener();
  }

  private void whenSettingOnItemClickListener() {
    uut.setOnItemClickListener(mock(ModelAdapter.OnItemClickListener.class));
  }

  private void thenNotifyDataSetChanged() {
    new Verifications() {{
      uut.notifyDataSetChanged();
    }};
  }

  private void whenSettingItemsList() {
    uut.setItems(mModels);
  }

  private void thenItemCountShouldBe(int expected) {
    assertEquals(expected, mItemCount);
  }

  private void whenGettingItemCount() {
    mItemCount = uut.getItemCount();
  }

  private void thenShouldBindModelTemperatureToViewHolder(final Forecast forecast) {
    new Verifications() {{
      forecast.getTemperature();
    }};
  }

  private void whenPopulatingTheTemperatureViews(Forecast forecast) {
    uut.populateTemperatureViews(mModelViewHolder, forecast);
  }

  private void givenSharedPreferenceForCelsius(final boolean celsiusPreferred) {
    new Expectations() {{
      PreferenceManager.getDefaultSharedPreferences(mContext);
      mSharedPreferences.getBoolean(SettingsActivity.PREF_TEMPERATURE_UNITS, true);
      result = celsiusPreferred;
    }};
  }

  private void givenResourceStrings() {
    new Expectations() {{
      WeatherLibApp.getInstance().getString(R.string.location_display_format);
      result = "%1$s, %2$s";
      WeatherLibApp.getInstance().getString(R.string.key_value_display_format);
      result = "%1$s, %2$s";
      WeatherLibApp.getInstance().getString(R.string.humidity);
      result = "Humidity";
    }};
  }

  private void givenDateTime(final Forecast forecast) {
    new Expectations() {{
      forecast.getDateTime();
      result = "2012-09-24 18:00:00";
    }};
  }

  private void thenShouldBindModelDataToViewHolder(final Forecast forecast) {
    new Verifications() {{
      forecast.getWeatherId();
      forecast.getCityName();
      forecast.getCountry();
      forecast.getHumidity();
      forecast.getDeg();
      forecast.getDescription();
      forecast.getTemperature();
      forecast.getSpeed();
    }};
  }

  private void whenBindingViewHolder() {
    uut.onBindViewHolder(mModelViewHolder, 0);
  }

  private void givenModel(Forecast forecast) {
    mModels.add(forecast);
  }

  private void givenViewHolder(View view) {
    givenViewsFound(view);
    mModelViewHolder = uut.onCreateViewHolder(mock(ViewGroup.class), 0);
  }

  private void givenViewsFound(final View view) {
    new Expectations() {{
      view.findViewById(R.id.item_image);
      result = mock(ImageView.class);
      view.findViewById(R.id.datetime_text_view);
      result = mTextView;
      view.findViewById(R.id.detail_location_name);
      result = mTextView;
      view.findViewById(R.id.day_textview);
      result = mTextView;
      view.findViewById(R.id.humidity_text_view);
      result = mTextView;
      view.findViewById(R.id.wind_speed_text_view);
      result = mTextView;
      view.findViewById(R.id.secondary_temperature_text_view);
      result = mTextView;
      view.findViewById(R.id.preferred_temperature_text_view);
      result = mTextView;
      view.findViewById(R.id.description_text_view);
      result = mTextView;
    }};
  }

  private void thenItemViewShouldListenToClicks(final View view) {
    new Verifications() {{
      view.setOnClickListener(withEqual(uut));
    }};
  }

  private void thenModelViewHolderIsNotNull() {
    assertNotNull(mModelViewHolder);
  }

  private void whenCreatingViewHolder() {
    mModelViewHolder = uut.onCreateViewHolder(mock(ViewGroup.class), 0);
  }
}