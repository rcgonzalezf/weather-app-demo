package rcgonzalezf.org.weather.settings;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcgonzalezf.org.weather.SettingsActivity;

import static android.R.id.list;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@LargeTest @RunWith(AndroidJUnit4.class) public class ChangingUnitsToFahrenheit {

  @Rule public ActivityTestRule<SettingsActivity> mActivityTestRule =
      new ActivityTestRule<>(SettingsActivity.class);

  @Before public void preparePreferences() {
    clearPreferences();
  }

  @After public void resetPreferences() {
    clearPreferences();
  }

  @Test public void changingUnitsToFahrenheit() {
    givenGeneralSettings();

    whenChangingTheUnits();

    thenSwitchShouldChangeToFahrenheit();
  }

  @SuppressLint("CommitPrefEdits") private void clearPreferences() {
    SharedPreferences settings =
        PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
    SharedPreferences.Editor editor = settings.edit();
    editor.clear();
    editor.commit();
  }

  private void thenSwitchShouldChangeToFahrenheit() {
    onView(IsInstanceOf.<View>instanceOf(android.widget.Switch.class)).check(
        matches(not(isChecked())));
  }

  private void whenChangingTheUnits() {
    onView(allOf(childAtPosition(withId(list), 0), isDisplayed())).perform(click());
  }

  private void givenGeneralSettings() {
    onView(allOf(childAtPosition(
        allOf(withId(list), withParent(withClassName(is("android.widget.LinearLayout")))), 0),
        isDisplayed())).perform(click());
  }

  private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher,
      final int position) {

    return new TypeSafeMatcher<View>() {
      @Override public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(
            ((ViewGroup) parent).getChildAt(position));
      }
    };
  }
}
