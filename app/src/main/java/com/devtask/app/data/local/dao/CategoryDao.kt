package com.devtask.app.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.devtask.app.data.local.entity.CategoryEntity

// DAO de categorías, mismo concepto que TaskDao
// pero para la tabla "categories"
@Dao
interface CategoryDao {

    // Obtiene todas las categorías ordenadas:
    // primero las predeterminadas, luego el resto por nombre
    @Query("SELECT * FROM categories ORDER BY isDefault DESC, name ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    // Versión sin Flow para cuando solo necesitamos leer una vez (no escuchar cambios)
    @Query("SELECT * FROM categories ORDER BY isDefault DESC, name ASC")
    suspend fun getAllCategories(): List<CategoryEntity>

    // Busca una categoría por su ID
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    // Cuenta cuántas categorías hay (usado para saber si hay que insertar las predeterminadas)
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int

    // IGNORE = si ya existe una categoría igual, no la duplica
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity): Long

    // Inserta una lista de categorías de un golpe (usado para las predeterminadas)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}