package com.devtask.app.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtask.app.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.devtask.app.data.local.entity.TaskEntity
import com.devtask.app.data.local.entity.TaskStatus
import com.devtask.app.data.local.entity.Priority

// Filtros disponibles en la pantalla de tareas
enum class TaskFilter { ALL, PENDING, COMPLETED, HIGH_PRIORITY }

// Estado completo de la pantalla de tareas
data class TasksUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: TaskFilter = TaskFilter.PENDING,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    // Flows privados que guardan el filtro y búsqueda actuales
    private val _filter = MutableStateFlow(TaskFilter.PENDING)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TasksUiState> = combine(
        _filter,
        _searchQuery
    ) { filter, query ->
        Pair(filter, query)
    }.flatMapLatest { (filter, query) ->
        // Si hay búsqueda activa usamos el buscador, si no usamos el filtro
        val baseFlow = when {
            query.isNotBlank() -> taskRepository.searchTasksFlow(query)
            filter == TaskFilter.PENDING -> taskRepository.getPendingTasksFlow()
            else -> taskRepository.getAllTasksFlow()
        }

        // Aplicamos filtros adicionales sobre los resultados
        baseFlow.map { tasks ->
            val filtered = when (filter) {
                TaskFilter.ALL -> tasks
                TaskFilter.PENDING -> tasks.filter { it.status != TaskStatus.COMPLETED }
                TaskFilter.COMPLETED -> tasks.filter { it.status == TaskStatus.COMPLETED }
                TaskFilter.HIGH_PRIORITY -> tasks.filter {
                    it.priority == Priority.HIGH && it.status != TaskStatus.COMPLETED
                }
            }
            TasksUiState(
                tasks = filtered,
                searchQuery = query,
                activeFilter = filter,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TasksUiState()
    )

    fun setFilter(filter: TaskFilter) { _filter.value = filter }

    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch { taskRepository.toggleTaskStatus(task) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { taskRepository.deleteTask(task) }
    }

    // Elimina todas las tareas completadas de un golpe
    fun deleteCompleted() {
        viewModelScope.launch { taskRepository.deleteCompletedTasks() }
    }
}