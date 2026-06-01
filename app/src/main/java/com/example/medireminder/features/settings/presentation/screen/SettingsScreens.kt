package com.example.medireminder.features.settings.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.settings.data.mapper.label
import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.model.TimeFormat
import com.example.medireminder.features.settings.presentation.components.SettingsDropdownRow
import com.example.medireminder.features.settings.presentation.components.SettingsRow
import com.example.medireminder.features.settings.presentation.components.SettingsSection
import com.example.medireminder.features.settings.presentation.components.SettingsSwitchRow
import com.example.medireminder.features.settings.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAppearanceClick: () -> Unit,
    onReminderClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onStockClick: () -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSection("Appearance") {
                SettingsRow("Theme and time format", "System, light, dark, 12-hour or 24-hour", onAppearanceClick)
            }
            SettingsSection("Reminder") {
                SettingsRow("Reminder defaults", "Snooze, vibration, full-screen alarm", onReminderClick)
            }
            SettingsSection("Notification") {
                SettingsRow("Notification sound", "Use system default or custom URI", onNotificationClick)
            }
            SettingsSection("Stock") {
                SettingsRow("Stock behavior", "Reduction mode, low stock, expiry alerts", onStockClick)
            }
            SettingsSection("About") {
                SettingsRow("MediReminder", "Local reminder and medicine stock manager", onClick = {})
            }
        }
    }
}

@Composable
fun AppearanceSettingsScreen(onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    SettingsSubScreen("Appearance", onNavigateBack) {
        SettingsDropdownRow("Theme", state.settings.themeMode, AppThemeMode.entries.toList(), { it.label() }, viewModel::updateTheme)
        SettingsDropdownRow("Time format", state.settings.timeFormat, TimeFormat.entries.toList(), { it.label() }, viewModel::updateTimeFormat)
        MessageText(state.errorMessage)
    }
}

@Composable
fun ReminderSettingsScreen(onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    SettingsSubScreen("Reminder Defaults", onNavigateBack) {
        SettingsDropdownRow(
            title = "Default snooze duration",
            selected = state.settings.defaultSnoozeDurationMinutes,
            options = listOf(5, 10, 15, 30),
            label = { "$it minutes" },
            onSelected = viewModel::updateDefaultSnoozeDuration
        )
        SettingsSwitchRow("Default vibration", state.settings.defaultVibrationEnabled, viewModel::updateDefaultVibration)
        SettingsSwitchRow("Full-screen alarm", state.settings.fullScreenAlarmEnabled, viewModel::updateFullScreenAlarm)
        MessageText(state.errorMessage)
    }
}

@Composable
fun NotificationSettingsScreen(onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val uri = remember(state.settings.notificationSoundUri) { mutableStateOf(state.settings.notificationSoundUri ?: "") }
    SettingsSubScreen("Notifications", onNavigateBack) {
        Text("Android notification permission may be required for reminder alerts.", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = uri.value,
            onValueChange = { uri.value = it },
            label = { Text("Notification sound URI") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = { Text("Leave blank for system default") }
        )
        Button(onClick = { viewModel.updateNotificationSound(uri.value.ifBlank { null }) }) {
            Text("Save sound")
        }
        SettingsSwitchRow("Default vibration", state.settings.defaultVibrationEnabled, viewModel::updateDefaultVibration)
        MessageText(state.errorMessage)
    }
}

@Composable
fun StockSettingsScreen(onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val threshold = remember(state.settings.defaultLowStockThreshold) { mutableStateOf(state.settings.defaultLowStockThreshold.toString()) }
    val expiryDays = remember(state.settings.expiryAlertDays) { mutableStateOf(state.settings.expiryAlertDays.toString()) }
    SettingsSubScreen("Stock Settings", onNavigateBack) {
        SettingsDropdownRow("Stock reduction mode", state.settings.stockReductionMode, StockReductionMode.entries.toList(), { it.label() }, viewModel::updateStockReductionMode)
        OutlinedTextField(
            value = threshold.value,
            onValueChange = { threshold.value = it },
            label = { Text("Default low stock threshold") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { viewModel.updateLowStockThreshold(threshold.value.toDoubleOrNull() ?: -1.0) }) {
            Text("Save threshold")
        }
        OutlinedTextField(
            value = expiryDays.value,
            onValueChange = { expiryDays.value = it },
            label = { Text("Expiry alert days") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { viewModel.updateExpiryAlertDays(expiryDays.value.toIntOrNull() ?: -1) }) {
            Text("Save expiry days")
        }
        MessageText(state.errorMessage)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsSubScreen(title: String, onNavigateBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun MessageText(message: String?) {
    message?.let {
        Text(it, color = MaterialTheme.colorScheme.error)
    }
}
