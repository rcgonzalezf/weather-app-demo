package org.rcgonzalezf.weather.espresso.navigation

import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers
import org.rcgonzalezf.weather.espresso.utils.RecyclerViewMatcher
import rcgonzalezf.org.weather.R

class WeatherListUi {

    class Navigations {
        internal fun openDialog() {
            val floatingActionButton = Espresso.onView(
                    Matchers.allOf(ViewMatchers.withId(R.id.main_fab),
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
            onView(withId(R.id.main_recycler_view))
            onView(RecyclerViewMatcher(R.id.main_recycler_view).atPosition(1))
                    .check(matches(hasDescendant(withText(expectedCity))))
        }
    }
}
