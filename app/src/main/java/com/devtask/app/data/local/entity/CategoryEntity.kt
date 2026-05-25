package com.devtask.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de categorías en la base de datos
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#1A73E8",  // Color en formato hexadecimal
    val iconName: String = "folder",   // Nombre del ícono de Material Icons
    val isDefault: Boolean = false     // true = categoría que viene preinstalada
)

// Categorías que se insertan automáticamente la primera vez que se instala la app
object DefaultCategories {
    val list = listOf(
        CategoryEntity(name = "Universidad", colorHex = "#1976D2", iconName = "school", isDefault = true),
        CategoryEntity(name = "Trabajo / Negocio", colorHex = "#388E3C", iconName = "work", isDefault = true),
        CategoryEntity(name = "Inglés", colorHex = "#F57C00", iconName = "language", isDefault = true),
        CategoryEntity(name = "Personal", colorHex = "#7B1FA2", iconName = "person", isDefault = true)
    )
}

