package com.example.medireminder.features.reminder.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.reminder.presentation.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailsScreen(
    reminderId: String,
    onNavigateBack: () -> Unit,
    onEditReminder: (String) -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reminder = uiState.selectedReminder

    LaunchedEffect(reminderId) {
        viewModel.loadReminderDetails(reminderId)
    }

    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminder Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditReminder(reminderId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showCancelDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Cancel Reminder", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (reminder != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = reminder.reminderTime,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = reminder.repeatType.name.replace("_", " "),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    SectionCard(title = "Medication Information") {
                        InfoRow(label = "Medicine", value = reminder.medicineName ?: "Unknown")
                        InfoRow(label = "Patient", value = reminder.familyMemberName ?: "Unknown")
                        InfoRow(label = "Dosage", value = "${reminder.dosageQuantity} ${reminder.medicineUnit ?: ""}")
                        if (!reminder.instructions.isNullOrBlank()) {
                            InfoRow(label = "Instructions", value = reminder.instructions)
                        }
                    }

                    SectionCard(title = "Schedule Information") {
                        val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        InfoRow(label = "Start Date", value = df.format(Date(reminder.startDate)))
                        InfoRow(label = "End Date", value = reminder.endDate?.let { df.format(Date(it)) } ?: "Ongoing")
                        if (reminder.repeatDays.isNotEmpty()) {
                            val days = listOf("", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            val selectedDays = reminder.repeatDays.joinToString(", ") { days[it] }
                            InfoRow(label = "Repeat Days", value = selectedDays)
                        }
                    }

                    SectionCard(title = "Settings") {
                        InfoRow(label = "Status", value = if (reminder.isActive) "Active" else "Inactive")
                        InfoRow(label = "Vibration", value = if (reminder.vibrationEnabled) "On" else "Off")
                        InfoRow(label = "Notification", value = if (reminder.notificationEnabled) "On" else "Off")
                        InfoRow(label = "Snooze", value = if (reminder.snoozeEnabled) "${reminder.snoozeDuration} mins" else "Disabled")
                    }

                    if (reminder.isActive) {
                        Button(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Disable Reminder")
                        }
                    }
                }
            } else if (!uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Reminder not found")
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Disable Reminder") },
            text = { Text("Are you sure you want to disable this medication reminder?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelReminder(reminderId) {
                            onNavigateBack()
                        }
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Disable")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Active")
                }
            }
        )
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
