package com.example.medireminder.features.history.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medireminder.features.history.domain.model.ReminderHistorySummary

@Composable
fun ReminderHistorySummaryCard(
    summary: ReminderHistorySummary?,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryValue("Taken", summary?.takenCount?.toString() ?: "0")
            SummaryValue("Missed", summary?.missedCount?.toString() ?: "0")
            SummaryValue("Skipped", summary?.skippedCount?.toString() ?: "0")
            SummaryValue("Adherence", "${summary?.adherencePercentage ?: 0.0}%")
        }
    }
}

@Composable
private fun SummaryValue(label: String, value: String) {
    Column {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
