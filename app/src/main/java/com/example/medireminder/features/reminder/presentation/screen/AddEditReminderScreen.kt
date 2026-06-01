package com.example.medireminder.features.reminder.presentation.screen

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType
import com.example.medireminder.features.reminder.presentation.state.ReminderFormState
import com.example.medireminder.features.reminder.presentation.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.*

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
                title = { Text(if (reminderId == null) "Schedule Reminder" else "Edit Reminder") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Medicine Assignment Selection
            MemberMedicineDropdown(
                assignments = uiState.memberMedicines,
                selectedId = formState.memberMedicineId,
                onSelected = viewModel::onMemberMedicineChange,
                error = formState.validationErrors["memberMedicineId"]
            )

            // Time Picker
            TimePickerField(
                label = "Reminder Time *",
                value = formState.reminderTime,
                onValueChange = viewModel::onReminderTimeChange,
                error = formState.validationErrors["reminderTime"]
            )

            // Repeat Type
            Text("Repeat", style = MaterialTheme.typography.titleMedium)
            ReminderRepeatType.entries.forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onRepeatTypeChange(type) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = formState.repeatType == type,
                        onClick = { viewModel.onRepeatTypeChange(type) }
                    )
                    Text(type.name.replace("_", " "), modifier = Modifier.padding(start = 8.dp))
                }
            }

            if (formState.repeatType == ReminderRepeatType.SPECIFIC_WEEKDAYS) {
                Text("Select Weekdays", style = MaterialTheme.typography.titleSmall)
                val weekdays = listOf(
                    "Mon" to 1, "Tue" to 2, "Wed" to 3, "Thu" to 4,
                    "Fri" to 5, "Sat" to 6, "Sun" to 7
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    weekdays.forEach { (label, day) ->
                        FilterChip(
                            selected = formState.repeatDays.contains(day),
                            onClick = { viewModel.onRepeatDaysChange(day) },
                            label = { Text(label) }
                        )
                    }
                }
                formState.validationErrors["repeatDays"]?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            // Date Pickers
            DatePickerField(
                label = "Start Date *",
                value = formState.startDate,
                onValueChange = viewModel::onStartDateChange,
                error = formState.validationErrors["startDate"]
            )

            DatePickerField(
                label = "End Date (Optional)",
                value = formState.endDate,
                onValueChange = viewModel::onEndDateChange,
                error = formState.validationErrors["endDate"]
            )

            // Preferences
            HorizontalDivider()
            Text("Preferences", style = MaterialTheme.typography.titleMedium)

            PreferenceSwitch(
                label = "Vibration",
                checked = formState.vibrationEnabled,
                onCheckedChange = viewModel::onVibrationChange
            )

            PreferenceSwitch(
                label = "Notifications",
                checked = formState.notificationEnabled,
                onCheckedChange = viewModel::onNotificationChange
            )

            PreferenceSwitch(
                label = "Snooze Enabled",
                checked = formState.snoozeEnabled,
                onCheckedChange = viewModel::onSnoozeChange
            )

            if (formState.snoozeEnabled) {
                OutlinedTextField(
                    value = formState.snoozeDuration,
                    onValueChange = viewModel::onSnoozeDurationChange,
                    label = { Text("Snooze Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.validationErrors.containsKey("snoozeDuration"),
                    supportingText = { formState.validationErrors["snoozeDuration"]?.let { Text(it) } }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveReminder(reminderId, onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (reminderId == null) "Schedule Reminder" else "Update Reminder")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberMedicineDropdown(
    assignments: List<MemberMedicine>,
    selectedId: String,
    onSelected: (String) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = assignments.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected?.let { "${it.medicineName} (${it.familyMemberName})" } ?: "Select Medication Assignment",
            onValueChange = {},
            readOnly = true,
            label = { Text("Medication Assignment *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
            isError = error != null,
            supportingText = { error?.let { Text(it) } }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            assignments.forEach { assignment ->
                DropdownMenuItem(
                    text = { Text("${assignment.medicineName} for ${assignment.familyMemberName}") },
                    onClick = {
                        onSelected(assignment.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimePickerField(
    label: String,
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
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { timePickerDialog.show() }) {
                Icon(Icons.Default.Notifications, contentDescription = "Select Time")
            }
        },
        modifier = Modifier.fillMaxWidth().clickable { timePickerDialog.show() },
        isError = error != null,
        supportingText = { error?.let { Text(it) } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    value: Long?,
    onValueChange: (Long?) -> Unit,
    error: String?
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = value ?: System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onValueChange(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val dateString = value?.let { dateFormatter.format(Date(it)) } ?: ""

    OutlinedTextField(
        value = dateString,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
            }
        },
        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
        isError = error != null,
        supportingText = { error?.let { Text(it) } }
    )
}

@Composable
fun PreferenceSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
