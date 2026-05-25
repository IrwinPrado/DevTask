package com.devtask.app.ui.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtask.app.data.repository.CategoryRepository
import com.devtask.app.data.repository.TaskRepository
import com.devtask.app.ui.notifications.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import com.devtask.app.data.local.entity.TaskEntity
import com.devtask.app.data.local.entity.CategoryEntity
import com.devtask.app.data.local.entity.Priority

// Estado del formulario de crear/editar tarea
data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val categoryId: Long? = null,
    val dueDate: LocalDateTime? = null,
    val reminderAt: LocalDateTime? = null,
    val isReminderSet: Boolean = false,
    val categories: List<CategoryEntity> = emptyList(),
    val isEditMode: Boolean = false, // true = editando, false = creando
    val isSaved: Boolean = false,    // true = guardar exitoso, navegamos de regreso
    val error: String? = null        // mensaje de error si algo falla
)

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val notificationHelper: NotificationHelper,
    savedStateHandle: SavedStateHandle // nos da acceso a los argumentos de navegación
) : ViewModel() {

    // Obtenemos el ID de la tarea desde los argumentos de navegación
    // -1 significa que es una tarea nueva
    private val taskId: Long = savedStateHandle.get<Long>("taskId") ?: -1L

    private val _state = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Cargamos las categorías para el dropdown
            val categories = categoryRepository.getAllCategories()
            _state.update { it.copy(categories = categories) }

            // Si hay ID válido cargamos los datos de la tarea a editar
            if (taskId != -1L) {
                taskRepository.getTaskById(taskId)?.let { task ->
                    _state.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            priority = task.priority,
                            categoryId = task.categoryId,
                            dueDate = task.dueDate,
                            reminderAt = task.reminderAt,
                            isReminderSet = task.isReminderSet,
                            isEditMode = true
                        )
                    }
                }
            }
        }
    }

    // Funciones para actualizar cada campo del formulario
    fun onTitleChange(title: String) =
        _state.update { it.copy(title = title, error = null) }

    fun onDescriptionChange(desc: String) =
        _state.update { it.copy(description = desc) }

    fun onPriorityChange(priority: Priority) =
        _state.update { it.copy(priority = priority) }

    fun onCategoryChange(id: Long?) =
        _state.update { it.copy(categoryId = id) }

    fun onDueDateChange(date: LocalDateTime?) =
        _state.update { it.copy(dueDate = date) }

    fun onReminderChange(date: LocalDateTime?) =
        _state.update { it.copy(reminderAt = date, isReminderSet = date != null) }

    fun saveTask() {
        val s = _state.value

        // Validación básica
        if (s.title.isBlank()) {
            _state.update { it.copy(error = "El título no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            if (s.isEditMode) {
                // Actualizamos la tarea existente conservando su ID y fecha de creación
                val existing = taskRepository.getTaskById(taskId) ?: return@launch
                val updated = existing.copy(
                    title = s.title.trim(),
                    description = s.description.trim(),
                    priority = s.priority,
                    categoryId = s.categoryId,
                    dueDate = s.dueDate,
                    reminderAt = s.reminderAt,
                    isReminderSet = s.isReminderSet
                )
                taskRepository.updateTask(updated)

                // Cancelamos el recordatorio anterior y programamos el nuevo
                notificationHelper.cancelTaskReminder(taskId)
                if (s.isReminderSet && s.reminderAt != null) {
                    notificationHelper.scheduleTaskReminder(updated)
                }
            } else {
                // Creamos una tarea nueva
                val newTask = TaskEntity(
                    title = s.title.trim(),
                    description = s.description.trim(),
                    priority = s.priority,
                    categoryId = s.categoryId,
                    dueDate = s.dueDate,
                    reminderAt = s.reminderAt,
                    isReminderSet = s.isReminderSet
                )
                val newId = taskRepository.createTask(newTask)

                // Programamos el recordatorio si tiene uno
                if (s.isReminderSet && s.reminderAt != null) {
                    notificationHelper.scheduleTaskReminder(newTask.copy(id = newId))
                }
            }

            // Indicamos que se guardó correctamente para navegar de regreso
            _state.update { it.copy(isSaved = true) }
        }
    }
}