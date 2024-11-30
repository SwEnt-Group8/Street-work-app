package com.android.streetworkapp.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Add a classical palette color
val WoodCharcoal = Color(0xFF464646)
val MistGrey = Color(0xFFC4C4BC)
val WhiteSmoke = Color(0xFFF4F4F4)
val ShyBeige = Color(0xFFDEDAD1)

// used in event overview
val Snowflake = Color(0xFFE0E0E0)

// GoogleAuthButton Colors :
val White = Color(0xFFFFFFFF)
val LightGray = Color(0xFFDADCE0)
val DarkGray = Color(0xFF3C4043)

// Train with a friend Button in ProfileScreen :
val ButtonRed = Color(0xFFA53A36)

// FIGMA v2 colors
object ColorPalette {
  val PRINCIPLE_BACKGROUND_COLOR = Color(0xFFF9F9F9)
  val PRIMARY_TEXT_COLOR = Color(0xFF333333) // Dark grey
  val SECONDARY_TEXT_COLOR = Color(0xFF666666) // Medium Gray
  val INTERACTION_COLOR_DARK = Color(0xFF007BFF) // Blue
  val INTERACTION_COLOR_LIGHT = Color(0xFFBADBFF) // Light Blue
  val SHADOW_GREY = Color(0xFFC9C9C9)
  val BORDER_COLOR = Color(0xFFDDDDDD)
  val BUTTON_COLOR =
      ButtonColors(
          containerColor = INTERACTION_COLOR_DARK,
          contentColor = PRIMARY_TEXT_COLOR,
          disabledContentColor = SECONDARY_TEXT_COLOR,
          disabledContainerColor = INTERACTION_COLOR_LIGHT)
  val NAVIGATION_BAR_ITEM_COLORS =
      NavigationBarItemColors(
          selectedIconColor = PRIMARY_TEXT_COLOR,
          selectedTextColor = PRIMARY_TEXT_COLOR,
          selectedIndicatorColor = INTERACTION_COLOR_DARK,
          disabledIconColor = Color.Transparent,
          disabledTextColor = PRIMARY_TEXT_COLOR,
          unselectedIconColor = PRIMARY_TEXT_COLOR,
          unselectedTextColor = PRIMARY_TEXT_COLOR)

  val TUTORIAL_INTERACTION_1 = Color(0xFFFF0000) // red
  val TUTORIAL_INTERACTION_2 = Color(0xFF00FF00) // green
}
