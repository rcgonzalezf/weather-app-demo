package rcgonzalezf.org.weather.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.rcgonzalezf.weather.WeatherLibApp;
import org.rcgonzalezf.weather.common.models.WeatherInfo;
import org.rcgonzalezf.weather.common.models.WeatherViewModel;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import rcgonzalezf.org.weather.R;
import rcgonzalezf.org.weather.SettingsActivity;
import rcgonzalezf.org.weather.databinding.WeatherRowBinding;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JMockit.class)
public class ModelAdapterTest {

    @Tested
    private ModelAdapter<WeatherInfo> uut;

    private List<WeatherInfo> models;
    @Mocked
    WeatherLibApp weatherLibApp;
    @Mocked
    private Context context;
    @Mocked
    private RecyclerView.ViewHolder recyclerViewHolder;
    @Mocked
    private TextView textView;
    @Mocked
    private SharedPreferences sharedPreferences;
    @Mocked
    private PreferenceManager preferenceManager;
    private WeatherModelViewHolder modelViewHolder;
    private int itemCount;
    private Runnable itemClickListenerRunnable;
    private ModelAdapter.OnItemClickListener itemClickListener;

    @Before
    public void setUp() throws Exception {
        models = new ArrayList<>();
        uut = new ModelAdapter<>(models);
    }

    @SuppressWarnings("UnusedParameters")
    @Test
    public void shouldCreateViewHolder(@Mocked LayoutInflater layoutInflater, @Mocked View view,
                                       @Mocked WeatherRowBinding weatherRowBinding) {
        givenViewsFound(view);

        whenCreatingViewHolder();

        thenModelViewHolderIsNotNull();
    }

    @SuppressWarnings("UnusedParameters")
    @Test
    public void shouldBindViewHolder(@Mocked WeatherInfo weatherInfo, @Mocked View view,
                                     @Mocked LayoutInflater layoutInflater,
                                     @Mocked WeatherRowBinding weatherRowBinding) {
        givenViewHolder(view);
        givenModel(weatherInfo);
        givenSharedPreferenceForCelsius(true);

        whenBindingViewHolder();

        thenShouldBindModelDataToViewHolder(weatherInfo);
    }

    @SuppressWarnings("UnusedParameters")
    @Test
    public void shouldPopulateTemperatureViewInFahrenheit
            (@Mocked WeatherInfo weatherInfo,
             @Mocked View view,
             @Mocked LayoutInflater layoutInflater,
             @Mocked WeatherRowBinding weatherRowBinding) {
        givenViewHolder(view);
        givenSharedPreferenceForCelsius(false);

        whenPopulatingTheTemperatureViews(weatherInfo);

        thenShouldBindModelTemperatureToViewHolder(weatherInfo);
    }

    @Test
    public void shouldGetItemCount(@Mocked WeatherInfo weatherInfo) {
        givenModel(weatherInfo);

        whenGettingItemCount();

        thenItemCountShouldBe(1);
    }

    @SuppressWarnings("UnusedParameters")
    @Test
    public void shouldNotifyDataSetChangedWhenSettingItems(
            @Mocked RecyclerView.Adapter<WeatherModelViewHolder> adapter) {

        whenSettingItemsList();

        thenNotifyDataSetChanged();
    }

    @Test
    public void shouldPostDelayedItemClickOnClickForNonNullItemClickListener(
            @SuppressWarnings("UnusedParameters") @Mocked Handler handler) {
        givenItemClickListener();

        whenClicking();

        thenHandlerShouldPostRunnable();
    }

    @Test
    public void shouldNotPostDelayedItemClickOnClickForNullItemClickListener(
            @SuppressWarnings("UnusedParameters") @Mocked Handler handler) {

        whenClicking();

        thenNoInteractionsOnHandler(handler);
    }

    @Test
    public void shouldCallItemClickListenerOnItemClick() {
        givenItemClickListener();
        givenRunnable();

        whenRunning();

        thenItemClickListenerShouldHandleItemClick();
    }

    private void thenItemClickListenerShouldHandleItemClick() {
        //noinspection unchecked
        verify(itemClickListener, times(1)).onItemClick(eq(textView),
                any(WeatherViewModel.class));
    }

    private void whenRunning() {
        itemClickListenerRunnable.run();
    }

    private void givenRunnable() {
        new Expectations() {{
            textView.getTag();
            result = Mockito.mock(WeatherViewModel.class);
        }};
        itemClickListenerRunnable = uut.createClickRunnable(textView);
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
        uut.onClick(textView);
    }

    private void givenItemClickListener() {
        itemClickListener = mock(ModelAdapter.OnItemClickListener.class);
        uut.setOnItemClickListener(itemClickListener);
    }

    @Test
    public void shouldSetItemClickListener() {
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
        uut.setItems(models);
    }

    private void thenItemCountShouldBe(int expected) {
        assertEquals(expected, itemCount);
    }

    private void whenGettingItemCount() {
        itemCount = uut.getItemCount();
    }

    private void thenShouldBindModelTemperatureToViewHolder(final WeatherInfo weatherInfo) {
        new Verifications() {{
            weatherInfo.getTemperature();
        }};
    }

    private void whenPopulatingTheTemperatureViews(WeatherInfo weatherInfo) {
        uut.populateTemperatureViews(context, modelViewHolder, weatherInfo);
    }

    private void givenSharedPreferenceForCelsius(final boolean celsiusPreferred) {
        new Expectations() {{
            PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.getBoolean(SettingsActivity.PREF_TEMPERATURE_UNITS, true);
            result = celsiusPreferred;
        }};
    }

    private void thenShouldBindModelDataToViewHolder(final WeatherInfo weatherInfo) {
        new Verifications() {{
            weatherInfo.getTemperature();
        }};
    }

    private void whenBindingViewHolder() {
        uut.onBindViewHolder(modelViewHolder, 0);
    }

    private void givenModel(WeatherInfo weatherInfo) {
        models.add(weatherInfo);
    }

    private void givenViewHolder(View view) {
        givenViewsFound(view);
        modelViewHolder = uut.onCreateViewHolder(mock(ViewGroup.class), 0);
    }

    private void givenViewsFound(final View view) {
        new Expectations() {{
            view.findViewById(R.id.secondary_temperature_text_view);
            result = textView;
            view.findViewById(R.id.preferred_temperature_text_view);
            result = textView;
        }};
    }

    private void thenModelViewHolderIsNotNull() {
        assertNotNull(modelViewHolder);
    }

    private void whenCreatingViewHolder() {
        modelViewHolder = uut.onCreateViewHolder(mock(ViewGroup.class), 0);
    }
}
