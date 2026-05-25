package com.devtask.app.data.repository

import com.devtask.app.data.local.dao.TaskDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import com.devtask.app.data.local.entity.TaskEntity
import com.devtask.app.data.local.entity.TaskStatus

// @Singleton = solo existe una instancia de este repositorio en toda la app
// @Inject = Hilt sabe cómo crearlo y pasarlo donde se necesite
@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao // Hilt nos da el DAO automáticamente
) {
    // --- Consultas que regresan Flow (la UI se actualiza sola cuando cambian) ---

    fun getAllTasksFlow(): Flow<List<TaskEntity>> =
        taskDao.getAllTasksFlow()

    fun getPendingTasksFlow(): Flow<List<TaskEntity>> =
        taskDao.getPendingTasksFlow()

    fun getTasksByCategoryFlow(categoryId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByCategoryFlow(categoryId)

    // Convierte un día completo en rango de fechas para el calendario
    fun getTasksForDayFlow(date: LocalDate): Flow<List<TaskEntity>> {
        val start = date.atStartOfDay()           // ej: 2024-11-15T00:00:00
        val end = date.plusDays(1).atStartOfDay() // ej: 2024-11-16T00:00:00
        return taskDao.getTasksByDateRangeFlow(start, end)
    }

    suspend fun getTasksForDay(date: LocalDate): List<TaskEntity> {
        val start = date.atStartOfDay()
        val end = date.plusDays(1).atStartOfDay()
        return taskDao.getTasksByDateRange(start, end)
    }

    fun searchTasksFlow(query: String): Flow<List<TaskEntity>> =
        taskDao.searchTasksFlow(query)

    // Contadores para las estadísticas del Home
    fun getPendingCountFlow(): Flow<Int> = taskDao.getPendingCountFlow()
    fun getCompletedCountFlow(): Flow<Int> = taskDao.getCompletedCountFlow()
    fun getHighPriorityCountFlow(): Flow<Int> = taskDao.getHighPriorityCountFlow()

    // --- Consultas simples (se leen una sola vez) ---

    suspend fun getTaskById(id: Long): TaskEntity? =
        taskDao.getTaskById(id)

    suspend fun getPendingReminders(): List<TaskEntity> =
        taskDao.getPendingReminders()

    // --- Operaciones de escritura ---

    suspend fun createTask(task: TaskEntity): Long =
        taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) {
        // Actualizamos la fecha de modificación automáticamente
        taskDao.updateTask(task.copy(updatedAt = LocalDateTime.now()))
    }

    // Alterna entre completada y pendiente
    suspend fun toggleTaskStatus(task: TaskEntity) {
        val newStatus = if (task.status == TaskStatus.COMPLETED) {
            TaskStatus.PENDING
        } else {
            TaskStatus.COMPLETED
        }
        taskDao.updateTaskStatus(task.id, newStatus)
    }

    suspend fun deleteTask(task: TaskEntity) =
        taskDao.deleteTask(task)

    suspend fun deleteCompletedTasks() =
        taskDao.deleteCompletedTasks()
}