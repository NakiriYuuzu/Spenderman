package net.yuuzu.spenderman.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import net.yuuzu.spenderman.ui.screens.CategoryScreen
import net.yuuzu.spenderman.ui.screens.ExpenseScreen
import net.yuuzu.spenderman.ui.screens.HomeScreen
import net.yuuzu.spenderman.ui.screens.SettingsScreen
import net.yuuzu.spenderman.ui.viewmodel.CategoryViewModel
import net.yuuzu.spenderman.ui.viewmodel.ExpenseViewModel
import net.yuuzu.spenderman.ui.viewmodel.HomeViewModel
import net.yuuzu.spenderman.ui.viewmodel.SettingsViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddExpense : Screen("add_expense")
    data object EditExpense : Screen("edit_expense/{expenseId}")
    data object Settings : Screen("settings")
    data object Categories : Screen("categories")
    
    fun createRoute(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                route.replace("{${arg.substringBefore("=")}}", arg.substringAfter("="))
            }
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            val viewModel = koinInject<HomeViewModel>()
            
            HomeScreen(
                viewModel = viewModel,
                onAddExpenseClick = {
                    navController.navigate(Screen.AddExpense.route)
                },
                onExpenseClick = { expenseId ->
                    navController.navigate("edit_expense/$expenseId")
                },
                onBudgetClick = { /* Navigate to budget details */ },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onCategoriesClick = {
                    navController.navigate(Screen.Categories.route)
                }
            )
        }
        
        composable(Screen.AddExpense.route) {
            val viewModel = koinInject<ExpenseViewModel>()
            
            ExpenseScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.EditExpense.route) { backStackEntry ->
            val viewModel = koinInject<ExpenseViewModel>()
            val expenseId = backStackEntry.id

            ExpenseScreen(
                viewModel = viewModel,
                expenseId = expenseId,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            val viewModel = koinInject<SettingsViewModel>()
            
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onCategoriesClick = {
                    navController.navigate(Screen.Categories.route)
                }
            )
        }
        
        composable(Screen.Categories.route) {
            val viewModel = koinInject<CategoryViewModel>()
            
            CategoryScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
