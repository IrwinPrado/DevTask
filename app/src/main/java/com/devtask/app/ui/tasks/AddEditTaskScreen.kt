package com.devtask.app.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.devtask.app.data.local.entity.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditTaskViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Cuando se guarda exitosamente navegamos de regreso
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    // Control de visibilidad de los diálogos de fecha/hora
    var showDueDatePicker by remember { mutableStateOf(false) }
    var showDueTimePicker by remember { mutableStateOf(false) }
    var showReminderDatePicker by remember { mutableStateOf(false) }
    var showReminderTimePicker by remember { mutableStateOf(false) }

    // Estados de los selectores de fecha
    val dueDatePickerState = rememberDatePickerState()
    val reminderDatePickerState = rememberDatePickerState()

    val dtFormatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditMode) "Editar tarea" else "Nueva tarea",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = viewModel::saveTask) {
                        Text("Guardar", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- Título ---
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Título *") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.error != null,
                supportingText = state.error?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                leadingIcon = {
                    Icon(Icons.Default.Title, contentDescription = null)
                },
                singleLine = true
            )

            // --- Descripción ---
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Notes, contentDescription = null)
                },
                minLines = 2,
                maxLines = 4
            )

            // --- Prioridad ---
            FormSectionLabel(icon = Icons.Default.Flag, label = "Prioridad")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.values().forEach { priority ->
                    FilterChip(
                        selected = state.priority == priority,
                        onClick = { viewModel.onPriorityChange(priority) },
                        label = { Text(priority.label) },
                        leadingIcon = {
                            Icon(
                                priority.icon(),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = priority.color()
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = priority.color().copy(alpha = 0.2f),
                            selectedLabelColor = priority.color()
                        )
                    )
                }
            }

            // --- Categoría ---
            if (state.categories.isNotEmpty()) {
                FormSectionLabel(icon = Icons.Default.Category, label = "Categoría")
                var expanded by remember { mutableStateOf(false) }
                val selectedCat = state.categories.find { it.id == state.categoryId }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCat?.name ?: "Sin categoría",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // Opción para quitar la categoría
                        DropdownMenuItem(
                            text = { Text("Sin categoría") },
                            onClick = {
                                viewModel.onCategoryChange(null)
                                expanded = false
                            }
                        )
                        state.categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    viewModel.onCategoryChange(cat.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // --- Fecha de vencimiento ---
            FormSectionLabel(
                icon = Icons.Default.CalendarToday,
                label = "Fecha de vencimiento"
            )
            OutlinedCard(
                onClick = { showDueDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        state.dueDate?.format(dtFormatter) ?: "Seleccionar fecha y hora",
                        color = if (state.dueDate == null)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.weight(1f))
                    // Botón para quitar la fecha
                    if (state.dueDate != null) {
                        IconButton(onClick = { viewModel.onDueDateChange(null) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Quitar fecha")
                        }
                    }
                }
            }

            // --- Recordatorio ---
            FormSectionLabel(
                icon = Icons.Default.Notifications,
                label = "Recordatorio"
            )
            OutlinedCard(
                onClick = { showReminderDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Alarm,
                        contentDescription = null,
                        tint = if (state.isReminderSet)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        state.reminderAt?.format(dtFormatter) ?: "Agregar recordatorio",
                        color = if (!state.isReminderSet)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.weight(1f))
                    if (state.isReminderSet) {
                        IconButton(onClick = { viewModel.onReminderChange(null) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Quitar recordatorio")
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // --- Botón guardar ---
            Button(
                onClick = viewModel::saveTask,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(
                    if (state.isEditMode) Icons.Default.Save else Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(if (state.isEditMode) "Guardar cambios" else "Crear tarea")
            }
        }

        // --- Diálogo selector de fecha de vencimiento ---
        if (showDueDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDueDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        dueDatePickerState.selectedDateMillis?.let { millis ->
                            val date = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            viewModel.onDueDateChange(date.atTime(23, 59))
                        }
                        showDueDatePicker = false
                        showDueTimePicker = true
                    }) { Text("Siguiente") }
                },
                dismissButton = {
                    TextButton(onClick = { showDueDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) { DatePicker(state = dueDatePickerState) }
        }

        // --- Diálogo selector de hora de vencimiento ---
        if (showDueTimePicker) {
            val timeState = rememberTimePickerState(
                initialHour = state.dueDate?.hour ?: 23,
                initialMinute = state.dueDate?.minute ?: 59
            )
            TimePickerDialog(
                onDismiss = { showDueTimePicker = false },
                onConfirm = {
                    val date = state.dueDate?.toLocalDate() ?: LocalDate.now()
                    viewModel.onDueDateChange(
                        LocalDateTime.of(date, LocalTime.of(timeState.hour, timeState.minute))
                    )
                    showDueTimePicker = false
                }
            ) { TimePicker(state = timeState) }
        }

        // --- Diálogo selector de fecha de recordatorio ---
        if (showReminderDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showReminderDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        reminderDatePickerState.selectedDateMillis?.let { millis ->
                            val date = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            viewModel.onReminderChange(date.atTime(8, 0))
                        }
                        showReminderDatePicker = false
                        showReminderTimePicker = true
                    }) { Text("Siguiente") }
                },
                dismissButton = {
                    TextButton(onClick = { showReminderDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) { DatePicker(state = reminderDatePickerState) }
        }

        // --- Diálogo selector de hora de recordatorio ---
        if (showReminderTimePicker) {
            val reminderTimeState = rememberTimePickerState(
                initialHour = state.reminderAt?.hour ?: 8,
                initialMinute = state.reminderAt?.minute ?: 0
            )
            TimePickerDialog(
                onDismiss = { showReminderTimePicker = false },
                onConfirm = {
                    val date = state.reminderAt?.toLocalDate() ?: LocalDate.now()
                    viewModel.onReminderChange(
                        LocalDateTime.of(
                            date,
                            LocalTime.of(reminderTimeState.hour, reminderTimeState.minute)
                        )
                    )
                    showReminderTimePicker = false
                }
            ) { TimePicker(state = reminderTimeState) }
        }
    }
}

// Etiqueta de sección del formulario con ícono
@Composable
fun FormSectionLabel(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Diálogo reutilizable para el selector de hora
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Aceptar") }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                content()
            }
        }
    )
}