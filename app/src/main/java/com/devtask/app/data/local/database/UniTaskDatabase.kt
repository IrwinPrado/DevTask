package com.devtask.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devtask.app.data.local.dao.CategoryDao
import com.devtask.app.data.local.dao.TaskDao
import com.devtask.app.data.local.entity.TaskEntity
import com.devtask.app.data.local.entity.CategoryEntity

// @Database le dice a Room cuáles son las tablas y la versión de la BD
// Si agregas o modificas tablas en el futuro, debes subir el número de versión
@Database(
    entities = [TaskEntity::class, CategoryEntity::class], // tablas de la BD
    version = 1,
    exportSchema = false // no necesitamos exportar el esquema por ahora
)
// Le decimos a Room que use nuestros conversores de fecha
@TypeConverters(Converters::class)
abstract class UniTaskDatabase : RoomDatabase() {

    // Room genera automáticamente estas implementaciones
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
}