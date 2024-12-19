package com.android.streetworkapp.end2end

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.size
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.preferences.PreferencesRepository
import com.android.streetworkapp.model.preferences.PreferencesViewModel
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class End2EndParks {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var parkLocationRepository: OverpassParkLocationRepository
  private lateinit var parkLocationViewModel: ParkLocationViewModel

  private val idlingResource =
      CountingIdlingResource(
          "MapLoading") // this is to make sure the map and markers are loaded for the test

  @Before
  fun setUp() {
    val mockParkList =
        listOf(
            ParkLocation(
                lat = 46.518659400000004,
                lon = 6.566561505148001,
                id = "1"), // TODO: change so that it always equals default cam pos
            ParkLocation(lat = 34.052235, lon = -118.243683, id = "2"),
            ParkLocation(lat = 51.507351, lon = -0.127758, id = "3"),
            ParkLocation(lat = 35.676192, lon = 139.650311, id = "4"),
            ParkLocation(lat = -33.868820, lon = 151.209290, id = "5"))

    parkLocationRepository =
        mockk<OverpassParkLocationRepository>() // TODO: check why interface doens't work
    every {
      parkLocationRepository.search(
          any<Double>(),
          any<Double>(),
          any<(List<ParkLocation>) -> Unit>(),
          any<(Exception) -> Unit>())
    } answers
        {
          val onSuccess = this.args[2] as (List<ParkLocation>) -> Unit
          onSuccess(mockParkList) // Invoke onSuccess with the custom list
        }

    parkLocationViewModel = ParkLocationViewModel(parkLocationRepository)
    IdlingRegistry.getInstance().register(idlingResource)
  }

  @After
  fun cleanUp() {
    // Unregister the Idling Resource
    IdlingRegistry.getInstance().unregister(idlingResource)
  }

  @Ignore("google maps API can be slow sometimes, wouldn't want the CI to fail because of this")
  @Test
  fun E2ECanClickParkAncAccessSpecificParkOverview() {
    idlingResource.increment()
    composeTestRule.setContent {
      StreetWorkApp(
          parkLocationViewModel,
          { navigateTo(Route.MAP) },
          { idlingResource.decrement() },
          UserViewModel(mockk()),
          ParkViewModel(mockk()),
          EventViewModel(mockk()),
          ProgressionViewModel(mockk()),
          WorkoutViewModel(mockk()),
          TextModerationViewModel(mockk()),
          ImageViewModel(mockk()),
          PreferencesViewModel(mock(PreferencesRepository::class.java)),
          GoogleAuthService(
              "abc", mock(FirebaseAuth::class.java, RETURNS_DEFAULTS), LocalContext.current))
      // setup so as we're already on the MAP route
    }

    // Testing we can click on the marker that's already placed at the center of the screen and
    // we're being redirected to its park overview page
    composeTestRule.waitUntil { idlingResource.isIdleNow }

    val bounds = composeTestRule.onNodeWithTag("mapScreen").getUnclippedBoundsInRoot()
    val xClickOffset = bounds.left + bounds.size.width / 2
    val yClickOffset = bounds.top + bounds.size.height / 2

    val bottomBarBounds =
        composeTestRule.onNodeWithTag("bottomNavigationMenu").getUnclippedBoundsInRoot()
    val yOffsetCorr =
        bottomBarBounds
            .height // for some reason the height of the map matches the one of the screen not the
    // actual size it does :)))))))))), this is an ugly fix to correct the position
    // of the click

    composeTestRule.onNodeWithTag("mapScreen").performTouchInput {
      click(Offset(xClickOffset.toPx(), yClickOffset.toPx() - yOffsetCorr.toPx()))
    }

    composeTestRule.waitUntil(
        10000) { // this value is arbitrary, we just don't want the test to completely halt. Might
          // need to tune it for the CI
          composeTestRule.onNodeWithTag("ParkOverview").isDisplayed()
        }

    composeTestRule.onNodeWithTag("ParkOverview").assertIsDisplayed()

    // we're going to check if the park matches the one we should have clicked on
    // TODO: tbd, can't be implemented yet as necessary features aren't on github yet
  }
}

// Below is what I used to be able to visualize where the offsets where, this can be useful for
// debugging later so I'll leave it in (even though it's not used anymore atm)
@Composable
fun PositionedComposable(offset: Offset) {
  Box(
      modifier = Modifier.fillMaxSize() // Ensure the container fills the available space
      ) {
        // Create a circle at the specified offset
        CircleIndicator(modifier = Modifier.offset(offset.x.toDp(), offset.y.toDp()))

        // Optional: Add any other content here
        Text(
            text = "Circle at ${offset.x}, ${offset.y}",
            modifier = Modifier.align(Alignment.TopStart),
            color = Color.Black)
      }
}

@Composable
fun CircleIndicator(modifier: Modifier = Modifier) {
  Box(
      modifier =
          modifier
              .size(20.dp) // Size of the visual indicator
              .background(Color.Red) // Circle shape (red background)
      )
}

// Extension function to convert Float to Dp
@Composable
fun Float.toDp(): Dp {
  return (this / LocalDensity.current.density).dp
}
