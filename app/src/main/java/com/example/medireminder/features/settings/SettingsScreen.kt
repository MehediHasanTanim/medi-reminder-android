package com.example.medireminder.features.settings

import androidx.compose.runtime.Composable
import com.example.medireminder.features.settings.presentation.screen.SettingsScreen as SettingsScreenContent

@Composable
fun SettingsScreen(
    onAppearanceClick: () -> Unit = {},
    onReminderClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onStockClick: () -> Unit = {}
) {
    SettingsScreenContent(
        onAppearanceClick = onAppearanceClick,
        onReminderClick = onReminderClick,
        onNotificationClick = onNotificationClick,
        onStockClick = onStockClick
    )
}
