package com.devtask.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

// @Entity le dice a Room que esta clase es una tabla en la base de datos
@Entity(
    tableName = "tasks",
    // ForeignKey conecta cada tarea con su categoría
    // Si se borra la categoría, categoryId queda en null (no borra la tarea)
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    // Índice para que las búsquedas por categoría sean más rápidas
    indices = [Index("categoryId")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) // Room genera el ID automáticamente
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val categoryId: Long? = null,         // null = sin categoría asignada
    val dueDate: LocalDateTime? = null,   // null = sin fecha límite
    val reminderAt: LocalDateTime? = null,
    val isReminderSet: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

// Niveles de prioridad de una tarea
enum class Priority(val label: String, val level: Int) {
    HIGH("Alta", 3),
    MEDIUM("Media", 2),
    LOW("Baja", 1)
}

// Estado actual de una tarea
enum class TaskStatus(val label: String) {
    PENDING("Pendiente"),
    IN_PROGRESS("En progreso"),
    COMPLETED("Completada")
}

