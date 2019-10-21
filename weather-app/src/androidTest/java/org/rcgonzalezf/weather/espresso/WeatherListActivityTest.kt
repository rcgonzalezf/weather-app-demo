package org.rcgonzalezf.weather.espresso


import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.rcgonzalezf.weather.espresso.navigation.EnterCityManuallyUi
import org.rcgonzalezf.weather.espresso.navigation.WeatherListUi
import rcgonzalezf.org.weather.WeatherListActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
class WeatherListActivityTest {

    private lateinit var uut: WeatherListUi.Verifications
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(WeatherListActivity::class.java)

    @Rule
    @JvmField
    var grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION")

    @Before
    fun setUp() {
        uut = WeatherListUi.Verifications()
    }

    @Test
    fun manualEntryShouldRefreshList() {
        // Opening the Dialog and accepting the default entry,
        // right now is hardcoded as "Portland, US"
        WeatherListUi.Navigations().openDialog()
        EnterCityManuallyUi.Actions().pressOkButton()


        uut.checkWeatherResultCity("Portland, US")
    }
}
