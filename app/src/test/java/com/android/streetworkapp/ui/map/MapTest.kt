import android.Manifest
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.android.streetworkapp.ui.map.Map
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
class MapTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var context: Context
  private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()

    // Mock the ActivityResultLauncher for permission requests
    requestPermissionLauncher = mockk(relaxed = true)
  }

  @Test
  fun testPermissionRequestTriggered() {
    ShadowApplication.getInstance().denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

    composeTestRule.setContent { Map(requestPermissionLauncher = requestPermissionLauncher) }

    // Verify that the permission request was triggered
    verify { requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
  }

  @Test
  fun testPermissionRequestNotTriggered() {
    ShadowApplication.getInstance().grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

    composeTestRule.setContent { Map(requestPermissionLauncher = requestPermissionLauncher) }

    // Verify that the permission request was not triggered
    verify(exactly = 0) {
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }
}
