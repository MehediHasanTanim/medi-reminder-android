package com.example.medireminder.features.history.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.history.presentation.components.ReminderHistoryCard
import com.example.medireminder.features.history.presentation.components.ReminderHistoryFilterBar
import com.example.medireminder.features.history.presentation.components.ReminderHistorySummaryCard
import com.example.medireminder.features.history.presentation.viewmodel.ReminderHistoryViewModel
import com.example.medireminder.features.settings.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderHistoryScreen(
    onHistoryClick: (String) -> Unit,
    viewModel: ReminderHistoryViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val timeFormat = settingsState.settings.timeFormat

    Scaffold(
        topBar = { TopAppBar(title = { Text("Reminder History") }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ReminderHistorySummaryCard(summary = uiState.summary)
                }
                item {
                    ReminderHistoryFilterBar(
                        filterState = filterState,
                        familyMembers = uiState.familyMembers,
                        medicines = uiState.medicines,
                        onPresetSelected = viewModel::selectPreset,
                        onMemberSelected = viewModel::updateMemberFilter,
                        onMedicineSelected = viewModel::updateMedicineFilter,
                        onStatusSelected = viewModel::updateStatusFilter,
                        onApply = viewModel::applyFilters,
                        onClear = viewModel::clearFilters
                    )
                }

                if (!uiState.isLoading && uiState.historyItems.isEmpty()) {
                    item {
                        Text(
                            text = "No reminder history found",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                }

                items(uiState.historyItems) { item ->
                    ReminderHistoryCard(
                        item = item,
                        onClick = { onHistoryClick(item.id) },
                        timeFormat = timeFormat
                    )
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
