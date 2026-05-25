package com.devtask.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtask.app.data.repository.CategoryRepository
import com.devtask.app.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import com.devtask.app.data.local.entity.TaskEntity


// Datos que necesita mostrar la pantalla de inicio
data class HomeUiState(
    val todayTasks: List<TaskEntity> = emptyList(),  // tareas de hoy
    val pendingCount: Int = 0,                        // total pendientes
    val completedCount: Int = 0,                      // total completadas
    val highPriorityCount: Int = 0,                   // total urgentes
    val isLoading: Boolean = true
)

// @HiltViewModel permite que Hilt cree y maneje este ViewModel automáticamente
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // combine escucha 4 Flows al mismo tiempo y cada vez que cualquiera
    // cambia, recalcula el estado completo de la pantalla
    val uiState: StateFlow<HomeUiState> = combine(
        taskRepository.getTasksForDayFlow(LocalDate.now()),
        taskRepository.getPendingCountFlow(),
        taskRepository.getCompletedCountFlow(),
        taskRepository.getHighPriorityCountFlow()
    ) { todayTasks, pending, completed, highPriority ->
        HomeUiState(
            todayTasks = todayTasks,
            pendingCount = pending,
            completedCount = completed,
            highPriorityCount = highPriority,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        // WhileSubscribed = solo escucha mientras hay una pantalla activa
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    // Cambia el estado de una tarea entre completada y pendiente
    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.toggleTaskStatus(task)
        }
    }

    // Al iniciar el ViewModel insertamos las categorías predeterminadas
    // si es la primera vez que se abre la app
    init {
        viewModelScope.launch {
            categoryRepository.seedDefaultCategoriesIfNeeded()
        }
    }
}