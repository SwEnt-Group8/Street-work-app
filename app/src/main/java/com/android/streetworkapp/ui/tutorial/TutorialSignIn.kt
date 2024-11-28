package com.android.streetworkapp.ui.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.streetworkapp.ui.authentication.IconAndTextRow

@Composable
fun TutorialSignIn() {
  // Set number of page
  val pagerState = rememberPagerState(pageCount = { 3 })
  Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxWidth().testTag("introScreenBoxContainer")) {
        HorizontalPager(state = pagerState) { page ->
          // Display the corresponding page
          when (page) {
            0 -> IntroPage1()
            1 -> IntroPage2()
            2 -> IntroPage3()
          }
        }
      }
  // Dots indicator
  Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(16.dp).testTag("introDotRow")) {
        repeat(3) { index ->
          val isSelected = pagerState.currentPage == index
          Box(
              modifier =
                  Modifier.size(if (isSelected) 12.dp else 12.dp)
                      .padding(4.dp)
                      .clip(CircleShape)
                      .background(if (isSelected) Color.Blue else Color.Gray))
        }
      }
}

@Composable
fun IntroPage1() {
  Column(
      modifier = Modifier.fillMaxWidth().testTag("introColumn1"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().testTag("introBox1")) {

          // Background image
          Image(
              painter = painterResource(id = R.drawable.intro_1),
              contentDescription = "Background image 1",
              modifier = Modifier.fillMaxWidth().testTag("introImage1"),
              contentScale = ContentScale.Crop)

          // APP image
          Image(
              painter = painterResource(id = R.drawable.page_map),
              contentDescription = "App image 1",
              contentScale = ContentScale.Fit,
              modifier =
                  Modifier.align(Alignment.TopCenter)
                      .padding(top = 50.dp)
                      .size(450.dp)
                      .testTag("introApp1"))
        }

        Spacer(modifier = Modifier.height(20.dp).testTag("loginScreenFirstSpacer"))

        IconAndTextRow(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = "Location marker icon",
            text = "Find nearby parks and events to participate in or create",
            testName = "loginScreenFirstRow")
      }
}

@Composable
fun IntroPage2() {
  Column(
      modifier = Modifier.fillMaxWidth().testTag("introColumn2"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().testTag("introBox2")) {

          // Background image
          Image(
              painter = painterResource(id = R.drawable.intro_2),
              contentDescription = "Background image 2",
              modifier = Modifier.fillMaxWidth().testTag("IntroImage2"))

          // APP image
          Image(
              painter = painterResource(id = R.drawable.page_event),
              contentDescription = "App image 2",
              contentScale = ContentScale.Fit,
              modifier =
                  Modifier.align(Alignment.TopCenter)
                      .padding(top = 50.dp)
                      .size(450.dp)
                      .testTag("introApp2"))
        }

        Spacer(modifier = Modifier.height(20.dp).testTag("loginScreenSecondSpacer"))

        IconAndTextRow(
            imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
            contentDescription = "Running person icon",
            text = "Track your activities and learn new skills",
            testName = "loginScreenSecondRow")
      }
}

@Composable
fun IntroPage3() {
  Column(
      modifier = Modifier.fillMaxWidth().testTag("introColumn3"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().testTag("introBox3")) {

          // Background image
          Image(
              painter = painterResource(id = R.drawable.intro_3),
              contentDescription = "Background image 3",
              modifier = Modifier.fillMaxWidth().testTag("introImage3"))

          // APP image
          Image(
              painter =
                  painterResource(id = R.drawable.page_progress), // Replace with your resource ID
              contentDescription = "App image 3",
              contentScale = ContentScale.Fit,
              modifier =
                  Modifier.align(Alignment.TopCenter)
                      .padding(top = 50.dp)
                      .size(450.dp)
                      .testTag("introApp3"))
        }

        Spacer(modifier = Modifier.height(20.dp).testTag("loginScreenThirdSpacer"))

        IconAndTextRow(
            imageVector = Icons.Filled.ConnectWithoutContact,
            contentDescription = "People connecting icon",
            text = "Make new friends, train together and share activities",
            testName = "loginScreenThirdRow")
      }
}
