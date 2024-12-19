package com.android.streetworkapp.end2end

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule

class End2EndAchievement {

  @get:Rule val composeTestRule = createComposeRule()

  // grant the permission to access location (remove the window for permission)
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
}
