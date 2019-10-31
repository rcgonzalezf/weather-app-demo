package org.rcgonzalezf.weather.espresso.navigation

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.allOf
import rcgonzalezf.org.weather.R

class EnterCityManuallyUi {

    class Actions {
        internal fun pressOkButton() {
            onView(allOf(withId(android.R.id.button1),
                    withText("OK"),
                    childAtPosition(childAtPosition(withId(R.id.buttonPanel), 0), 3)))
                    .perform(scrollTo(), click())
        }
    }
}
