package com.karsaku.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object AddEditReminder : Screen("add_edit_reminder?reminderId={reminderId}") {
        fun createRoute(reminderId: Int? = null): String {
            return if (reminderId != null) "add_edit_reminder?reminderId=$reminderId" else "add_edit_reminder"
        }
    }
}
