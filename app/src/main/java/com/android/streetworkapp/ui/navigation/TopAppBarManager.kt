package com.android.streetworkapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.sample.R

class TopAppBarManager(
    private var title: String = "",
    private var hasNavigationIcon: Boolean = false,
    private var navigationIcon: ImageVector? = null,
    private var actions: List<TopAppBarAction> = emptyList(),
    private var actionCallbacks: MutableMap<TopAppBarAction, () -> Unit> = mutableMapOf()
) {

  companion object {
    val DEFAULT_TOP_APP_BAR_NAVIGATION_ICON = Icons.AutoMirrored.Filled.ArrowBack
  }

  enum class TopAppBarAction(val icon: Int, val contentDescription: String, val testTag: String) {
    SETTINGS(
        icon = R.drawable.settings, contentDescription = "Settings", testTag = "settings_button"),
    INFO(icon = R.drawable.octagon_help, contentDescription = "Info", testTag = "info_button")

    // Add more actions here
  }

  /** Set the callbacks for each action */
  fun setAllActionCallbacks(
      actionCallbacks: Map<TopAppBarAction, () -> Unit> = actions.associateWith { {} }
  ) {
    this.actionCallbacks = actionCallbacks as MutableMap<TopAppBarAction, () -> Unit>
  }

  /** Set the callback for a specific action */
  fun setActionCallback(action: TopAppBarAction, callback: () -> Unit) {
    this.actionCallbacks[action] = callback
  }

  /** Trigger onClick for an action */
  fun onActionClick(action: TopAppBarAction) {
    actionCallbacks[action]?.invoke()
  }

  /** Changes the TopAppBar title */
  fun setTopAppBarTitle(newTitle: String) {
    this.title = newTitle
  }

  /** Get the TopAppBar title */
  fun getTopAppBarTitle(): String {
    return this.title
  }

  /** returns hasNavigationIcon */
  fun hasNavigationIcon(): Boolean {
    return this.hasNavigationIcon
  }

  /** Get the TopAppBar title */
  fun getNavigationIcon(): ImageVector {
    return this.navigationIcon ?: TopAppBarManager.DEFAULT_TOP_APP_BAR_NAVIGATION_ICON
  }

  /** Set the actions for the Top App Bar */
  fun setTopAppBarActions(actions: List<TopAppBarAction>) {
    this.actions = actions
  }

  /** Get the actions for the Top App Bar */
  fun getTopAppBarActions(): List<TopAppBarAction> {
    return this.actions
  }
}
