package com.example.medireminder.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.medireminder.MainActivity
import com.example.medireminder.core.alarm.AlarmConstants
import com.example.medireminder.core.alarm.ReminderActionReceiver
import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.presentation.activity.ReminderAlarmActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                CHANNEL_ID,
                "Medicine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for medicine intake reminders"
                enableVibration(true)
                setBypassDnd(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val lowStockChannel = NotificationChannel(
                LOW_STOCK_CHANNEL_ID,
                "Stock Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for low medicine stock"
            }
            notificationManager.createNotificationChannels(listOf(reminderChannel, lowStockChannel))
        }
    }

    fun showReminderNotification(
        reminder: Reminder,
        fullScreenEnabled: Boolean = true
    ) {
        val scheduledTime = System.currentTimeMillis()

        // Full screen intent
        val fullScreenIntent = Intent(context, ReminderAlarmActivity::class.java).apply {
            putExtra(AlarmConstants.EXTRA_REMINDER_ID, reminder.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.hashCode(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Main content intent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.hashCode() + 100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Actions
        val takenPendingIntent = createActionPendingIntent(reminder.id, AlarmConstants.ACTION_REMINDER_TAKEN, 1, scheduledTime)
        val snoozePendingIntent = createActionPendingIntent(reminder.id, AlarmConstants.ACTION_REMINDER_SNOOZE, 2, scheduledTime)
        val skipPendingIntent = createActionPendingIntent(reminder.id, AlarmConstants.ACTION_REMINDER_SKIP, 3, scheduledTime)
        val dismissPendingIntent = createActionPendingIntent(reminder.id, AlarmConstants.ACTION_REMINDER_DISMISS, 4, scheduledTime)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Medicine Reminder: ${reminder.medicineName}")
            .setContentText("Time to take ${reminder.dosageQuantity} ${reminder.medicineUnit ?: ""} for ${reminder.familyMemberName}")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(android.R.drawable.ic_menu_save, "Taken", takenPendingIntent)
            .addAction(android.R.drawable.ic_menu_recent_history, "Snooze", snoozePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Skip", skipPendingIntent)
            .addAction(android.R.drawable.ic_delete, "Dismiss", dismissPendingIntent)

        if (fullScreenEnabled) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
        }

        if (reminder.vibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 500, 500, 500, 500))
        }

        notificationManager.notify(reminder.id.hashCode(), builder.build())
    }

    private fun createActionPendingIntent(
        reminderId: String, 
        actionName: String, 
        requestCodeOffset: Int,
        scheduledTime: Long
    ): PendingIntent {
        val intent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = actionName
            putExtra(AlarmConstants.EXTRA_REMINDER_ID, reminderId)
            putExtra(AlarmConstants.EXTRA_SCHEDULED_TIME, scheduledTime)
        }
        return PendingIntent.getBroadcast(
            context,
            reminderId.hashCode() + requestCodeOffset,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun dismissNotification(reminderId: String) {
        notificationManager.cancel(reminderId.hashCode())
    }

    fun showLowStockNotification(
        medicineId: String,
        medicineName: String,
        quantity: Double,
        unit: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            medicineId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, LOW_STOCK_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Low Stock Alert")
            .setContentText("$medicineName has only $quantity $unit remaining.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(LOW_STOCK_NOTIFICATION_BASE_ID + medicineId.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_ID = "medicine_reminders_channel"
        const val LOW_STOCK_CHANNEL_ID = "medicine_low_stock_channel"
        private const val LOW_STOCK_NOTIFICATION_BASE_ID = 80_000
    }
}
