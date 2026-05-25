package com.devtask.app.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtask.app.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.devtask.app.data.local.entity.CategoryEntity

// Estado de la pantalla de categorías
data class CategoriesUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // Escucha cambios en tiempo real en la lista de categorías
    val uiState: StateFlow<CategoriesUiState> = categoryRepository.getAllCategoriesFlow()
        .map { CategoriesUiState(categories = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CategoriesUiState()
        )

    // Solo permite borrar categorías que no son predeterminadas
    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch { categoryRepository.deleteCategory(category) }
    }

    // Crea una categoría nueva con nombre, color e ícono
    fun createCategory(name: String, colorHex: String, iconName: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            categoryRepository.createCategory(
                CategoryEntity(
                    name = name.trim(),
                    colorHex = colorHex,
                    iconName = iconName
                )
            )
        }
    }
}