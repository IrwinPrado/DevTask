package com.devtask.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

// @HiltAndroidApp inicializa Hilt en toda la aplicación
// Sin esto Hilt no funciona en ninguna parte del proyecto
@HiltAndroidApp
class DevTaskApplication : Application(), Configuration.Provider {

    // Hilt nos inyecta el factory que sabe crear workers con dependencias
    // como nuestro ReminderWorker que necesita NotificationHelper
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Le decimos a WorkManager que use el factory de Hilt
    // para que pueda inyectar dependencias en los Workers
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}