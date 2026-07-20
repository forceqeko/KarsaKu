package com.karsaku.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.karsaku.domain.repository.ReminderRepository
import com.karsaku.ui.addedit.AddEditReminderScreen
import com.karsaku.ui.addedit.AddEditReminderViewModel
import com.karsaku.ui.home.HomeScreen
import com.karsaku.ui.home.HomeViewModel
import com.karsaku.ui.settings.SettingsScreen
import com.karsaku.ui.settings.SettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: ReminderRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
            HomeScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate(Screen.AddEditReminder.createRoute()) },
                onEditClick = { id -> navController.navigate(Screen.AddEditReminder.createRoute(id)) }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(repository))
            SettingsScreen(viewModel = viewModel)
        }

        composable(
            route = Screen.AddEditReminder.route,
            arguments = listOf(
                navArgument("reminderId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getInt("reminderId").takeIf { it != -1 }
            val viewModel: AddEditReminderViewModel = viewModel(
                factory = AddEditReminderViewModel.Factory(repository, reminderId)
            )
            AddEditReminderScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
