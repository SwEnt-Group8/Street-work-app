package com.android.streetworkapp.ui.profile

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class SettingsTest {

  private lateinit var authService: GoogleAuthService
  private lateinit var userViewModel: UserViewModel
  private lateinit var parkViewModel: ParkViewModel
  private lateinit var eventViewModel: EventViewModel
  private lateinit var progressionViewModel: ProgressionViewModel
  private lateinit var workoutViewModel: WorkoutViewModel
  private lateinit var imageViewModel: ImageViewModel
  private lateinit var currentUser: User

  @Before
  fun setUp() {
    authService = mock(GoogleAuthService::class.java)
    userViewModel = mock(UserViewModel::class.java)
    parkViewModel = mock(ParkViewModel::class.java)
    eventViewModel = mock(EventViewModel::class.java)
    progressionViewModel = mock(ProgressionViewModel::class.java)
    workoutViewModel = mock(WorkoutViewModel::class.java)
    imageViewModel = mock(ImageViewModel::class.java)
    currentUser = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList(), "")

    whenever(userViewModel.currentUser).thenReturn(MutableStateFlow(currentUser))
  }

  @Test
  fun testDeleteAccountSuccess() {
    whenever(authService.getCurrentUser()).thenReturn(mock(FirebaseUser::class.java))

    val result =
        deleteAccount(
            authService,
            userViewModel,
            parkViewModel,
            eventViewModel,
            progressionViewModel,
            workoutViewModel,
            imageViewModel)

    assertTrue(result)
    verify(authService).deleteAuthUser()
    verify(userViewModel).deleteUserByUid("uid-alice")
    verify(userViewModel).removeUserFromAllFriendsLists("uid-alice")
    verify(parkViewModel).deleteRatingFromAllParks("uid-alice")
    verify(progressionViewModel).deleteProgressionByUid("uid-alice")
    verify(workoutViewModel).deleteWorkoutDataByUid("uid-alice")
  }

  @Test
  fun testDeleteAccountFailsDueToEmptyUid() {
    val result =
        deleteAccount(
            authService,
            userViewModel,
            parkViewModel,
            eventViewModel,
            progressionViewModel,
            workoutViewModel,
            imageViewModel)

    assertFalse(result)
    verify(authService, never()).deleteAuthUser()
    verify(userViewModel, never()).deleteUserByUid(anyString())
    verify(userViewModel, never()).removeUserFromAllFriendsLists(anyString())
    verify(parkViewModel, never()).deleteRatingFromAllParks(anyString())
    verify(progressionViewModel, never()).deleteProgressionByUid(anyString())
    verify(workoutViewModel, never()).deleteWorkoutDataByUid(anyString())
  }

  @Test
  fun testDeleteAccountFailsDueToNullFirebaseUser() {
    whenever(authService.getCurrentUser()).thenReturn(null)

    val result =
        deleteAccount(
            authService,
            userViewModel,
            parkViewModel,
            eventViewModel,
            progressionViewModel,
            workoutViewModel,
            imageViewModel)

    assertFalse(result)
    verify(authService, never()).deleteAuthUser()
    verify(userViewModel, never()).deleteUserByUid(anyString())
    verify(userViewModel, never()).removeUserFromAllFriendsLists(anyString())
    verify(parkViewModel, never()).deleteRatingFromAllParks(anyString())
    verify(progressionViewModel, never()).deleteProgressionByUid(anyString())
    verify(workoutViewModel, never()).deleteWorkoutDataByUid(anyString())
  }
}
