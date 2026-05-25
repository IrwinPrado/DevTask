package com.devtask.app.ui.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.devtask.app.data.repository.CategoryRepository
import com.devtask.app.data.repository.TaskRepository
import com.devtask.app.ui.tasks.TaskCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.devtask.app.data.local.entity.CategoryEntity
import com.devtask.app.data.local.entity.TaskEntity


// Estado de la pantalla de tareas por categoría
data class CategoryTasksUiState(
    val category: CategoryEntity? = null,
    val tasks: List<TaskEntity> = emptyList(),
    val isLoading: Boolean = true
)

// ViewModel dentro del mismo archivo por ser simple y muy relacionado
@HiltViewModel
class CategoryTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Obtenemos el ID de la categoría desde los argumentos de navegación
    private val categoryId: Long = checkNotNull(savedStateHandle["categoryId"])

    val uiState: StateFlow<CategoryTasksUiState> = combine(
        // Leemos la categoría una sola vez con flow { emit(...) }
        flow { emit(categoryRepository.getCategoryById(categoryId)) },
        // Escuchamos las tareas de esta categoría en tiempo real
        taskRepository.getTasksByCategoryFlow(categoryId)
    ) { cat, tasks ->
        CategoryTasksUiState(
            category = cat,
            tasks = tasks,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoryTasksUiState()
    )

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch { taskRepository.toggleTaskStatus(task) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTasksScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTask: (Long) -> Unit,
    viewModel: CategoryTasksViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val cat = state.category

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Ícono de la categoría en su color
                        cat?.let {
                            Icon(
                                iconFromName(it.iconName),
                                contentDescription = null,
                                tint = parseColor(it.colorHex)
                            )
                        }
                        Text(
                            cat?.name ?: "Categoría",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.tasks.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📂", style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Sin tareas en esta categoría",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Crea una tarea y asígnala aquí",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggle = { viewModel.toggleTask(task) },
                            onClick = { onNavigateToTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}