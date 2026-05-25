package com.devtask.app.data.repository

import com.devtask.app.data.local.dao.CategoryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import com.devtask.app.data.local.entity.CategoryEntity
import com.devtask.app.data.local.entity.DefaultCategories

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    // Escucha cambios en tiempo real en la lista de categorías
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>> =
        categoryDao.getAllCategoriesFlow()

    // Lectura única, sin Flow
    suspend fun getAllCategories(): List<CategoryEntity> =
        categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): CategoryEntity? =
        categoryDao.getCategoryById(id)

    suspend fun createCategory(category: CategoryEntity): Long =
        categoryDao.insertCategory(category)

    suspend fun updateCategory(category: CategoryEntity) =
        categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) =
        categoryDao.deleteCategory(category)

    // Se llama al iniciar la app — si no hay categorías las inserta
    // Así las categorías predeterminadas solo se crean una vez
    suspend fun seedDefaultCategoriesIfNeeded() {
        if (categoryDao.getCount() == 0) {
            categoryDao.insertCategories(DefaultCategories.list)
        }
    }
}