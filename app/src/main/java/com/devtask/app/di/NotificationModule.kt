package com.devtask.app.di

import android.app.NotificationManager
import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Mismo concepto que DatabaseModule pero para
// los objetos relacionados con notificaciones
@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    // NotificationManager es el sistema de Android que muestra las notificaciones
    // Lo obtenemos del sistema y Hilt lo inyecta donde se necesite
    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // WorkManager es el que programa las notificaciones en segundo plano
    // aunque la app esté cerrada
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager =
        WorkManager.getInstance(context)
}