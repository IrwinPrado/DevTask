package com.devtask.app.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import com.devtask.app.data.local.entity.TaskEntity
import com.devtask.app.data.local.entity.TaskStatus

// DAO = Data Access Object
// Es la interfaz que define todas las operaciones con la tabla "tasks"
// Room genera automáticamente el código de estas funciones
@Dao
interface TaskDao {

    // Obtiene todas las tareas ordenadas por prioridad y fecha
    // Flow significa que la UI se actualiza automáticamente cuando cambian los datos
    @Query("SELECT * FROM tasks ORDER BY CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END, dueDate ASC")
    fun getAllTasksFlow(): Flow<List<TaskEntity>>

    // Solo las tareas que NO están completadas
    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' ORDER BY CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END, dueDate ASC")
    fun getPendingTasksFlow(): Flow<List<TaskEntity>>

    // Tareas filtradas por categoría
    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END, dueDate ASC")
    fun getTasksByCategoryFlow(categoryId: Long): Flow<List<TaskEntity>>

    // Tareas dentro de un rango de fechas (usado por el calendario)
    @Query("SELECT * FROM tasks WHERE dueDate >= :start AND dueDate < :end ORDER BY dueDate ASC")
    fun getTasksByDateRangeFlow(start: LocalDateTime, end: LocalDateTime): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate >= :start AND dueDate < :end ORDER BY dueDate ASC")
    suspend fun getTasksByDateRange(start: LocalDateTime, end: LocalDateTime): List<TaskEntity>

    // Busca una tarea por su ID
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    // Recordatorios pendientes que aún no han pasado
    @Query("SELECT * FROM tasks WHERE isReminderSet = 1 AND reminderAt > :now AND status != 'COMPLETED'")
    suspend fun getPendingReminders(now: LocalDateTime = LocalDateTime.now()): List<TaskEntity>

    // Contadores para las estadísticas del Home
    @Query("SELECT COUNT(*) FROM tasks WHERE status != 'COMPLETED'")
    fun getPendingCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'COMPLETED'")
    fun getCompletedCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = 'HIGH' AND status != 'COMPLETED'")
    fun getHighPriorityCountFlow(): Flow<Int>

    // Busca tareas por texto en título o descripción
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTasksFlow(query: String): Flow<List<TaskEntity>>

    // Inserta una tarea nueva, si ya existe la reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // Actualiza solo el estado de una tarea (más eficiente que actualizar todo)
    @Query("UPDATE tasks SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: TaskStatus, updatedAt: LocalDateTime = LocalDateTime.now())

    // Elimina todas las tareas completadas de un golpe
    @Query("DELETE FROM tasks WHERE status = 'COMPLETED'")
    suspend fun deleteCompletedTasks()
}