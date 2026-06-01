package com.example.medireminder.features.history

import androidx.compose.runtime.Composable
import com.example.medireminder.features.history.presentation.screen.ReminderHistoryScreen

@Composable
fun HistoryScreen(onHistoryClick: (String) -> Unit = {}) {
    ReminderHistoryScreen(onHistoryClick = onHistoryClick)
}
