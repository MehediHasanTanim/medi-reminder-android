package com.example.medireminder.features.history.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus
import com.example.medireminder.features.history.presentation.state.ReminderHistoryFilterState
import com.example.medireminder.features.medicine.domain.model.Medicine

@Composable
fun ReminderHistoryFilterBar(
    filterState: ReminderHistoryFilterState,
    familyMembers: List<FamilyMember>,
    medicines: List<Medicine>,
    onPresetSelected: (String) -> Unit,
    onMemberSelected: (String?) -> Unit,
    onMedicineSelected: (String?) -> Unit,
    onStatusSelected: (ReminderHistoryStatus?) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChipRow {
            listOf("Today", "Yesterday", "Last 7 days", "Last 30 days").forEach { preset ->
                FilterChip(
                    selected = filterState.selectedPreset == preset,
                    onClick = { onPresetSelected(preset) },
                    label = { Text(preset) }
                )
            }
        }

        Text("Family member", style = MaterialTheme.typography.labelMedium)
        ChipRow {
            FilterChip(selected = filterState.selectedMemberId == null, onClick = { onMemberSelected(null) }, label = { Text("All") })
            familyMembers.forEach { member ->
                FilterChip(
                    selected = filterState.selectedMemberId == member.id,
                    onClick = { onMemberSelected(member.id) },
                    label = { Text(member.fullName) }
                )
            }
        }

        Text("Medicine", style = MaterialTheme.typography.labelMedium)
        ChipRow {
            FilterChip(selected = filterState.selectedMedicineId == null, onClick = { onMedicineSelected(null) }, label = { Text("All") })
            medicines.forEach { medicine ->
                FilterChip(
                    selected = filterState.selectedMedicineId == medicine.id,
                    onClick = { onMedicineSelected(medicine.id) },
                    label = { Text(medicine.name) }
                )
            }
        }

        Text("Status", style = MaterialTheme.typography.labelMedium)
        ChipRow {
            FilterChip(selected = filterState.selectedStatus == null, onClick = { onStatusSelected(null) }, label = { Text("All") })
            ReminderHistoryStatus.values().forEach { status ->
                FilterChip(
                    selected = filterState.selectedStatus == status,
                    onClick = { onStatusSelected(status) },
                    label = { Text(status.name) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onApply) {
                Text("Apply")
            }
            OutlinedButton(onClick = onClear) {
                Text("Clear")
            }
        }
    }
}

@Composable
private fun ChipRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}
