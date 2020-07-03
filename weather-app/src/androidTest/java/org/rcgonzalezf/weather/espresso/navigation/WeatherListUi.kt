package org.rcgonzalezf.weather.espresso.navigation

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers
import org.rcgonzalezf.weather.espresso.utils.RecyclerViewMatcher
import rcgonzalezf.org.weather.R

class WeatherListUi {

    class Navigations {
        internal fun openDialog() {
            val floatingActionButton = Espresso.onView(
                    Matchers.allOf(
                        ViewMatchers.withId(R.id.main_fab),
                            childAtPosition(
                                    Matchers.allOf(ViewMatchers.withId(R.id.content),
                                            childAtPosition(
                                                    ViewMatchers.withId(R.id.drawer_layout),
                                                    0)),
                                    3),
                            ViewMatchers.isDisplayed()))
            floatingActionButton.perform(ViewActions.click())
        }
    }

    class Verifications {
        internal fun checkWeatherResultCity(expectedCity: String) {
            onIdle()
            // TODO what next onView is doing?
            onView(withId(R.id.main_recycler_view))
            onView(RecyclerViewMatcher(R.id.main_recycler_view).atPosition(1))
                    .check(matches(hasDescendant(withText(expectedCity))))
        }
    }
}
