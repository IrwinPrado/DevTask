package com.devtask.app.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// AlarmReceiver se activa cuando el sistema dispara una alarma programada
// @AndroidEntryPoint permite que Hilt inyecte dependencias aquí
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: return
        if (taskId == -1L) return

        // Muestra la notificación cuando recibe la señal de la alarma
        notificationHelper.showReminderNotification(taskId, taskTitle)
    }
}

// BootReceiver se activa cuando el teléfono se reinicia
// WorkManager restaura los trabajos pendientes automáticamente
// pero necesitamos declarar este receiver en el Manifest para que funcione
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // WorkManager se encarga de re-encolar los recordatorios pendientes
            // no necesitamos hacer nada manualmente aquí
        }
    }
}