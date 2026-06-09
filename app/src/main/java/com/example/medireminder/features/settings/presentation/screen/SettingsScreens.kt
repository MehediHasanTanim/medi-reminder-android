package com.example.medireminder.features.settings.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.settings.data.mapper.label
import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.model.TimeFormat
import com.example.medireminder.features.settings.presentation.components.SettingsDropdownRow
import com.example.medireminder.features.settings.presentation.components.SettingsInfoRow
import com.example.medireminder.features.settings.presentation.components.SettingsRow
import com.example.medireminder.features.settings.presentation.components.SettingsSection
import com.example.medireminder.features.settings.presentation.components.SettingsSwitchRow
import com.example.medireminder.features.settings.presentation.viewmodel.SettingsViewModel

// ─── Main Settings Screen ───
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAppearanceClick: () -> Unit,
    onReminderClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onStockClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Appearance
            SettingsSection(
                title = "Appearance",
                icon = Icons.Filled.Brush,
                color = Color(0xFF7B1FA2)
            ) {
                SettingsRow(
                    title = "Theme & Time Format",
                    subtitle = "System, light, dark mode and clock format",
                    icon = Icons.Filled.Brush,
                    iconColor = Color(0xFF7B1FA2),
                    onClick = onAppearanceClick
                )
            }

            // Reminders
            SettingsSection(
                title = "Reminders",
                icon = Icons.Filled.Schedule,
                color = Color(0xFF1565C0)
            ) {
                SettingsRow(
                    title = "Reminder Defaults",
                    subtitle = "Snooze duration, vibration, full-screen alarm",
                    icon = Icons.Filled.Schedule,
                    iconColor = Color(0xFF1565C0),
                    onClick = onReminderClick
                )
            }

            // Notifications
            SettingsSection(
                title = "Notifications",
                icon = Icons.Filled.Notifications,
                color = Color(0xFF2E7D32)
            ) {
                SettingsRow(
                    title = "Notification Preferences",
                    subtitle = "Sound, vibration, and alert behavior",
                    icon = Icons.Filled.Notifications,
                    iconColor = Color(0xFF2E7D32),
                    onClick = onNotificationClick
                )
            }

            // Stock
            SettingsSection(
                title = "Stock Management",
                icon = Icons.Filled.Inventory,
                color = Color(0xFFEF6C00)
            ) {
                SettingsRow(
                    title = "Stock Settings",
                    subtitle = "Reduction mode, thresholds, expiry alerts",
                    icon = Icons.Filled.Inventory,
                    iconColor = Color(0xFFEF6C00),
                    onClick = onStockClick
                )
            }

            // About
            SettingsSection(
                title = "About",
                icon = Icons.Filled.Info,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                SettingsInfoRow(
                    title = "MediReminder",
                    subtitle = "Version 1.0 • Medicine Reminder & Stock Manager",
                    icon = Icons.Filled.Medication,
                    iconColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ─── Appearance Settings ───
@Composable
fun AppearanceSettingsScreen(onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    SettingsSubScreen("Appearance", onNavigateBack) {
        SettingsDropdownRow(
            title = "Theme",
            selected = state.settings.themeMode,
            options = AppThemeMode.entries.toList(),
            label = { it.label() },
            onSelected = viewModel::updateTheme,
            icon = Icons.Filled.Brush,
            iconColor = Color(0xFF7B1FA2)
        )
        SettingsDropdownRow(
            title = "Time Format",
            selected = state.settings.timeFormat,
            options = TimeFormat.entries.toList(),
            label = { it.label() },
            onSelected = viewModel::updateTimeFormat,
            icon = Icons.Filled.ViewAgenda,
            iconColor = Color(0xFF1565C0)
        )
        MessageBanner(state.errorMessage, state.successMessage)
    }
}

// ─── Reminder Settings ───
@Composable
fun ReminderSettingsScreen(onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    SettingsSubScreen("Reminder Defaults", onNavigateBack) {
        SettingsDropdownRow(
            title = "Default Snooze Duration",
            selected = state.settings.defaultSnoozeDurationMinutes,
            options = listOf(5, 10, 15, 30),
            label = { "$it minutes" },
            onSelected = viewModel::updateDefaultSnoozeDuration,
            icon = Icons.Filled.Timer,
            iconColor = Color(0xFF1565C0)
        )
        SettingsSwitchRow(
            title = "Default Vibration",
            checked = state.settings.defaultVibrationEnabled,
            onCheckedChange = viewModel::updateDefaultVibration,
            subtitle = "Vibrate device when reminder triggers",
            icon = Icons.Filled.Vibration,
            iconColor = Color(0xFF1565C0)
        )
        SettingsSwitchRow(
            title = "Full-Screen Alarm",
            checked = state.settings.fullScreenAlarmEnabled,
            onCheckedChange = viewModel::updateFullScreenAlarm,
            subtitle = "Show full-screen reminder overlay when alarm fires",
            icon = Icons.Filled.Visibility,
            iconColor = Color(0xFF1565C0)
        )
        MessageBanner(state.errorMessage, state.successMessage)
    }
}

// ─── Notification Settings ───
@Composable
fun NotificationSettingsScreen(onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val uri = remember(state.settings.notificationSoundUri) {
        mutableStateOf(state.settings.notificationSoundUri ?: "")
    }
    SettingsSubScreen("Notification Preferences", onNavigateBack) {
        SettingsSwitchRow(
            title = "Default Vibration",
            checked = state.settings.defaultVibrationEnabled,
            onCheckedChange = viewModel::updateDefaultVibration,
            subtitle = "Vibrate when reminders are triggered",
            icon = Icons.Filled.Vibration,
            iconColor = Color(0xFF2E7D32)
        )

        Text(
            text = "Notification Sound",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Android notification permission is required for reminder alerts.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = uri.value,
            onValueChange = { uri.value = it },
            label = { Text("Custom Sound URI") },
            placeholder = { Text("Use system default") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = { Text("Leave blank for system default notification sound") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.updateNotificationSound(uri.value.ifBlank { null }) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Save Sound", fontWeight = FontWeight.SemiBold)
        }

        MessageBanner(state.errorMessage, state.successMessage)
    }
}

// ─── Stock Settings ───
@Composable
fun StockSettingsScreen(onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val threshold = remember(state.settings.defaultLowStockThreshold) {
        mutableStateOf(state.settings.defaultLowStockThreshold.toString())
    }
    val expiryDays = remember(state.settings.expiryAlertDays) {
        mutableStateOf(state.settings.expiryAlertDays.toString())
    }
    SettingsSubScreen("Stock Settings", onNavigateBack) {
        SettingsDropdownRow(
            title = "Stock Reduction Mode",
            selected = state.settings.stockReductionMode,
            options = StockReductionMode.entries.toList(),
            label = { it.label() },
            onSelected = viewModel::updateStockReductionMode,
            icon = Icons.Filled.Inventory,
            iconColor = Color(0xFFEF6C00)
        )

        Text(
            text = "Default Low Stock Threshold",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp)
        )
        OutlinedTextField(
            value = threshold.value,
            onValueChange = { threshold.value = it },
            label = { Text("Quantity") },
            placeholder = { Text("e.g. 5") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = { viewModel.updateLowStockThreshold(threshold.value.toDoubleOrNull() ?: -1.0) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Save Threshold", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Expiry Alert Days",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp)
        )
        OutlinedTextField(
            value = expiryDays.value,
            onValueChange = { expiryDays.value = it },
            label = { Text("Days before expiry") },
            placeholder = { Text("e.g. 7") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = { viewModel.updateExpiryAlertDays(expiryDays.value.toIntOrNull() ?: -1) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Save Expiry Days", fontWeight = FontWeight.SemiBold)
        }

        MessageBanner(state.errorMessage, state.successMessage)
    }
}

// ─── Shared Sub-Screen Scaffold ───
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsSubScreen(
    title: String,
    onNavigateBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(title)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

// ─── Message Banner ───
@Composable
private fun MessageBanner(error: String?, success: String?) {
    error?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        )
    }
    success?.let {
        Text(
            text = it,
            color = Color(0xFF2E7D32),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF2E7D32).copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        )
    }
}
