package com.android.streetworkapp.ui.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.ConnectWithoutContact
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.streetworkapp.ui.authentication.IconAndTextRow
import com.android.streetworkapp.ui.theme.ColorPalette

@Composable
fun TutorialSignIn() {
  // Set number of page
  // Access screen dimensions
  val configuration = LocalConfiguration.current
  val screenHeight = configuration.screenHeightDp.dp

  val pagerState = rememberPagerState(pageCount = { 3 })
  Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxWidth().testTag("introScreenBoxContainer")) {
        HorizontalPager(state = pagerState) { page ->
          // Display the corresponding page
          when (page) {
            0 -> IntroPage1(screenHeight)
            1 -> IntroPage2(screenHeight)
            2 -> IntroPage3(screenHeight)
          }
        }
      }
  // Dots indicator
  Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(5.dp).testTag("introDotRow")) {
        repeat(3) { index ->
          val isSelected = pagerState.currentPage == index
          Box(
              modifier =
                  Modifier.size(12.dp)
                      .padding(4.dp)
                      .clip(CircleShape)
                      .background(
                          if (isSelected) ColorPalette.INTERACTION_COLOR_DARK
                          else ColorPalette.BORDER_COLOR))
        }
      }
}

@Composable
fun IntroPage1(screenHeight: Dp) {
  val context = LocalContext.current
  Column(
      modifier = Modifier.fillMaxWidth().testTag("introColumn1"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().testTag("introBox1")) {

          // Background image
          // Source : https://kengurupro.eu/about/
          Image(
              painter = painterResource(id = R.drawable.intro_1),
              contentDescription = "Background image 1",
              modifier = Modifier.fillMaxWidth().fillMaxHeight(0.55f).testTag("introImage1"),
              contentScale = ContentScale.Crop)

          // APP image
          Image(
              painter = painterResource(id = R.drawable.page_map),
              contentDescription = "App image 1",
              contentScale = ContentScale.Fit,
              modifier =
                  Modifier.align(Alignment.TopCenter)
                      .padding(top = 50.dp)
                      .size(screenHeight / 2)
                      .testTag("introApp1"))
        }

        Spacer(modifier = Modifier.height(20.dp).testTag("loginScreenFirstSpacer"))

        IconAndTextRow(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = "Location marker icon",
            text = context.getString(R.string.SignInTutorialIntroPage1Text),
            testName = "loginScreenFirstRow")
      }
}

@Composable
fun IntroPage2(screenHeight: Dp) {
  val context = LocalContext.current
  Column(
      modifier = Modifier.fillMaxWidth().testTag("introColumn2"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().testTag("introBox2")) {

          // Background image
          // Source : https://verticaltechnik.ch/en/cms/8/street-workout
          Image(
              painter = painterResource(id = R.drawable.intro_2),
              contentDescription = "Background image 2",
              modifier = Modifier.fillMaxWidth().fillMaxHeight(0.55f).testTag("IntroImage2"),
              contentScale = ContentScale.Crop)

          // APP image
          Image(
              painter = painterResource(id = R.drawable.page_event),
              contentDescription = "App image 2",
              contentScale = ContentScale.Fit,
              modifier =
                  Modifier.align(Alignment.TopCenter)
                      .padding(top = 50.dp)
                      .size(screenHeight / 2)
                      .testTag("introApp2"))
        }

        Spacer(modifier = Modifier.height(20.dp).testTag("loginScreenSecondSpacer"))

        IconAndTextRow(
            imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
            contentDescription = "Running person icon",
            text = context.getString(R.string.SignInTutorialIntroPage2Text),
            testName = "loginScreenSecondRow")
      }
}

@Composable
fun IntroPage3(screenHeight: Dp) {
  val context = LocalContext.current
  Column(
      modifier = Modifier.fillMaxWidth().testTag("introColumn3"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().testTag("introBox3")) {

          // Background image
          // Source : https://www.urbanmovement.info/what-is-street-work-out/
          Image(
              painter = painterResource(id = R.drawable.intro_3),
              contentDescription = "Background image 3",
              modifier = Modifier.fillMaxWidth().fillMaxHeight(0.55f).testTag("introImage3"),
              contentScale = ContentScale.Crop)

          // APP image
          Image(
              painter =
                  painterResource(id = R.drawable.page_progress), // Replace with your resource ID
              contentDescription = "App image 3",
              contentScale = ContentScale.Fit,
              modifier =
                  Modifier.align(Alignment.TopCenter)
                      .padding(top = 50.dp)
                      .size(screenHeight / 2)
                      .testTag("introApp3"))
        }

        Spacer(modifier = Modifier.height(20.dp).testTag("loginScreenThirdSpacer"))

        IconAndTextRow(
            imageVector = Icons.Filled.ConnectWithoutContact,
            contentDescription = "People connecting icon",
            text = context.getString(R.string.SignInTutorialIntroPage3Text),
            testName = "loginScreenThirdRow")
      }
}
