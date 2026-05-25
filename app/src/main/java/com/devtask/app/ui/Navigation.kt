package com.devtask.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Cada pantalla de la app tiene una ruta única (como una URL)
// que se usa para navegar entre ellas
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Tasks : Screen("tasks")
    object Calendar : Screen("calendar")
    object Categories : Screen("categories")

    // AddEditTask recibe un parámetro opcional: el ID de la tarea a editar
    // Si taskId es -1 significa que es una tarea nueva
    object AddEditTask : Screen("add_edit_task?taskId={taskId}") {
        fun buildRoute(taskId: Long? = null) =
            if (taskId != null) "add_edit_task?taskId=$taskId"
            else "add_edit_task?taskId=-1"
    }

    // CategoryTasks recibe el ID de la categoría a mostrar
    object CategoryTasks : Screen("category_tasks/{categoryId}") {
        fun buildRoute(categoryId: Long) = "category_tasks/$categoryId"
    }
}

// Modelo para cada item de la barra de navegación inferior
data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

// Las 4 pantallas principales que aparecen en la barra inferior
val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Default.Home, "Inicio"),
    BottomNavItem(Screen.Tasks, Icons.Default.CheckCircle, "Tareas"),
    BottomNavItem(Screen.Calendar, Icons.Default.CalendarMonth, "Calendario"),
    BottomNavItem(Screen.Categories, Icons.Default.Category, "Categorías")
)