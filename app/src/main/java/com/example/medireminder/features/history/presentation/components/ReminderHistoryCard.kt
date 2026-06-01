package com.example.medireminder.features.history.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medireminder.core.common.DateTimeUtils
import com.example.medireminder.features.history.domain.model.ReminderHistory
import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus
import com.example.medireminder.features.settings.domain.model.TimeFormat

@Composable
fun ReminderHistoryCard(
    item: ReminderHistory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.HOUR_12
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.medicineName ?: "Unknown medicine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.memberName ?: "Unknown member",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(item.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Scheduled: ${DateTimeUtils.formatShortDateTime(item.scheduledTime, timeFormat)}",
                style = MaterialTheme.typography.bodySmall
            )
            item.actionTime?.let {
                Text(
                    text = "Action: ${DateTimeUtils.formatShortDateTime(it, timeFormat)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: ReminderHistoryStatus) {
    val color = when (status) {
        ReminderHistoryStatus.TAKEN -> Color(0xFF2E7D32)
        ReminderHistoryStatus.SKIPPED -> Color(0xFFF57C00)
        ReminderHistoryStatus.MISSED -> MaterialTheme.colorScheme.error
        ReminderHistoryStatus.SNOOZED -> Color(0xFF1565C0)
        ReminderHistoryStatus.DISMISSED -> MaterialTheme.colorScheme.outline
    }
    AssistChip(
        onClick = {},
        label = { Text(status.name) },
        leadingIcon = null,
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(labelColor = color)
    )
}
