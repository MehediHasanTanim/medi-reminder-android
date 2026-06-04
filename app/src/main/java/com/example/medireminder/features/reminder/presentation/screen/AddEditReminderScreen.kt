package com.example.medireminder.features.reminder.presentation.screen

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType
import com.example.medireminder.features.reminder.presentation.state.ReminderFormState
import com.example.medireminder.features.reminder.presentation.viewmodel.ReminderViewModel
import com.example.medireminder.ui.theme.MediReminderTheme
import com.example.medireminder.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ──────────────────────────────────────────────
// Screen Composable
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditReminderScreen(
    reminderId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(reminderId) {
        if (reminderId != null) {
            viewModel.loadReminderDetails(reminderId)
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            onNavigateBack()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (reminderId == null) "Schedule Reminder" else "Edit Reminder",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Reminder Icon
            ReminderAvatarSection()

            // Family Member & Medicine Selection
            FormSectionHeader(icon = Icons.Default.NotificationsActive, title = "Family Member & Medicine")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    val uniqueMembers = uiState.memberMedicines
                        .map { it.familyMemberName.orEmpty() }
                        .distinct()
                        .filter { it.isNotBlank() }

                    val selectedMemberName = uiState.memberMedicines
                        .find { it.id == formState.memberMedicineId }
                        ?.familyMemberName.orEmpty()

                    val filteredMedicines = if (selectedMemberName.isNotBlank()) {
                        uiState.memberMedicines.filter {
                            it.familyMemberName.orEmpty() == selectedMemberName
                        }
                    } else {
                        emptyList()
                    }

                    FamilyMemberDropdownField(
                        members = uniqueMembers,
                        selectedMember = selectedMemberName,
                        onMemberSelected = { memberName ->
                            val firstAssignment = uiState.memberMedicines
                                .find { it.familyMemberName.orEmpty() == memberName }
                            viewModel.onMemberMedicineChange(firstAssignment?.id ?: "")
                        },
                        error = formState.validationErrors["memberMedicineId"]
                    )

                    MedicineDropdownField(
                        assignments = filteredMedicines,
                        selectedId = formState.memberMedicineId,
                        onMedicineSelected = { id ->
                            viewModel.onMemberMedicineChange(id)
                        },
                        enabled = selectedMemberName.isNotBlank()
                    )
                }
            }

            // Time & Repeat
            FormSectionHeader(icon = Icons.Default.Schedule, title = "Time & Repeat")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Time picker
                    ReminderTimeField(
                        value = formState.reminderTime,
                        onValueChange = viewModel::onReminderTimeChange,
                        error = formState.validationErrors["reminderTime"]
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // Repeat type
                    Text("Repeat", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    RepeatTypeChips(
                        selectedType = formState.repeatType,
                        onTypeSelected = viewModel::onRepeatTypeChange
                    )

                    // Weekday chips for specific weekdays
                    if (formState.repeatType == ReminderRepeatType.SPECIFIC_WEEKDAYS) {
                        WeekdayChips(
                            selectedDays = formState.repeatDays,
                            onDayToggle = viewModel::onRepeatDaysChange,
                            error = formState.validationErrors["repeatDays"]
                        )
                    }
                }
            }

            // Date Range
            FormSectionHeader(icon = Icons.Default.DateRange, title = "Date Range")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ReminderDateField(
                        label = "Start Date *",
                        value = formState.startDate,
                        onValueChange = viewModel::onStartDateChange,
                        error = formState.validationErrors["startDate"]
                    )
                    ReminderDateField(
                        label = "End Date (Optional)",
                        value = formState.endDate,
                        onValueChange = viewModel::onEndDateChange,
                        error = formState.validationErrors["endDate"]
                    )
                }
            }

            // Preferences
            FormSectionHeader(icon = Icons.Default.FilterAlt, title = "Preferences")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PreferenceToggle(
                        icon = Icons.Default.Vibration,
                        label = "Vibration",
                        description = "Vibrate on reminder",
                        checked = formState.vibrationEnabled,
                        onCheckedChange = viewModel::onVibrationChange
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    PreferenceToggle(
                        icon = Icons.Default.Notifications,
                        label = "Notifications",
                        description = "Show notification alert",
                        checked = formState.notificationEnabled,
                        onCheckedChange = viewModel::onNotificationChange
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    PreferenceToggle(
                        icon = Icons.Default.Timer,
                        label = "Snooze",
                        description = "Allow snoozing reminder",
                        checked = formState.snoozeEnabled,
                        onCheckedChange = viewModel::onSnoozeChange
                    )

                    if (formState.snoozeEnabled) {
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = formState.snoozeDuration,
                            onValueChange = viewModel::onSnoozeDurationChange,
                            label = { Text("Snooze Duration (minutes)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            isError = formState.validationErrors.containsKey("snoozeDuration"),
                            supportingText = { formState.validationErrors["snoozeDuration"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGreen,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                cursorColor = PrimaryGreen
                            )
                        )
                    }
                }
            }

            // Save Button
            Button(
                onClick = { viewModel.saveReminder(reminderId, onNavigateBack) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text(
                    text = if (reminderId == null) "Schedule Reminder" else "Update Reminder",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ──────────────────────────────────────────────
// Reminder Avatar Section
// ──────────────────────────────────────────────

@Composable
private fun ReminderAvatarSection() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(80.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(36.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Set Reminder Details", fontSize = 13.sp, color = PrimaryGreen, fontWeight = FontWeight.Medium)
    }
}

// ──────────────────────────────────────────────
// Form Section Header
// ──────────────────────────────────────────────

@Composable
private fun FormSectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ──────────────────────────────────────────────
// Family Member Dropdown
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FamilyMemberDropdownField(
    members: List<String>,
    selectedMember: String,
    onMemberSelected: (String) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedMember,
            onValueChange = {},
            readOnly = true,
            label = { Text("Family Member *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Select family member", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.7f)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            isError = error != null,
            supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                cursorColor = PrimaryGreen
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            members.forEach { memberName ->
                DropdownMenuItem(
                    text = { Text(memberName, fontWeight = if (memberName == selectedMember) FontWeight.SemiBold else FontWeight.Normal) },
                    onClick = { onMemberSelected(memberName); expanded = false }
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Medicine Dropdown
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicineDropdownField(
    assignments: List<MemberMedicine>,
    selectedId: String,
    onMedicineSelected: (String) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = assignments.find { it.id == selectedId }

    ExposedDropdownMenuBox(expanded = expanded && enabled, onExpandedChange = { if (enabled) expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.medicineName.orEmpty(),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Medicine *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text(if (enabled) "Select medicine" else "Select a member first", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.7f)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                cursorColor = PrimaryGreen
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            assignments.forEach { assignment ->
                DropdownMenuItem(
                    text = { Text("${assignment.medicineName.orEmpty()} (${assignment.dosageQuantity?.toInt() ?: ""} ${assignment.medicineUnit.orEmpty()})", fontWeight = if (assignment.id == selectedId) FontWeight.SemiBold else FontWeight.Normal) },
                    onClick = { onMedicineSelected(assignment.id); expanded = false }
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Repeat Type Chips
// ──────────────────────────────────────────────

@Composable
private fun RepeatTypeChips(
    selectedType: ReminderRepeatType,
    onTypeSelected: (ReminderRepeatType) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        ReminderRepeatType.entries.forEach { type ->
            val label = when (type) {
                ReminderRepeatType.ONCE -> "Once"
                ReminderRepeatType.DAILY -> "Daily"
                ReminderRepeatType.WEEKLY -> "Weekly"
                ReminderRepeatType.SPECIFIC_WEEKDAYS -> "Custom"
            }
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(label, fontSize = 13.sp) }
            )
        }
    }
}

// ──────────────────────────────────────────────
// Weekday Chips
// ──────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WeekdayChips(
    selectedDays: List<Int>,
    onDayToggle: (Int) -> Unit,
    error: String?
) {
    val weekdays = listOf("Mon" to 1, "Tue" to 2, "Wed" to 3, "Thu" to 4, "Fri" to 5, "Sat" to 6, "Sun" to 7)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        weekdays.forEach { (label, day) ->
            FilterChip(
                selected = selectedDays.contains(day),
                onClick = { onDayToggle(day) },
                label = { Text(label, fontSize = 12.sp) }
            )
        }
    }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
}

// ──────────────────────────────────────────────
// Time Field
// ──────────────────────────────────────────────

@Composable
private fun ReminderTimeField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            onValueChange(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
        },
        if (value.isNotBlank()) value.split(":")[0].toInt() else calendar.get(Calendar.HOUR_OF_DAY),
        if (value.isNotBlank()) value.split(":")[1].toInt() else calendar.get(Calendar.MINUTE),
        false
    )

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text("Reminder Time *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        placeholder = { Text("Select time", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.7f)) },
        trailingIcon = {
            IconButton(onClick = { timePickerDialog.show() }) {
                Icon(Icons.Default.Notifications, contentDescription = "Select Time", tint = PrimaryGreen.copy(alpha = 0.7f))
            }
        },
        modifier = Modifier.fillMaxWidth().clickable { timePickerDialog.show() },
        isError = error != null,
        supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreen,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            cursorColor = PrimaryGreen
        )
    )
}

// ──────────────────────────────────────────────
// Date Field
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderDateField(
    label: String,
    value: Long?,
    onValueChange: (Long?) -> Unit,
    error: String?
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = value ?: System.currentTimeMillis())

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { onValueChange(datePickerState.selectedDateMillis); showDatePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val dateString = value?.let { dateFormatter.format(Date(it)) } ?: ""

    OutlinedTextField(
        value = dateString,
        onValueChange = {},
        readOnly = true,
        label = { Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        placeholder = { Text("Select date", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.7f)) },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date", tint = PrimaryGreen.copy(alpha = 0.7f))
            }
        },
        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
        isError = error != null,
        supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreen,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            cursorColor = PrimaryGreen
        )
    )
}

// ──────────────────────────────────────────────
// Preference Toggle
// ──────────────────────────────────────────────

@Composable
private fun PreferenceToggle(
    icon: ImageVector,
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            Text(description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// ──────────────────────────────────────────────
// Preview
// ──────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun AddReminderPreview() {
    MediReminderTheme {
        AddEditReminderScreen(
            reminderId = null,
            onNavigateBack = {}
        )
    }
}
