package com.devtask.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.devtask.app.ui.*
import com.devtask.app.ui.calendar.CalendarScreen
import com.devtask.app.ui.categories.CategoriesScreen
import com.devtask.app.ui.categories.CategoryTasksScreen
import com.devtask.app.ui.home.HomeScreen
import com.devtask.app.ui.tasks.AddEditTaskScreen
import com.devtask.app.ui.tasks.TasksScreen
import com.devtask.app.ui.theme.DevTaskTheme
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint permite que Hilt inyecte dependencias en esta Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // la app ocupa toda la pantalla incluyendo la barra de estado
        setContent {
            DevTaskTheme {
                DevTaskApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevTaskApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Solo mostramos la barra inferior en las 4 pantallas principales
    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == item.screen.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    // Evita apilar múltiples copias de la misma pantalla
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // NavHost contiene todas las pantallas y maneja la navegación entre ellas
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pantalla de inicio
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAddTask = {
                        navController.navigate(Screen.AddEditTask.buildRoute())
                    },
                    onNavigateToTask = { id ->
                        navController.navigate(Screen.AddEditTask.buildRoute(id))
                    }
                )
            }

            // Pantalla de tareas
            composable(Screen.Tasks.route) {
                TasksScreen(
                    onNavigateToAddTask = {
                        navController.navigate(Screen.AddEditTask.buildRoute())
                    },
                    onNavigateToTask = { id ->
                        navController.navigate(Screen.AddEditTask.buildRoute(id))
                    }
                )
            }

            // Pantalla de calendario
            composable(Screen.Calendar.route) {
                CalendarScreen(
                    onNavigateToTask = { id ->
                        navController.navigate(Screen.AddEditTask.buildRoute(id))
                    }
                )
            }

            // Pantalla de categorías
            composable(Screen.Categories.route) {
                CategoriesScreen(
                    onNavigateToCategoryTasks = { id ->
                        navController.navigate(Screen.CategoryTasks.buildRoute(id))
                    }
                )
            }

            // Pantalla de crear/editar tarea
            // taskId = -1 significa tarea nueva, cualquier otro valor es edición
            composable(
                route = Screen.AddEditTask.route,
                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                AddEditTaskScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Pantalla de tareas por categoría
            composable(
                route = Screen.CategoryTasks.route,
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.LongType }
                )
            ) {
                CategoryTasksScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTask = { id ->
                        navController.navigate(Screen.AddEditTask.buildRoute(id))
                    }
                )
            }
        }
    }
}