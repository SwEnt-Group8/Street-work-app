package com.android.streetworkapp.ui.map

import android.Manifest
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.app.ActivityOptionsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.MainActivity
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowApplication

@RunWith(AndroidJUnit4::class)
class MapTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  private lateinit var requestPermissionLauncher: FakeActivityResultLauncher

  @Before
  fun setup() {
    requestPermissionLauncher = FakeActivityResultLauncher()
  }

  @Test
  fun testRequestPermissionWhenNotGranted() {

    composeTestRule.activityRule.scenario.onActivity { activity ->
      // Simulate that the permission is denied for this test case
      ShadowApplication.getInstance().denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
      activity.setContent { Map(requestPermissionLauncher = requestPermissionLauncher) }
    }
    // Assert that the permission request was triggered
    assertNotNull(requestPermissionLauncher.launchedPermission) {
      "Expected permission not null but found: ${requestPermissionLauncher.launchedPermission}"
    }
  }

  @Test
  fun testDoNotRequestPermissionWhenGranted() {
    composeTestRule.activityRule.scenario.onActivity { activity ->
      // Simulate that the permission is already granted
      ShadowApplication.getInstance().grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

      activity.setContent { Map(requestPermissionLauncher = requestPermissionLauncher) }
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
