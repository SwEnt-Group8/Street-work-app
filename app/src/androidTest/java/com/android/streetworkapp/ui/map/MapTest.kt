package com.android.streetworkapp.ui.map

import android.Manifest
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.android.streetworkapp.MainActivity
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  // GrantPermissionRule is used to automatically grant permissions
  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  private lateinit var requestPermissionLauncher: FakeActivityResultLauncher

  @Before
  fun setup() {
    requestPermissionLauncher = FakeActivityResultLauncher()
  }

  @Test
  fun buttonClick() {
    composeTestRule.activityRule.scenario.onActivity { activity ->
      activity.setContent { MapPermission(requestPermissionLauncher = requestPermissionLauncher) }
    }
    composeTestRule.onNodeWithTag("requestPermissionButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("requestPermissionButton").performClick()
    composeTestRule.onNodeWithText("Test Permission").assertIsDisplayed()

    // Assert that clicking the button triggers a permission request
    assertNotNull(
        "Expected permission not null but found: ${requestPermissionLauncher.launchedPermission}",
        requestPermissionLauncher.launchedPermission)
  }

  @Test
  fun testDoNotRequestPermissionWhenGranted() {
    composeTestRule.activityRule.scenario.onActivity { activity ->
      // Grant permission using the InstrumentationRegistry for testing purposes
      val instrumentation = InstrumentationRegistry.getInstrumentation()
      val context = instrumentation.targetContext
      instrumentation.uiAutomation.grantRuntimePermission(
          context.packageName, Manifest.permission.ACCESS_FINE_LOCATION)

      activity.setContent { MapPermission(requestPermissionLauncher = requestPermissionLauncher) }
    }
    // Assert that the permission request was not triggered
    assertNull(requestPermissionLauncher.launchedPermission)
  }
}

class FakeActivityResultLauncher : ActivityResultLauncher<String>() {
  var launchedPermission: String? = null

  override fun launch(input: String?, options: ActivityOptionsCompat?) {
    launchedPermission = input
  }

  override fun unregister() {}

  override fun getContract(): ActivityResultContract<String, *> {
    throw NotImplementedError("Not needed for testing")
  }
}
