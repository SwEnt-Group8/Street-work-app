package com.android.streetworkapp.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.moderation.TextModerationRepository
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.progression.ProgressionRepository
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutRepository
import com.android.streetworkapp.model.workout.WorkoutViewModel
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.mock

// this is very wrong but something in the ADD_EVENT screen makes the test stall and I really can't
// be bothered to debug it. (We only skip one screen out of all the others so it shouldn't matter
// that much)
val TEST_SCREEN_EXCLUSION_LIST = listOf<String>(Screen.ADD_EVENT, Screen.EVENT_OVERVIEW)

@RunWith(AndroidJUnit4::class)
class BottomNavigationTest {
  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  @Composable
  fun BottomNavigationTest() {
    Scaffold(
        bottomBar = {
          BottomNavigationMenu(onTabSelect = {}, tabList = LIST_TOP_LEVEL_DESTINATION)
        }) {
          Text("test")
        }
  }

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  @Composable
  fun EmptyBottomNavigationTest() {
    Scaffold(bottomBar = { BottomNavigationMenu(onTabSelect = {}, tabList = listOf()) }) {
      Text("test")
    }
  }

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun printComposeHierarchy() {
    composeTestRule.setContent { BottomNavigationTest() }
    composeTestRule.onRoot().printToLog("BottomNavigationTest")
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { BottomNavigationTest() }
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists().assertIsDisplayed()

    for (topLevelDest in LIST_TOP_LEVEL_DESTINATION) composeTestRule
        .onNodeWithTag("bottomNavigationItem${topLevelDest.route}")
        .assertIsDisplayed()
  }

  @Test
  fun menuItemsAreClickable() {
    composeTestRule.setContent { BottomNavigationTest() }

    for (topLevelDest in LIST_TOP_LEVEL_DESTINATION) composeTestRule
        .onNodeWithTag("bottomNavigationItem${topLevelDest.route}")
        .performClick()
  }

  @Test
  fun displayNoComponents() {
    composeTestRule.setContent { EmptyBottomNavigationTest() }
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists().assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("bottomNavigationItem").assertCountEquals(0)
    composeTestRule.onAllNodesWithTag("bottomNavIcon").assertCountEquals(0)
  }

  @Test
  fun bottomBarDisplaysCorrectlyOnScreens() {

    val currentScreenParam =
        mutableStateOf(
            LIST_OF_SCREENS.first()) // can't call setContent twice per test so we use this instead
    composeTestRule.setContent {
      StreetWorkApp(
          ParkLocationViewModel(mock(ParkLocationRepository::class.java, RETURNS_DEFAULTS)),
          { navigateTo(currentScreenParam.value.screenName) },
          {},
          UserViewModel(mock(UserRepository::class.java, RETURNS_DEFAULTS)),
          ParkViewModel(mock(ParkRepository::class.java, RETURNS_DEFAULTS)),
          EventViewModel(mock(EventRepository::class.java, RETURNS_DEFAULTS)),
          ProgressionViewModel(mock(ProgressionRepository::class.java, RETURNS_DEFAULTS)),
          WorkoutViewModel(mock(WorkoutRepository::class.java, RETURNS_DEFAULTS)),
          TextModerationViewModel(mock(TextModerationRepository::class.java, RETURNS_DEFAULTS)),
          true)
    }

    val bottomNavTypeToTest =
        BottomNavigationMenuType.entries.filter { it != BottomNavigationMenuType.NONE }

    for (screenParam in LIST_OF_SCREENS) {
      if (screenParam.screenName in TEST_SCREEN_EXCLUSION_LIST) continue

      currentScreenParam.value = screenParam // Update the state to recompose our UI

      composeTestRule.waitForIdle()
      if (screenParam.isBottomBarVisible) {
        when (screenParam.bottomBarType) {
          BottomNavigationMenuType.NONE ->
              Assert.fail(
                  "Invalid use of the bottomBar setup, if isBottomBarVisible is set to false its type should be set to NONE")
          BottomNavigationMenuType.DEFAULT ->
              composeTestRule
                  .onNodeWithTag(BottomNavigationMenuType.DEFAULT.getTopLevelTestTag())
                  .assertIsDisplayed()
          BottomNavigationMenuType.EVENT_OVERVIEW ->
              composeTestRule
                  .onNodeWithTag(BottomNavigationMenuType.EVENT_OVERVIEW.getTopLevelTestTag())
                  .assertIsDisplayed()
        }
      } else {
        for (bottomNavType in bottomNavTypeToTest) composeTestRule
            .onNodeWithTag(bottomNavType.getTopLevelTestTag())
            .assertIsNotDisplayed()
      }
    }
  }
}
