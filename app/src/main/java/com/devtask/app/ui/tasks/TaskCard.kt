package com.devtask.app.ui.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.devtask.app.ui.theme.HighPriorityColor
import com.devtask.app.ui.theme.LowPriorityColor
import com.devtask.app.ui.theme.MediumPriorityColor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.devtask.app.data.local.entity.TaskEntity
import com.devtask.app.data.local.entity.TaskStatus
import com.devtask.app.data.local.entity.Priority

// Extensiones de Priority para obtener su color e ícono fácilmente
// desde cualquier parte de la app
fun Priority.color() = when (this) {
    Priority.HIGH -> HighPriorityColor
    Priority.MEDIUM -> MediumPriorityColor
    Priority.LOW -> LowPriorityColor
}

fun Priority.icon() = when (this) {
    Priority.HIGH -> Icons.Default.KeyboardArrowUp
    Priority.MEDIUM -> Icons.Default.Remove
    Priority.LOW -> Icons.Default.KeyboardArrowDown
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: TaskEntity,
    onToggle: () -> Unit,       // cuando el usuario marca/desmarca la tarea
    onClick: () -> Unit,        // cuando toca la tarjeta para editarla
    onDelete: (() -> Unit)? = null, // opcional: botón de eliminar
    modifier: Modifier = Modifier
) {
    val isCompleted = task.status == TaskStatus.COMPLETED

    // Una tarea está vencida si su fecha ya pasó y no está completada
    val isOverdue = task.dueDate != null &&
            task.dueDate.isBefore(LocalDateTime.now()) &&
            !isCompleted

    // El color de la tarjeta cambia suavemente según el estado
    val containerColor by animateColorAsState(
        targetValue = when {
            isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            isOverdue -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "card_color"
    )

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCompleted) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox para marcar como completada
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = task.priority.color()
                )
            )

            Spacer(Modifier.width(8.dp))

            // Contenido principal de la tarjeta
            Column(modifier = Modifier.weight(1f)) {
                // Título con tachado si está completada
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Descripción (solo si tiene)
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Fila inferior: prioridad, fecha y recordatorio
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Chip de prioridad
                    PriorityChip(priority = task.priority)

                    // Fecha de vencimiento (si tiene)
                    task.dueDate?.let { due ->
                        val fmt = DateTimeFormatter.ofPattern("d MMM, HH:mm")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (isOverdue) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                due.format(fmt),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOverdue) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Ícono de campana si tiene recordatorio
                    if (task.isReminderSet) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Recordatorio",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Botón de eliminar (opcional)
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// Chip pequeño que muestra la prioridad con su color
@Composable
fun PriorityChip(priority: Priority, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = priority.color().copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                priority.icon(),
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = priority.color()
            )
            Text(
                text = priority.label,
                style = MaterialTheme.typography.labelSmall,
                color = priority.color(),
                fontWeight = FontWeight.Medium
            )
        }
    }
}