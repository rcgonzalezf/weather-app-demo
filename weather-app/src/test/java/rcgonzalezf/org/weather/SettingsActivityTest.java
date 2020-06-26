package rcgonzalezf.org.weather;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import androidx.core.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
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
import rcgonzalezf.org.weather.common.analytics.AnalyticsManager;

import static org.junit.Assert.assertEquals;

@RunWith(JMockit.class) public class SettingsActivityTest {

  @Tested private SettingsActivity uut;

  @SuppressWarnings("unused") @Mocked private PreferenceActivity mPreferenceActivity;
  @SuppressWarnings("unused") @Mocked private AppCompatDelegate mAppCompatDelegate;
  @SuppressWarnings("unused") @Mocked private AnalyticsManager mAnalyticsManager;
  private boolean mOnMenuItemSelected;
  private boolean mIsMultiPane;

  @Before public void setUp() {
    uut = new SettingsActivity();
  }

  @Test public void shouldShowUpButtonInActionBar(@Mocked ActionBar actionBar) {

    whenCreatingSettingsActivity();

    thenActionBarShouldShowUpButton(actionBar);
  }

  @Test public void shouldNotCrashIfActionBarIsNull(@Mocked ActionBar actionBar) {
    givenNullActionBar();

    whenCreatingSettingsActivity();

    thenNoMoreInteractionsWithActionBar(actionBar);
  }

  @Test public void shouldNavigateUpFromSameTask(@Mocked MenuItem menuItem,
      @SuppressWarnings("UnusedParameters") @Mocked NavUtils navUtils) {
    givenHomeItemId(menuItem);
    givenOnMenuItemSelectedFromSuper(false, menuItem);

    whenMenuItemSelected(menuItem);

    shouldHandleMenuItemSelected(true);
    shouldNavigateUp();
  }

  @Test public void shouldDelegateOnMenuItemSelectedToSuper(@Mocked MenuItem menuItem) {

    whenMenuItemSelected(menuItem);

    shouldHandleMenuItemSelected(false);
  }

  @Test public void shouldHandleOnMenuItemSelectedButNotUseNavUtilsIfParentHandlesEvent(
      @Mocked MenuItem menuItem) {
    givenHomeItemId(menuItem);
    givenOnMenuItemSelectedFromSuper(true, menuItem);

    whenMenuItemSelected(menuItem);

    shouldHandleMenuItemSelected(true);
  }

  @Test public void shouldShowMultiPaneForXLargeConfiguration(@Mocked Configuration configuration,
      @Mocked Resources resources) {
    givenScreenLayoutXLarge(configuration, resources);

    whenCheckingIfMultiPane();

    thenIsMultipaneShouldBe(true);
  }

  @Test
  public void shouldNotShowMultiPaneForNonXLargeConfiguration(@Mocked Configuration configuration,
      @Mocked Resources resources) {
    givenScreenLayoutNonXLarge(configuration, resources);

    whenCheckingIfMultiPane();

    thenIsMultipaneShouldBe(false);
  }

  @Test public void shouldLoadHeadersFromResourcesOnBuildingHeaders(
      @Mocked List<PreferenceActivity.Header> target) {
    whenBuildingHeaders(target);

    thenShouldLoadHeadersFromResources(target);
  }

  private void thenShouldLoadHeadersFromResources(final List<PreferenceActivity.Header> target) {
    new Verifications() {{
      mPreferenceActivity.loadHeadersFromResource(R.xml.pref_headers, target);
    }};
  }

  private void whenBuildingHeaders(List<PreferenceActivity.Header> target) {
    uut.onBuildHeaders(target);
  }

  private void givenScreenLayoutNonXLarge(final Configuration configuration,
      final Resources resources) {
    new Expectations() {{
      uut.getResources(); result = resources;
      resources.getConfiguration(); result = configuration;
    }};
    configuration.screenLayout = 2;
  }

  private void givenScreenLayoutXLarge(final Configuration configuration,
      final Resources resources) {
    new Expectations() {{
      uut.getResources(); result = resources;
      resources.getConfiguration(); result = configuration;
    }};
    configuration.screenLayout = 5;
  }

  private void thenIsMultipaneShouldBe(boolean expected) {
    assertEquals(expected, mIsMultiPane);
  }

  private void whenCheckingIfMultiPane() {
    mIsMultiPane = uut.onIsMultiPane();
  }

  private void shouldNavigateUp() {
    new Verifications() {{
      NavUtils.navigateUpFromSameTask(uut);
    }};
  }

  private void shouldHandleMenuItemSelected(boolean expected) {
    assertEquals(expected, mOnMenuItemSelected);
  }

  private void whenMenuItemSelected(MenuItem menuItem) {
    mOnMenuItemSelected = uut.onMenuItemSelected(1, menuItem);
  }

  private void givenOnMenuItemSelectedFromSuper(final boolean onMenuItemSelectedOnSuper,
      final MenuItem menuItem) {
    new Expectations() {{
      mPreferenceActivity.onMenuItemSelected(1, withAny(menuItem));
      result = onMenuItemSelectedOnSuper;
    }};
  }

  private void givenHomeItemId(final MenuItem menuItem) {
    new Expectations() {{
      menuItem.getItemId();
      result = android.R.id.home;
    }};
  }

  private void thenNoMoreInteractionsWithActionBar(ActionBar actionBar) {
    new FullVerifications(actionBar) {
    };
  }

  private void givenNullActionBar() {
    new Expectations() {{
      mAppCompatDelegate.getSupportActionBar();
      result = null;
    }};
  }

  private void thenActionBarShouldShowUpButton(final ActionBar actionBar) {
    new Verifications() {{
      actionBar.setDisplayHomeAsUpEnabled(true);
    }};
  }

  private void whenCreatingSettingsActivity() {
    uut.onCreate(new Bundle());
  }
}