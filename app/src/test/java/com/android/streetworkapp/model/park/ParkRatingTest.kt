package com.android.streetworkapp.model.park

import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.ui.park.handleRating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never

class ParkRatingTest {
  private lateinit var parkRepository: ParkRepository
  private lateinit var parkViewModel: ParkViewModel
  private lateinit var emptyPark: Park
  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    parkRepository = Mockito.mock(ParkRepository::class.java)
    parkViewModel = ParkViewModel(parkRepository)
    emptyPark =
        Park("pid", "name", ParkLocation(0.0, 0.0, ""), "", 1f, 1, 10, 0, emptyList(), emptyList())
  }

  @Test
  fun handlerCorrectlyCallingAddRatingForStarRatingValues() = runTest {
    // Instantiate basic (but correct) user :
    val user = User("uid", "username", "email", 0, emptyList(), picture = "")

    val starRatingValues = listOf(1, 2, 3, 4, 5, 0, -1, 100)

    // Any value of starRating different from 1 to 5 should not call addRating in handleRating :
    for (starRating in starRatingValues) {
      handleRating(null, emptyPark, user, starRating, parkViewModel)
      testDispatcher.scheduler.advanceUntilIdle()

      if (starRating in 1..5) {
        Mockito.verify(parkRepository, Mockito.times(1))
            .addRating(emptyPark.pid, user.uid, starRating.toFloat())
      } else {
        Mockito.verify(parkRepository, never())
            .addRating(emptyPark.pid, user.uid, starRating.toFloat())
      }
    }
  }

  @Test
  fun handlerCorrectlyCallingAddRatingForUserValues() = runTest {
    // Instantiate basic (but correct) park :
    val user1 = User("uid1", "username1", "email1", 0, emptyList(), picture = "")
    val user2 = User("uid2", "username2", "email2", 0, emptyList(), picture = "")
    val user3 = User("uid3", "username3", "email3", 0, emptyList(), picture = "")

    val userValues = listOf(null, user1, user2, null, user3)
    val starRating = 4

    // Any value of starRating different from 1 to 5 should not call addRating in handleRating :
    for (user in userValues) {
      handleRating(null, emptyPark, user, starRating, parkViewModel)
      testDispatcher.scheduler.advanceUntilIdle()

      if (user != null) {
        Mockito.verify(parkRepository, Mockito.times(1))
            .addRating(emptyPark.pid, user.uid, starRating.toFloat())
      } else {
        Mockito.verify(parkRepository, never())
            .addRating(eq(emptyPark.pid), any(), eq(starRating.toFloat()))
      }
      Mockito.reset(parkRepository)
    }
  }

  @Test
  fun handlerCorrectlyCallingAddRatingForParkValues() = runTest {
    val starRating = 1
    val user = User("uid", "username", "email", 0, emptyList(), picture = "")

    // Testing for park with no rating and park with rating :
    val ratedPark =
        Park(
            "pid1",
            "name1",
            ParkLocation(0.0, 0.0, ""),
            "",
            1f,
            1,
            10,
            0,
            emptyList(),
            listOf(user.uid))

    val parkValues = listOf(null, emptyPark, ratedPark)

    // Any value of starRating different from 1 to 5 should not call addRating in handleRating :
    for (park in parkValues) {
      handleRating(null, park, user, starRating, parkViewModel)
      testDispatcher.scheduler.advanceUntilIdle()

      if (park != null && !park.votersUIDs.contains(user.uid)) {
        Mockito.verify(parkRepository, Mockito.times(1))
            .addRating(park.pid, user.uid, starRating.toFloat())
      } else {
        Mockito.verify(parkRepository, never())
            .addRating(null.toString(), user.uid, starRating.toFloat())
      }
      Mockito.reset(parkRepository)
    }
  }
}
