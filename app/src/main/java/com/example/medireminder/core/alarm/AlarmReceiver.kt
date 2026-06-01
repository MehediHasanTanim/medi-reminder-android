package com.example.medireminder.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medireminder.core.notification.NotificationHelper
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import com.example.medireminder.features.reminder.presentation.activity.ReminderAlarmActivity
import com.example.medireminder.features.settings.domain.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var soundController: AlarmSoundController

    @Inject
    lateinit var vibrationController: AlarmVibrationController

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(AlarmConstants.EXTRA_REMINDER_ID) ?: return
        val scheduledTime = intent.getLongExtra(AlarmConstants.EXTRA_SCHEDULED_TIME, System.currentTimeMillis())

        CoroutineScope(Dispatchers.IO).launch {
            val reminder = reminderRepository.getReminderById(reminderId)
            if (reminder != null && reminder.isActive) {
                val settings = settingsRepository.observeSettings().first()
                // Show notification
                notificationHelper.showReminderNotification(
                    reminder = reminder,
                    fullScreenEnabled = settings.fullScreenAlarmEnabled
                )

                // Start sound and vibration on Main thread
                withContext(Dispatchers.Main) {
                    soundController.start(context, reminder.alarmTone ?: settings.notificationSoundUri)
                    if (reminder.vibrationEnabled) {
                        vibrationController.start(context)
                    }

                    // Launch full screen activity
                    if (settings.fullScreenAlarmEnabled) {
                        val activityIntent = Intent(context, ReminderAlarmActivity::class.java).apply {
                            putExtra(AlarmConstants.EXTRA_REMINDER_ID, reminder.id)
                            putExtra(AlarmConstants.EXTRA_SCHEDULED_TIME, scheduledTime)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        context.startActivity(activityIntent)
                    }
                }
            }
        }
    }
}
