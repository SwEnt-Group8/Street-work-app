package com.android.streetworkapp.ui.park

// Portions of this code were generated with the help of GitHub Copilot.

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.android.sample.R
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventOverviewUiState
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.ui.utils.CustomDialog
import com.android.streetworkapp.utils.dateDifference
import com.android.streetworkapp.utils.toFormattedString
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Display the overview of a park, including park details and a list of events.
 *
 * @param parkViewModel The view model for the park.
 * @param innerPadding The padding to apply to the screen.
 * @param navigationActions The navigation actions to navigate to other screens.
 * @param eventViewModel The view model for the events.
 * @param userViewModel The view model for the user.
 */
@Composable
fun ParkOverviewScreen(
    parkViewModel: ParkViewModel,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    navigationActions: NavigationActions = NavigationActions(rememberNavController()),
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel =
        UserViewModel(UserRepositoryFirestore(FirebaseFirestore.getInstance()))
) {

  // MVVM calls for park state :
  val currentPark = parkViewModel.currentPark.collectAsState()
  parkViewModel.park.collectAsState().value?.pid?.let { parkViewModel.loadCurrentPark(it) }

  // MVVM calls for event state of the park :
  currentPark.value?.let { eventViewModel.getEvents(it) }

  // MVVM calls for user state :
  val currentUser = userViewModel.currentUser.collectAsState().value

  parkViewModel.updateCurrentParkNameNominatim()

  val showRatingDialog = remember { mutableStateOf(false) }

  val context = LocalContext.current

  Box(modifier = Modifier
      .padding(innerPadding)
      .fillMaxSize()
      .testTag("parkOverviewScreen")) {
    Column {
      ImageTitle(image = null, title = currentPark.value?.name ?: "loading...")
      // TODO: Fetch image from Firestore storage
      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        ParkDetails(park = currentPark.value, showRatingDialog, currentUser)
        Button(
            onClick = { navigationActions.navigateTo(Screen.ADD_EVENT) },
            modifier = Modifier
                .size(width = 150.dp, height = 40.dp)
                .testTag("createEventButton"),
            colors = ColorPalette.BUTTON_COLOR) {
              Text("Create an event")
            }
      }

      val starRating = remember { mutableIntStateOf(3) } // Stores the "live" rating value

      CustomDialog(
          showRatingDialog,
          tag = "Rating",
          Content = { InteractiveRatingComponent(starRating) },
          onSubmit = {
            Log.d("ParkOverview", "RatingDialog: Submitting rating")
            handleRating(
                context, currentPark.value, currentUser, starRating.intValue, parkViewModel)
          },
          onDismiss = { starRating.intValue = 3 })

      HorizontalDivider(modifier = Modifier.fillMaxWidth())
      EventItemList(eventViewModel, navigationActions)
    }
  }
}

/**
 * Display an image with a title overlay, if no image is provided, an AI generated default image is
 * displayed.
 *
 * @param image The image to display.
 * @param title The title to display.
 */
@Composable
fun ImageTitle(image: Painter?, title: String) {
  Box(modifier = Modifier
      .fillMaxWidth()
      .height(220.dp)
      .testTag("imageTitle")) {
    Image(
        // If no image is provided, use the default park image generated by Dall-E
        painter = image ?: painterResource(id = R.drawable.park_default),
        contentDescription = "Park Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth())
    Box(
        modifier =
        Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                brush =
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xCC000000)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ))
    Text(
        text = title,
        color = Color.White,
        fontSize = 24.sp,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
            .testTag("title"))

      IconButton(
          onClick = { },
          modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(2.dp)
      ) {
          Box(
              modifier = Modifier
                  .size(36.dp)
                  .background(
                      color = ColorPalette.INTERACTION_COLOR_DARK,
                      shape = CircleShape
                  )
                  .padding(4.dp)
          ) {
              Icon(
                  painter = painterResource(id = R.drawable.add_a_photo_24px), // Replace with your "add image" icon
                  contentDescription = "Add Image",
                  tint = Color.White,
                  modifier = Modifier.align(Alignment.Center).fillMaxSize(0.75f)
              )
          }
          }
  }
}

/**
 * Display the details of a park, including the park's rating and occupancy.
 *
 * @param park The park data to display.
 * @param showRatingDialog The state to show the rating dialog.
 * @param user The user who is viewing the park.
 */
@Composable
fun ParkDetails(park: Park?, showRatingDialog: MutableState<Boolean>, user: User?) {
  Column(modifier = Modifier
      .testTag("parkDetails")
      .padding(bottom = 16.dp, end = 48.dp)) {
    Text(
        text = "Park rating",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 2.dp))

    Row {
      park?.rating?.let { RatingComponent(rating = it.toInt(), park.nbrRating) } // Round the rating

      if (shouldRatingButtonBeVisible(park, user)) RatingButton(showRatingDialog)
    }
  }
}

/**
 * Display a star rating from 1 to 5 and the number of reviews.
 *
 * @param rating The rating to display from 1 to 5.
 * @param nbrReview The positive number of reviews.
 */
@Composable
fun RatingComponent(rating: Int, nbrReview: Int) {
  Log.d("ParkOverview", "RatingComponent: rating=$rating ; nbrReview=$nbrReview")
  require(rating in 1..5) { "Rating must be between 1 and 5" }
  Row(
      modifier = Modifier
          .padding(start = 16.dp)
          .testTag("ratingComponent"),
      verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
          Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Star",
              tint = if (i <= rating) ColorPalette.INTERACTION_COLOR_DARK else Color.Gray,
              modifier = Modifier.size(24.dp))
        }
        Text(
            text = "($nbrReview)",
            modifier = Modifier
                .padding(start = 8.dp)
                .testTag("nbrReview"),
            fontSize = 16.sp,
            fontWeight = FontWeight.Light)
      }
}

/**
 * Display the button to rate the park. Should not be displayed if the user has already rated the
 * park.
 *
 * @param showRatingDialog The state to show the rating dialog.
 */
@Composable
fun RatingButton(showRatingDialog: MutableState<Boolean>) {
  IconButton(
      onClick = { showRatingDialog.value = true },
      modifier = Modifier
          .size(24.dp)
          .padding(start = 8.dp)
          .testTag("ratingButton")) {
        Icon(
            painter = painterResource(id = R.drawable.add_plus_square), // Use an icon resource
            contentDescription = "Rate",
            modifier = Modifier.size(24.dp))
      }
}

/**
 * Verifies that the current state is correct and then rates the park.
 *
 * @param context The context of the application.
 * @param park The park to rate.
 * @param user The user who is rating the park.
 * @param starRating The rating value.
 * @param parkViewModel The park view model.
 */
fun handleRating(
    context: Context?,
    park: Park?,
    user: User?,
    starRating: Int,
    parkViewModel: ParkViewModel?
) {
  Log.d("ParkOverview", "handleRating: {park=$park ; user=$user ; rating=$starRating")

  when {
    user == null -> {
      if (context != null)
          Toast.makeText(context, "User not found, could not rate park", Toast.LENGTH_SHORT).show()
    }
    park == null -> {
      if (context != null)
          Toast.makeText(context, "Park not found, could not rate park", Toast.LENGTH_SHORT).show()
    }
    starRating < 1 || starRating > 5 -> {
      if (context != null)
          Toast.makeText(context, "Invalid rating value, could not rate park", Toast.LENGTH_SHORT)
              .show()
    }
    parkViewModel == null -> {
      if (context != null)
          Toast.makeText(context, "Error with view model, could not rate park", Toast.LENGTH_SHORT)
              .show()
    }
    else -> {
      Log.d("ParkOverview", "handleRating: Adding rating to park")
      if (context != null) Toast.makeText(context, "Rating submitted", Toast.LENGTH_SHORT).show()
      parkViewModel.addRating(park.pid, user.uid, starRating.toFloat())
    }
  }
}

/**
 * Defines whether a user can see the rating button depending on whether he already has rated a
 * park.
 *
 * @param park The park to check.
 * @param user The user to check.
 * @return True if the user has rated the park, false otherwise.
 */
fun shouldRatingButtonBeVisible(park: Park?, user: User?): Boolean {
  val res = park != null && user != null && !park.votersUIDs.contains(user.uid)
  Log.d("ParkOverview", "shouldRatingButtonBeVisible: for $user at $park => $res")
  return res
}

/**
 * Display an interactive star rating component.
 *
 * @param rating The rating value which will be modified by the user.
 */
@Composable
fun InteractiveRatingComponent(rating: MutableState<Int>) {
  require(rating.value in 1..5) { "Rating must be between 1 and 5" }
  Row(
      modifier = Modifier
          .fillMaxWidth()
          .testTag("ratingComponent"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center) {
        for (i in 1..5) {
          IconButton(
              onClick = { rating.value = i },
              modifier = Modifier
                  .size(45.dp)
                  .testTag("starButton_${i}")) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint =
                        if (i <= rating.value) ColorPalette.INTERACTION_COLOR_DARK else Color.Gray,
                    modifier = Modifier
                        .size(45.dp)
                        .testTag("starIcon_${i}"))
              }
        }
      }
}

/**
 * Display a list of events or a message if no there is no events.
 *
 * @param eventViewModel The event MVVM.
 * @param navigationActions The navigation actions to navigate to other screens.
 */
@Composable
fun EventItemList(eventViewModel: EventViewModel, navigationActions: NavigationActions) {
  val uiState = eventViewModel.uiState.collectAsState().value

  Column(modifier = Modifier.testTag("eventItemList")) {
    Text(
        text = "Events",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 2.dp))

    when (uiState) {
      is EventOverviewUiState.NotEmpty -> {
        LazyColumn {
          items(uiState.eventList) { event -> EventItem(event, eventViewModel, navigationActions) }
        }
      }
      is EventOverviewUiState.Empty -> {
        Box(modifier = Modifier.fillMaxSize()) {
          Text(
              text = "No event is planned yet",
              fontSize = 16.sp,
              fontWeight = FontWeight.Light,
              modifier =
              Modifier
                  .align(Alignment.Center)
                  .padding(bottom = 40.dp)
                  .testTag("noEventText"))
        }
      }
    }
  }
}

/**
 * Display an event item, including the event title, date, and number of participants.
 *
 * @param event The event data to display.
 */
@Composable
fun EventItem(event: Event, eventViewModel: EventViewModel, navigationActions: NavigationActions) {
  ListItem(
      modifier = Modifier
          .padding(0.dp)
          .testTag("eventItem"),
      headlineContent = { Text(text = event.title) },
      supportingContent = {
        Text(
            "Participants ${event.listParticipants.size}/${event.maxParticipants}",
            fontWeight = FontWeight.Light,
            modifier = Modifier.testTag("participantsText"))
      },
      overlineContent = {
        Text(
            text = dateDifference(event.date.toFormattedString()),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("dateText"))
      },
      leadingContent = {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Icon",
            modifier = Modifier
                .size(56.dp)
                .testTag("profileIcon"))
      },
      trailingContent = {
        Button(
            onClick = {
              eventViewModel.setCurrentEvent(event)
              navigationActions.navigateTo(Screen.EVENT_OVERVIEW)
            },
            modifier = Modifier
                .size(width = 80.dp, height = 48.dp)
                .testTag("eventButton"),
            colors = ColorPalette.BUTTON_COLOR,
            contentPadding = PaddingValues(0.dp)) {
              Text("About", modifier = Modifier.testTag("eventButtonText"))
            }
      })
  HorizontalDivider()
}
