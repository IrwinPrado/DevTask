package com.devtask.app

import com.devtask.app.data.local.entity.Priority
import com.devtask.app.data.local.entity.TaskEntity
import com.devtask.app.data.local.entity.TaskStatus
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class TaskRepositoryTest {

    // Prueba que una tarea nueva tenga estado PENDING por defecto
    @Test
    fun `nueva tarea tiene estado PENDING por defecto`() {
        val task = TaskEntity(title = "Estudiar para examen")
        assertEquals(TaskStatus.PENDING, task.status)
    }

    // Prueba que una tarea nueva tenga prioridad MEDIUM por defecto
    @Test
    fun `nueva tarea tiene prioridad MEDIUM por defecto`() {
        val task = TaskEntity(title = "Estudiar para examen")
        assertEquals(Priority.MEDIUM, task.priority)
    }

    // Prueba que el título no esté vacío
    @Test
    fun `titulo de tarea no debe estar vacio`() {
        val task = TaskEntity(title = "Tarea de inglés")
        assertTrue(task.title.isNotBlank())
    }

    // Prueba que una tarea completada tenga el estado correcto
    @Test
    fun `tarea completada tiene estado COMPLETED`() {
        val task = TaskEntity(
            title = "Entregar proyecto",
            status = TaskStatus.COMPLETED
        )
        assertEquals(TaskStatus.COMPLETED, task.status)
    }

    // Prueba que la prioridad Alta tenga nivel 3
    @Test
    fun `prioridad alta tiene nivel 3`() {
        assertEquals(3, Priority.HIGH.level)
    }

    // Prueba que la prioridad Media tenga nivel 2
    @Test
    fun `prioridad media tiene nivel 2`() {
        assertEquals(2, Priority.MEDIUM.level)
    }

    // Prueba que la prioridad Baja tenga nivel 1
    @Test
    fun `prioridad baja tiene nivel 1`() {
        assertEquals(1, Priority.LOW.level)
    }

    // Prueba que una tarea con fecha pasada sea identificable como vencida
    @Test
    fun `tarea con fecha pasada esta vencida`() {
        val task = TaskEntity(
            title = "Tarea vencida",
            dueDate = LocalDateTime.now().minusDays(1)
        )
        val isOverdue = task.dueDate != null &&
                task.dueDate.isBefore(LocalDateTime.now()) &&
                task.status != TaskStatus.COMPLETED
        assertTrue(isOverdue)
    }

    // Prueba que una tarea completada no sea vencida aunque su fecha haya pasado
    @Test
    fun `tarea completada no esta vencida aunque su fecha haya pasado`() {
        val task = TaskEntity(
            title = "Tarea completada",
            dueDate = LocalDateTime.now().minusDays(1),
            status = TaskStatus.COMPLETED
        )
        val isOverdue = task.dueDate != null &&
                task.dueDate.isBefore(LocalDateTime.now()) &&
                task.status != TaskStatus.COMPLETED
        assertFalse(isOverdue)
    }
}