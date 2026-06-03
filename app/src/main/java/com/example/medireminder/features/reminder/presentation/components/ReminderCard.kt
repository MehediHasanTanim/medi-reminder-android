package com.example.medireminder.features.reminder.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.ui.theme.PrimaryGreen

@Composable
fun ReminderCard(
    reminder: Reminder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = reminder.isActive
    val repeatLabel = when (reminder.repeatType) {
        com.example.medireminder.features.reminder.domain.model.ReminderRepeatType.ONCE -> "Once"
        com.example.medireminder.features.reminder.domain.model.ReminderRepeatType.DAILY -> "Daily"
        com.example.medireminder.features.reminder.domain.model.ReminderRepeatType.WEEKLY -> "Weekly"
        com.example.medireminder.features.reminder.domain.model.ReminderRepeatType.SPECIFIC_WEEKDAYS -> reminder.repeatDays.joinToString(",") { day -> listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun").getOrElse(day % 7) { "" } }
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notification icon circle
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(
                    if (isActive) PrimaryGreen.copy(alpha = 0.1f) else Color(0xFFF5F5F5)
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                    contentDescription = null,
                    tint = if (isActive) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = reminder.reminderTime,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    if (!isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.errorContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Disabled", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
                Text(
                    text = reminder.medicineName ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Row {
                    reminder.familyMemberName?.let {
                        Text(
                            text = "For $it",
                            fontSize = 12.sp,
                            color = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = repeatLabel,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
