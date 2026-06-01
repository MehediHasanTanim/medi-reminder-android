package com.example.medireminder.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medireminder.core.notification.NotificationHelper
import com.example.medireminder.features.reminder.domain.usecase.DismissReminderUseCase
import com.example.medireminder.features.reminder.domain.usecase.MarkReminderTakenUseCase
import com.example.medireminder.features.reminder.domain.usecase.SkipReminderUseCase
import com.example.medireminder.features.reminder.domain.usecase.SnoozeReminderUseCase
import com.example.medireminder.features.stock.domain.StockReductionService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var markReminderTakenUseCase: MarkReminderTakenUseCase

    @Inject
    lateinit var skipReminderUseCase: SkipReminderUseCase

    @Inject
    lateinit var snoozeReminderUseCase: SnoozeReminderUseCase

    @Inject
    lateinit var dismissReminderUseCase: DismissReminderUseCase

    @Inject
    lateinit var stockReductionService: StockReductionService

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var soundController: AlarmSoundController

    @Inject
    lateinit var vibrationController: AlarmVibrationController

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(AlarmConstants.EXTRA_REMINDER_ID) ?: return
        val action = intent.action ?: return
        val scheduledTime = intent.getLongExtra(
            AlarmConstants.EXTRA_SCHEDULED_TIME,
            System.currentTimeMillis()
        )

        // Stop sound and vibration immediately
        soundController.stop()
        vibrationController.stop()

        CoroutineScope(Dispatchers.IO).launch {
            when (action) {
                AlarmConstants.ACTION_REMINDER_TAKEN -> {
                    markReminderTakenUseCase(reminderId, scheduledTime)
                        .onSuccess {
                            stockReductionService.reduceStockForTakenReminder(reminderId)
                        }
                    notificationHelper.dismissNotification(reminderId)
                }
                AlarmConstants.ACTION_REMINDER_SNOOZE -> {
                    snoozeReminderUseCase(reminderId, scheduledTime)
                    notificationHelper.dismissNotification(reminderId)
                }
                AlarmConstants.ACTION_REMINDER_SKIP -> {
                    skipReminderUseCase(reminderId, scheduledTime)
                    notificationHelper.dismissNotification(reminderId)
                }
                AlarmConstants.ACTION_REMINDER_DISMISS -> {
                    dismissReminderUseCase(reminderId, scheduledTime)
                    notificationHelper.dismissNotification(reminderId)
                }
            }
        }
    }
}
