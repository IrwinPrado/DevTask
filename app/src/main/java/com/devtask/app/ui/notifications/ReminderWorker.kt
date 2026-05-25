package com.devtask.app.ui.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// @HiltWorker permite que Hilt inyecte dependencias en este Worker
// CoroutineWorker nos permite usar coroutines dentro del trabajo
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    // Hilt nos inyecta el NotificationHelper automáticamente
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    // Este método se ejecuta cuando llega la hora del recordatorio
    override suspend fun doWork(): Result {
        // Recuperamos los datos que le pasamos al programar el recordatorio
        val taskId = inputData.getLong(EXTRA_TASK_ID, -1L)
        val taskTitle = inputData.getString(EXTRA_TASK_TITLE) ?: "Tarea"

        // Si no hay ID válido algo salió mal, reportamos fallo
        if (taskId == -1L) return Result.failure()

        // Mostramos la notificación
        notificationHelper.showReminderNotification(taskId, taskTitle)

        return Result.success()
    }
}