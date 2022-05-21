package ufc.erv.garden

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.core.StringContains.containsString

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import ufc.erv.garden.data.Plant

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PlantItemTest {

    /**
     * Use [activityScenarioRule] to create and launch the activity under test before each test,
     * and close it after each test.
     */
    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun showsCorrectMockText() {
        // val context = InstrumentationRegistry.getInstrumentation().targetContext
        val plant = Plant(id="0", popularName="Beijo pintado", scientificName="Impatiens hawkeri", description="Regas frequentes (2 a 3 vezes por semana). Luminosidade: meia-sombra. Plantar em jardineiras ou canteiros", localization="Varandas")
        onView(withText(containsString(plant.popularName))).check(matches(withText(containsString(plant.popularName))))
        onView(withText(containsString(plant.scientificName))).check(matches(withText(containsString(plant.scientificName))))
        onView(withText(containsString(plant.description))).check(matches(withText(containsString(plant.description))))
        onView(withText(containsString(plant.localization))).check(matches(withText(containsString(plant.localization))))
    }
}