package com.devtask.app.di

import android.content.Context
import androidx.room.Room
import com.devtask.app.data.local.dao.CategoryDao
import com.devtask.app.data.local.dao.TaskDao
import com.devtask.app.data.local.database.UniTaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// @Module = este archivo le dice a Hilt cómo crear objetos que no podemos
// anotar directamente con @Inject (como la base de datos de Room)
@Module
@InstallIn(SingletonComponent::class) // estos objetos viven toda la vida de la app
object DatabaseModule {

    // Crea la base de datos una sola vez y la reutiliza en toda la app
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): UniTaskDatabase =
        Room.databaseBuilder(
            context,
            UniTaskDatabase::class.java,
            "devtask_database" // nombre del archivo de la BD en el dispositivo
        ).build()

    // Hilt llama a provideDatabase() automáticamente para obtener la BD
    // y de ahí extrae el DAO que necesita
    @Provides
    fun provideTaskDao(db: UniTaskDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideCategoryDao(db: UniTaskDatabase): CategoryDao = db.categoryDao()
}