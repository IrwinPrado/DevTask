package com.devtask.app.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.devtask.app.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import com.devtask.app.data.local.entity.TaskEntity

// Constantes que se comparten entre NotificationHelper y ReminderWorker
const val CHANNEL_REMINDERS = "devtask_reminders"
const val EXTRA_TASK_ID = "task_id"
const val EXTRA_TASK_TITLE = "task_title"

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager,
    private val workManager: WorkManager
) {

    // Se ejecuta al crear el objeto — crea los canales de notificación
    // Android 8+ requiere canales para clasificar las notificaciones
    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val remindersChannel = NotificationChannel(
            CHANNEL_REMINDERS,
            "Recordatorios de tareas",
            NotificationManager.IMPORTANCE_HIGH // aparece con sonido y en pantalla
        ).apply {
            description = "Recordatorios de tus tareas pendientes en DevTask"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(remindersChannel)
    }

    // Programa una notificación para que aparezca en la fecha del recordatorio
    fun scheduleTaskReminder(task: TaskEntity) {
        val reminderTime = task.reminderAt ?: return
        val now = LocalDateTime.now()

        // Si la fecha ya pasó no programamos nada
        if (reminderTime.isBefore(now)) return

        // Calculamos cuántos milisegundos faltan para el recordatorio
        val delay = Duration.between(now, reminderTime).toMillis()

        // Datos que le pasamos al Worker cuando se ejecute
        val data = workDataOf(
            EXTRA_TASK_ID to task.id,
            EXTRA_TASK_TITLE to task.title
        )

        // Creamos el trabajo que se ejecutará después del delay
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("reminder_${task.id}")
            .build()

        // enqueueUniqueWork evita duplicados — si ya existe uno lo reemplaza
        workManager.enqueueUniqueWork(
            "reminder_${task.id}",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    // Cancela el recordatorio de una tarea (cuando se edita o elimina)
    fun cancelTaskReminder(taskId: Long) {
        workManager.cancelUniqueWork("reminder_$taskId")
    }

    // Muestra la notificación en pantalla
    fun showReminderNotification(taskId: Long, taskTitle: String) {

        // Al tocar la notificación abre la app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TASK_ID, taskId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, taskId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("⏰ Recordatorio: $taskTitle")
            .setContentText("Tienes una tarea pendiente que atender.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // se cierra al tocarla
            .build()

        notificationManager.notify(taskId.toInt(), notification)
    }
}