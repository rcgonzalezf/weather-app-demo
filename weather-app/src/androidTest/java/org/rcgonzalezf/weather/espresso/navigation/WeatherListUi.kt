package org.rcgonzalezf.weather.espresso.navigation

import android.support.test.espresso.Espresso.onIdle
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.allOf
import org.rcgonzalezf.weather.espresso.utils.RecyclerViewMatcher
import rcgonzalezf.org.weather.R

class WeatherListUi {

    class Navigations {
        internal fun openDialog() {
            onView(allOf(withId(R.id.main_fab),
                    childAtPosition(allOf(withId(R.id.content),
                            childAtPosition(withId(R.id.drawer_layout), 0)), 3),
                    isDisplayed()))
                    .perform(click())
        }
    }

    class Verifications {
        internal fun checkWeatherResultCity(expectedCity: String) {
            onIdle()
            onView(RecyclerViewMatcher(R.id.main_recycler_view).atPosition(1))
                    .check(matches(hasDescendant(withText(expectedCity))))
        }
    }
}
