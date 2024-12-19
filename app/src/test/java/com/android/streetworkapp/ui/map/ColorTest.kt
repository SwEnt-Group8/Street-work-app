package com.android.streetworkapp.ui.map

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorTest {
  // test gradientColor
  @Test
  fun testGradientColorFraction0() {
    val startColor = Color(1f, 0f, 0f, 1f) // Red color
    val endColor = Color(0f, 0f, 1f, 1f) // Blue color
    val result = gradientColor(startColor, endColor, 0f)

    assertEquals(startColor, result)
  }

  @Test
  fun testGradientColorFraction1() {
    val startColor = Color(1f, 0f, 0f, 1f) // Red color
    val endColor = Color(0f, 0f, 1f, 1f) // Blue color
    val result = gradientColor(startColor, endColor, 1f)

    assertEquals(endColor, result)
  }

  @Test
  fun testGradientColorFraction05() {
    val startColor = Color(1f, 0f, 0f, 1f) // Red color
    val endColor = Color(0f, 0f, 1f, 1f) // Blue color
    val expectedColor = Color(0.5f, 0f, 0.5f, 1f) // Expected midpoint color (purple)

    val result = gradientColor(startColor, endColor, 0.5f)

    assertEquals(expectedColor, result)
  }
  // test colorToHue
  @Test
  fun testColorToHueRed() {
    val color = Color(1f, 0f, 0f, 1f) // Red color
    val result = colorToHue(color)

    assertEquals(0f, result, 0.1f) // Red has hue 0
  }
}
