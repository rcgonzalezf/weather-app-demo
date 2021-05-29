package rcgonzalezf.org.weather;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatDelegate;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.rcgonzalezf.weather.WeatherLibApp;

import rcgonzalezf.org.weather.common.analytics.Analytics;
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager;

@RunWith(JMockit.class) public class AppCompatPreferenceActivityTest {

  @Tested private AppCompatPreferenceActivity uut;

  @Mocked private AppCompatDelegate appCompatDelegate;
  @Mocked private AnalyticsManager analyticsManager;
  @org.mockito.Mock
  private WeatherApp weatherApp;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    WeatherLibApp.setAppInstance(weatherApp);
    uut = new SettingsActivity();
  }

  @Test public void shouldDelegateOnPostCreate() {
    whenPostCreating();

    thenDelegateShouldPostCreate();
  }

  @Test public void shouldDelegateMenuInflater() {
    whenGettingMenuInflater();

    thenDelegateShouldInflateMenu();
  }

  @Test public void shouldDelegateSetContentViewLayout() {
    whenSettingContentView();

    thenDelegateShouldSetContentView();
  }

  @Test public void shouldDelegateSetContentViewWithView(@Mocked View view) {
    whenSettingContentView(view);

    thenDelegateShouldSetContentView(view);
  }

  @Test public void shouldDelegateSetContentViewWithView(@Mocked View view,
      @Mocked ViewGroup.LayoutParams params) {
    whenSettingContentView(view, params);

    thenDelegateShouldSetContentView(view, params);
  }

  @Test public void shouldDelegateAddContentViewWithView(@Mocked View view,
      @Mocked ViewGroup.LayoutParams params) {
    whenAddingContentView(view, params);

    thenDelegateShouldAddContentView(view, params);
  }

  @Test public void shouldDelegateOnPostResume() {
    whenPostResume();

    thenDelegateShouldPostResume();
  }

  @Test public void shouldDelegateOnTitleChange() {
    whenChangingTitle();

    thenDelegateShouldChangeTitle();
  }

  @Test public void shouldDelegateOnConfigurationChanged(@Mocked Configuration newConfig) {
    whenOnConfigurationChanged(newConfig);

    thenDelegateShouldOnConfigurationChanged(newConfig);
  }

  @Test public void shouldDelegateOnStop() {
    whenStopping();

    thenDelegateShouldStop();
  }

  @Test public void shouldDelegateOnDestroy() {
    whenDestroying();

    thenDelegateShouldDestroy();
  }

  @Test public void shouldDelegateOnInvalidateOptionsMenu() {
    whenInvalidatingOptionsMenu();

    thenDelegateShouldInvalidateOptionsMenu();
  }

  @Test public void shouldDelegateOnTrackingScreen(
      @SuppressWarnings("UnusedParameters") @Mocked Analytics analytics) {
    whenTrackingScreen();

    thenDelegateShouldTrackScreen();
  }

  private void thenDelegateShouldTrackScreen() {
    new Verifications() {{
      new Analytics(withAny(analyticsManager)).trackOnScreen("SettingsActivity");
    }};
  }

  private void whenTrackingScreen() {
    uut.trackOnScreen();
  }

  private void thenDelegateShouldInvalidateOptionsMenu() {
    new Verifications() {{
      appCompatDelegate.invalidateOptionsMenu();
    }};
  }

  private void whenInvalidatingOptionsMenu() {
    uut.invalidateOptionsMenu();
  }

  private void thenDelegateShouldDestroy() {
    new Verifications() {{
      appCompatDelegate.onDestroy();
    }};
  }

  private void whenDestroying() {
    uut.onDestroy();
  }

  private void thenDelegateShouldStop() {
    new Verifications() {{
      appCompatDelegate.onStop();
    }};
  }

  private void whenStopping() {
    uut.onStop();
  }

  private void thenDelegateShouldOnConfigurationChanged(final Configuration newConfig) {
    new Verifications() {{
      appCompatDelegate.onConfigurationChanged(newConfig);
    }};
  }

  private void whenOnConfigurationChanged(Configuration newConfig) {
    uut.onConfigurationChanged(newConfig);
  }

  private void thenDelegateShouldChangeTitle() {
    new Verifications() {{
      appCompatDelegate.setTitle("anyTitle");
    }};
  }

  private void whenChangingTitle() {
    uut.onTitleChanged("anyTitle", 1);
  }

  private void thenDelegateShouldPostResume() {
    new Verifications() {{
      appCompatDelegate.onPostResume();
    }};
  }

  private void whenPostResume() {
    uut.onPostResume();
  }

  private void thenDelegateShouldAddContentView(final View view,
      final ViewGroup.LayoutParams params) {
    new Verifications() {{
      appCompatDelegate.addContentView(view, params);
    }};
  }

  private void whenAddingContentView(View view, ViewGroup.LayoutParams params) {
    uut.addContentView(view, params);
  }

  private void thenDelegateShouldSetContentView(final View view,
      final ViewGroup.LayoutParams params) {
    new Verifications() {{
      appCompatDelegate.setContentView(view, params);
    }};
  }

  private void whenSettingContentView(View view, ViewGroup.LayoutParams params) {
    uut.setContentView(view, params);
  }

  private void thenDelegateShouldSetContentView(final View view) {
    new Verifications() {{
      appCompatDelegate.setContentView(view);
    }};
  }

  private void whenSettingContentView(View view) {
    uut.setContentView(view);
  }

  private void thenDelegateShouldSetContentView() {
    new Verifications() {{
      appCompatDelegate.setContentView(R.layout.weather);
    }};
  }

  private void whenSettingContentView() {
    uut.setContentView(R.layout.weather);
  }

  private void thenDelegateShouldInflateMenu() {
    new Verifications() {{
      appCompatDelegate.getMenuInflater();
    }};
  }

  private void whenGettingMenuInflater() {
    uut.getMenuInflater();
  }

  private void thenDelegateShouldPostCreate() {
    new Verifications() {{
      appCompatDelegate.onPostCreate(withAny(new Bundle()));
    }};
  }

  private void whenPostCreating() {
    uut.onPostCreate(new Bundle());
  }
}