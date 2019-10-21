package org.rcgonzalezf.weather.espresso.navigation

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matchers
import rcgonzalezf.org.weather.R

class EnterCityManuallyUi {

    class Actions {
        internal fun pressOkButton() {
            val appCompatButton = Espresso.onView(
                    Matchers.allOf(ViewMatchers.withId(android.R.id.button1), ViewMatchers.withText("OK"),
                            childAtPosition(
                                    childAtPosition(
                                            ViewMatchers.withId(R.id.buttonPanel),
                                            0),
                                    3)))
            appCompatButton.perform(ViewActions.scrollTo(), ViewActions.click())
        }
    }
}
