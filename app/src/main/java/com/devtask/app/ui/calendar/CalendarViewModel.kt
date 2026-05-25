package com.devtask.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtask.app.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import com.devtask.app.data.local.entity.TaskEntity

// Estado completo de la pantalla de calendario
data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),   // día seleccionado
    val currentMonth: YearMonth = YearMonth.now(),   // mes que se está mostrando
    val tasksForSelectedDay: List<TaskEntity> = emptyList(), // tareas del día seleccionado
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    // Flows privados que controlan qué día y mes se muestran
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _currentMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<CalendarUiState> = combine(
        _selectedDate,
        _currentMonth,
        // flatMapLatest cancela el Flow anterior y crea uno nuevo
        // cada vez que cambia la fecha seleccionada
        _selectedDate.flatMapLatest { date ->
            taskRepository.getTasksForDayFlow(date)
        }
    ) { selectedDate, month, dayTasks ->
        CalendarUiState(
            selectedDate = selectedDate,
            currentMonth = month,
            tasksForSelectedDay = dayTasks,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CalendarUiState()
    )

    // Cambia el día seleccionado al tocar un día en el calendario
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    // Navega al mes anterior
    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    // Navega al mes siguiente
    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch { taskRepository.toggleTaskStatus(task) }
    }
}