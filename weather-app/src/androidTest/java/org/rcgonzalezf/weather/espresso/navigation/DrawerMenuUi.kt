package org.rcgonzalezf.weather.espresso.navigation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers
import rcgonzalezf.org.weather.R
import rcgonzalezf.org.weather.R.id

class DrawerMenuUi {
  class Navigations {
    fun openDrawer() {
      onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
    }
  }

  class Verifications {
    fun containsCityMenu() {
      // Based on espresso recorder
      onView(
          Matchers.allOf(
              withId(id.design_menu_item_text),
              childAtPosition(
                  childAtPosition(withId(id.design_navigation_view), 2),
                  0
              ),
              withText(R.string.action_cities)
          )
      ).check(matches(isDisplayed()))
    }
  }
}