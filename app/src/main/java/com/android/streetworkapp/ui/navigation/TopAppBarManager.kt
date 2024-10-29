package com.android.streetworkapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.vector.ImageVector

class TopAppBarManager(private var title: String = "", private var hasNavigationIcon: Boolean = false, private var navigationIcon: ImageVector? = null) {

    companion object {
        val DEFAULT_TOP_APP_BAR_NAVIGATION_ICON = Icons.AutoMirrored.Filled.ArrowBack
    }
    /**
     * Changes the TopAppBar title
     */
    fun setTopAppBarTitle(newTitle: String) {
        this.title = newTitle
    }

    /**
     * Get the TopAppBar title
     */
    fun getTopAppBarTitle(): String {
        return this.title
    }

    /**
     * Changes the TopAppBar navigationIcon
     */
    fun setNavigationIcon(newIcon: ImageVector) {
        this.navigationIcon = newIcon
    }

    /**
     * Get the TopAppBar title
     */
    fun getNavigationIcon(): ImageVector {
        return this.navigationIcon ?: TopAppBarManager.DEFAULT_TOP_APP_BAR_NAVIGATION_ICON
    }

    /**
     * returns hasNavigationIcon
     */
    fun hasNavigationIcon(): Boolean {
        return this.hasNavigationIcon
    }
}
