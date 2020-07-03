package org.rcgonzalezf.weather.espresso

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.rcgonzalezf.weather.espresso.navigation.CityListUi
import org.rcgonzalezf.weather.espresso.navigation.DrawerMenuUi
import rcgonzalezf.org.weather.WeatherListActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
class CityListActivityTest {

  private lateinit var uut: CityListUi.Verifications

  @Rule
  @JvmField
  var activityTestRule = ActivityTestRule(WeatherListActivity::class.java)

  @Rule
  @JvmField
  var grantPermissionRule: GrantPermissionRule =
    GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION")

  @Before
  fun setUp() {
    uut = CityListUi.Verifications()
  }

  @Test
  fun drawerShouldContainCitiesMenu() {
    DrawerMenuUi.Navigations()
        .openDrawer()
    DrawerMenuUi.Verifications()
        .containsCityMenu()
  }
}
