package com.android.streetworkapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq

class NavigationActionsTest {

  // Mocks
  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions
  private lateinit var graph: NavGraph

  // Captors
  private lateinit var navOptionsBuilderCaptor: KArgumentCaptor<NavOptionsBuilder.() -> Unit>

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navOptionsBuilderCaptor = argumentCaptor<NavOptionsBuilder.() -> Unit>()
    navigationActions = NavigationActions(navHostController)
    graph = mock(NavGraph::class.java)
  }

  @Test
  fun navigateToCallsControllerWithTopLevelDestObject() {
    LIST_TOP_LEVEL_DESTINATION.forEach { dst ->
      navigationActions.navigateTo(dst)
      verify(navHostController).navigate(eq(dst.route), any<NavOptionsBuilder.() -> Unit>())
    }
  }

  @Test
  fun navigateToCallsControllerWithScreen() {
    // having a list of screens to update in the testing would be cumbersome, only testing with
    // Screen.Map should be enough unless a big fuckup
    navigationActions.navigateTo(Screen.MAP)
    verify(navHostController).navigate(eq(Screen.MAP), eq(null), eq(null))
  }

  @Test
  fun goBackCallsController() {
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  @Test
  fun currentRouteWorksWithDestination() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    LIST_TOP_LEVEL_DESTINATION.forEach { dst ->
      `when`(navigationDestination.route).thenReturn(dst.route)
      assertThat(navigationActions.currentRoute(), `is`(dst.route))
    }

    // Auth is not a top level dest, so we check the case here
    `when`(navigationDestination.route).thenReturn(Route.AUTH)
    assertThat(navigationActions.currentRoute(), `is`(Route.AUTH))
  }

  @Test
  fun currentRouteReturnsEmptyStringIfNull() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(null)
    assertThat(navigationActions.currentRoute(), `is`(""))
  }

  @Test
  fun restoreStateIsTrueAndLaunchSingleTopIsTrueWhenNavigatingToTopDest() {
    `when`(navHostController.graph).thenReturn(graph)
    `when`(navHostController.graph.startDestinationId)
        .thenReturn(1) // 1 is arbitrary, just need a dummy id
    LIST_TOP_LEVEL_DESTINATION.forEach { dst ->
      navigationActions.navigateTo(dst)
      verify(navHostController).navigate(eq(dst.route), navOptionsBuilderCaptor.capture())

      val navOptions = NavOptionsBuilder().apply(navOptionsBuilderCaptor.firstValue)
      assertThat(navOptions.restoreState, `is`(true))
      assertThat(navOptions.launchSingleTop, `is`(true))
    }
  }

  @Test
  fun restoreStateIsFalseAndLaunchSingleTopIsTrueNavigatingToAuth() {
    `when`(navHostController.graph).thenReturn(graph)
    `when`(navHostController.graph.startDestinationId)
        .thenReturn(1) // 1 is arbitrary, just need a dummy id
    val authTopLevelDestination =
        TopLevelDestination(
            Route.AUTH,
            Icons.Outlined.AccountBox,
            null,
            "Auth") // dummy top lvl dest, it shouldn't exist for the Auth route but we need it for
    // the test

    navigationActions.navigateTo(authTopLevelDestination)
    verify(navHostController)
        .navigate(eq(authTopLevelDestination.route), navOptionsBuilderCaptor.capture())

    val navOptions = NavOptionsBuilder().apply(navOptionsBuilderCaptor.firstValue)
    assertThat(navOptions.restoreState, `is`(false))
    assertThat(navOptions.launchSingleTop, `is`(true))
  }
  @Test
  fun navigateToSoloScreen_WithAllParams_CorrectRoute() {
    navigationActions.navigateToSoloScreen(
      activity = "Running",
      isTimeDependent = true,
      time = 120,
      sets = null,
      reps = null
    )

    verify(navHostController).navigate("TrainSolo/Running/true?time=120")
  }

  @Test
  fun navigateToCoachScreen_WithSetsAndReps_CorrectRoute() {
    navigationActions.navigateToCoachScreen(
      activity = "Swimming",
      isTimeDependent = false,
      time = null,
      sets = 4,
      reps = 10
    )

    verify(navHostController).navigate("TrainCoach/Swimming/false?sets=4&reps=10")
  }

  @Test
  fun navigateToChallengeScreen_WithTime_CorrectRoute() {
    navigationActions.navigateToChallengeScreen(
      activity = "Cycling",
      isTimeDependent = true,
      time = 300,
      sets = null,
      reps = null
    )

    verify(navHostController).navigate("TrainChallenge/Cycling/true?time=300")
  }

  @Test
  fun navigateToTrainParam_CorrectRoute() {
    navigationActions.navigateToTrainParam(
      activity = "Jumping",
      isTimeDependent = true,
      type = "Solo"
    )

    verify(navHostController).navigate("TrainParam/Jumping/true/Solo")
  }

  @Test
  fun buildRoute_WithTime_CorrectString() {
    val route = navigationActions.buildRoute(
      baseRoute = "TrainSolo",
      activity = "Push-ups",
      isTimeDependent = true,
      time = 60,
      sets = null,
      reps = null
    )

    assertThat(route, `is`("TrainSolo/Push-ups/true?time=60"))
  }

  @Test
  fun buildRoute_WithSetsAndReps_CorrectString() {
    val route = navigationActions.buildRoute(
      baseRoute = "TrainCoach",
      activity = "Push-ups",
      isTimeDependent = false,
      time = null,
      sets = 3,
      reps = 15
    )

    assertThat(route, `is`("TrainCoach/Push-ups/false?sets=3&reps=15"))
  }
}
