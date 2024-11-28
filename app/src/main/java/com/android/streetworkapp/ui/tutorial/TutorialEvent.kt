package com.android.streetworkapp.ui.tutorial

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.ui.theme.DarkGray
import com.android.streetworkapp.ui.theme.GoogleAuthButtonTextStyle
import com.android.streetworkapp.ui.theme.White

@Composable
fun TutorialEvent(navigationActions: NavigationActions) {
  // Set number of page
  val pagerState = rememberPagerState(pageCount = { 5 })

  // Tutorial text for each page
  val tutoText1 = buildAnnotatedString {
    withStyle(style = SpanStyle(color = ColorPalette.TUTORIAL_INTERACTION_1)) { append("Click") }
    append(" on your favorite park")
  }

  val tutoText2 = buildAnnotatedString {
    withStyle(style = SpanStyle(ColorPalette.TUTORIAL_INTERACTION_2)) { append("Look") }
    append(" for other people's events in the park\nOr ")
    withStyle(style = SpanStyle(color = ColorPalette.TUTORIAL_INTERACTION_1)) { append("start") }
    append(" one yourself")
  }

  val tutoText3 = buildAnnotatedString {
    withStyle(style = SpanStyle(ColorPalette.TUTORIAL_INTERACTION_2)) { append("Join") }
    append(" an already ongoing event")
  }

  val tutoText4 = buildAnnotatedString {
    append("Or ")
    withStyle(style = SpanStyle(color = ColorPalette.TUTORIAL_INTERACTION_1)) { append("create") }
    append(" your own event")
  }

  Box(modifier = Modifier.fillMaxSize().testTag("tutoScreenBoxContainer")) {
    Column(
        modifier =
            Modifier.fillMaxWidth().testTag("tutoScreenColumnContainer").padding(top = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
              modifier = Modifier.fillMaxSize(0.9f).testTag("tutoPageContainer"),
          ) {
            HorizontalPager(state = pagerState) { page ->
              // Display the corresponding page
              when (page) {
                0 -> Page1()
                1 ->
                    PageTuto(
                        R.drawable.tuto_map, tutoText1, "tutoColumn1", "tutoImage1", "tutoText1")
                2 ->
                    PageTuto(
                        R.drawable.tuto_park, tutoText2, "tutoColumn2", "tutoImage2", "tutoText2")
                3 ->
                    PageTuto(
                        R.drawable.tuto_join, tutoText3, "tutoColumn3", "tutoImage3", "tutoText3")
                4 -> {
                  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                    PageTuto(
                        R.drawable.tuto_event, tutoText4, "tutoColumn4", "tutoImage4", "tutoText4")
                    PageEndButton(navigationActions)
                  }
                }
              }
            }
          }

          // Dots indicator
          Row(
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(16.dp).testTag("tutoDot")) {
                repeat(5) { index ->
                  val isSelected = pagerState.currentPage == index
                  Box(
                      modifier =
                          Modifier.size(if (isSelected) 12.dp else 12.dp)
                              .padding(4.dp)
                              .clip(CircleShape)
                              .background(
                                  if (isSelected) ColorPalette.INTERACTION_COLOR_DARK
                                  else ColorPalette.BORDER_COLOR))
                }
              }
        }
  }
}

@Composable
fun Page1() {
  // title
  Column(
      modifier = Modifier.fillMaxSize().testTag("tutoColumn0"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Welcome to Street WorkApp!",
            style =
                TextStyle(
                    fontSize = 26.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight(500),
                    color = ColorPalette.PRIMARY_TEXT_COLOR,
                    textAlign = TextAlign.Center,
                ),
            modifier =
                Modifier.height(32.dp).aspectRatio(308f / 24f).fillMaxWidth().testTag("tutoText0"))
      }
}

@Composable
fun PageTuto(id: Int, text: AnnotatedString, columnTag: String, imageTag: String, textTag: String) {
  // Each page have 1 App image And 1 Explication
  Column(modifier = Modifier.fillMaxSize().testTag(columnTag)) {
    Image(
        painter = painterResource(id),
        contentDescription = "Tutorial image",
        modifier = Modifier.fillMaxHeight(0.8f).fillMaxWidth().padding(15.dp).testTag(imageTag),
        contentScale = ContentScale.Fit,
    )
    Text(
        text = text,
        style =
            TextStyle(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(500),
                color = ColorPalette.PRIMARY_TEXT_COLOR,
                textAlign = TextAlign.Center,
            ),
        modifier = Modifier.fillMaxWidth().testTag(textTag).padding(15.dp))
  }
}

@Composable
fun PageEndButton(navigationActions: NavigationActions) {
  // Quit the tutorial and go to MAP screen
  Box(
      modifier = Modifier.fillMaxSize().padding(16.dp).testTag("tutoButtonBox"),
      contentAlignment = Alignment.BottomEnd // Align all content to the bottom-right
      ) {
        Button(
            onClick = {
              Log.d("TutorialScreen", "Close Tutorial")
              navigationActions.navigateTo(Screen.MAP)
            },
            modifier =
                Modifier.width(150.dp)
                    .height(40.dp)
                    .background(
                        color = ColorPalette.INTERACTION_COLOR_DARK,
                        shape = RoundedCornerShape(20.dp))
                    .testTag("tutoButton"),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = ColorPalette.INTERACTION_COLOR_DARK,
                    contentColor = DarkGray)) {
              Row(
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxSize().testTag("tutoButtonRow")) {
                    Text(
                        modifier = Modifier.testTag("tutoCloseButtonText"),
                        text = "Close",
                        style =
                            GoogleAuthButtonTextStyle.copy(
                                color = White, textAlign = TextAlign.Center))
                  }
            }
      }
}
