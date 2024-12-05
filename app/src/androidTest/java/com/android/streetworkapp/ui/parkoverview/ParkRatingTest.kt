package com.android.streetworkapp.ui.parkoverview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.ui.park.InteractiveRatingComponent
import com.android.streetworkapp.ui.park.ParkDetails
import com.android.streetworkapp.ui.park.RatingButton
import com.android.streetworkapp.ui.utils.CustomDialog
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ParkRatingTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var showRatingDialog: MutableState<Boolean>
  private lateinit var emptyPark: Park

  @Before
  fun setUp() {
    showRatingDialog = mutableStateOf(false)
    emptyPark = Park("", "", ParkLocation(0.0, 0.0, ""), "", 1f, 1, 10, 0, emptyList(), emptyList())
  }

  @Test
  fun isRatingButtonCorrect() {
    composeTestRule.setContent {
      RatingButton(showRatingDialog)

      CustomDialog(
          showRatingDialog,
          tag = "Rating",
          Content = { /* No content needed */},
          onSubmit = { /* submit function not called */},
          onDismiss = { /* dismiss function not called */})
    }

    val ratingButton = composeTestRule.onNodeWithTag("ratingButton")

    ratingButton.assertIsDisplayed().assertHasClickAction().performClick()

    composeTestRule.waitForIdle() // Wait for recomposition
    assert(showRatingDialog.value)
    composeTestRule.onNodeWithTag("RatingDialog").assertIsDisplayed()
  }

  @Test
  fun isInteractiveComponentCorrectlyDisplayed() {
    val starRating = mutableIntStateOf(1)

    composeTestRule.setContent { InteractiveRatingComponent(starRating) }

    for (index in 1..5) {
      composeTestRule
          .onNodeWithTag("starButton_${index}")
          .assertIsDisplayed()
          .assertHasClickAction()
          .performClick()
      composeTestRule.waitForIdle()
      assert(starRating.intValue == index)
    }
  }

  // New tests after MVVM link :
  // 1 - Is the button correctly hidden / shown depending on user and park state :

  @Test
  fun isRatingButtonCorrectlyDisplayedWhenNoRating() {
    val user = User("uid", "username", "email", 0, emptyList(), picture = "",
      parks = listOf(""))

    composeTestRule.setContent { ParkDetails(emptyPark, showRatingDialog, user) }

    // User not null and has not rated the emptyPark (empty votersUID list).
    composeTestRule.onNodeWithTag("ratingButton").assertIsDisplayed()
  }

  @Test
  fun isRatingButtonCorrectlyDisplayedWhenRating() {
    val user = User("uid", "username", "email", 0, emptyList(), picture = "",
      parks = listOf(""))

    // Set the user as having rated the emptyPark.
    emptyPark.votersUIDs = listOf(user.uid)

    composeTestRule.setContent { ParkDetails(emptyPark, showRatingDialog, user) }

    // User not null and has rated the emptyPark => should not be displayed.
    emptyPark.votersUIDs = listOf(user.uid)

    composeTestRule.waitForIdle()
    assert(emptyPark.votersUIDs.contains(user.uid))
    composeTestRule.onNodeWithTag("ratingButton").assertIsNotDisplayed()
  }

  @Test
  fun isRatingButtonCorrectlyDisplayedWhenNoUser() {
    val user: User? = null

    composeTestRule.setContent { ParkDetails(emptyPark, showRatingDialog, user) }

    // User null => should not be displayed.
    composeTestRule.onNodeWithTag("ratingButton").assertIsNotDisplayed()
  }
}
