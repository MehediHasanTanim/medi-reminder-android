package com.example.medireminder.features.history.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.core.common.DateTimeUtils
import com.example.medireminder.features.history.presentation.components.StatusBadge
import com.example.medireminder.features.history.presentation.viewmodel.ReminderHistoryViewModel
import com.example.medireminder.features.settings.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderHistoryDetailsScreen(
    historyId: String,
    onNavigateBack: () -> Unit,
    viewModel: ReminderHistoryViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val history = uiState.selectedHistory
    val timeFormat = settingsState.settings.timeFormat

    LaunchedEffect(historyId) {
        viewModel.loadHistoryDetails(historyId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (history == null) {
                Text(
                    text = uiState.errorMessage ?: "History item not found",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = history.medicineName ?: "Unknown medicine",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(history.status)
                        }
                        DetailRow("Family member", history.memberName ?: "Unknown")
                        DetailRow("Strength", history.medicineStrength ?: "Not set")
                        DetailRow("Scheduled time", DateTimeUtils.formatDateTime(history.scheduledTime, timeFormat))
                        DetailRow("Action time", history.actionTime?.let { DateTimeUtils.formatDateTime(it, timeFormat) } ?: "No action")
                        DetailRow("Notes", history.notes ?: "None")
                        DetailRow("Reminder ID", history.reminderId)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
