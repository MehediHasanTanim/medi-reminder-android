package com.example.medireminder.features.reminder.presentation.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.medireminder.core.alarm.AlarmConstants
import com.example.medireminder.core.alarm.ReminderActionReceiver
import com.example.medireminder.features.reminder.presentation.screen.ReminderAlarmScreen
import com.example.medireminder.features.reminder.presentation.viewmodel.ReminderViewModel
import com.example.medireminder.ui.theme.MediReminderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderAlarmActivity : ComponentActivity() {

    private val viewModel: ReminderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup for lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val reminderId = intent.getStringExtra(AlarmConstants.EXTRA_REMINDER_ID) ?: finish().run { return }
        val scheduledTime = intent.getLongExtra(AlarmConstants.EXTRA_SCHEDULED_TIME, System.currentTimeMillis())

        viewModel.loadReminderDetails(reminderId)

        setContent {
            MediReminderTheme {
                val uiState by viewModel.uiState.collectAsState()
                val reminder = uiState.selectedReminder

                if (reminder != null) {
                    ReminderAlarmScreen(
                        reminder = reminder,
                        onTaken = { performAction(AlarmConstants.ACTION_REMINDER_TAKEN, reminderId, scheduledTime) },
                        onSnooze = { performAction(AlarmConstants.ACTION_REMINDER_SNOOZE, reminderId, scheduledTime) },
                        onSkip = { performAction(AlarmConstants.ACTION_REMINDER_SKIP, reminderId, scheduledTime) },
                        onDismiss = { performAction(AlarmConstants.ACTION_REMINDER_DISMISS, reminderId, scheduledTime) }
                    )
                }
            }
        }
    }

    private fun performAction(action: String, reminderId: String, scheduledTime: Long) {
        val intent = Intent(this, ReminderActionReceiver::class.java).apply {
            this.action = action
            putExtra(AlarmConstants.EXTRA_REMINDER_ID, reminderId)
            putExtra(AlarmConstants.EXTRA_SCHEDULED_TIME, scheduledTime)
        }
        sendBroadcast(intent)
        finish()
    }
}
