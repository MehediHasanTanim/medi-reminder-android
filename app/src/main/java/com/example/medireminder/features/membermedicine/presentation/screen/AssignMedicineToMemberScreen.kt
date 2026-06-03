package com.example.medireminder.features.membermedicine.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.membermedicine.presentation.state.MemberMedicineFormState
import com.example.medireminder.features.membermedicine.presentation.viewmodel.MemberMedicineViewModel
import com.example.medireminder.ui.theme.MediReminderTheme
import com.example.medireminder.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ──────────────────────────────────────────────
// Screen Composable
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignMedicineToMemberScreen(
    onNavigateBack: () -> Unit,
    viewModel: MemberMedicineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            onNavigateBack()
            viewModel.clearMessages()
        }
    }

    AssignMedicineContent(
        title = "Assign Medicine",
        formState = formState,
        familyMembers = uiState.familyMembers,
        medicines = uiState.medicines,
        onFamilyMemberChange = viewModel::onFamilyMemberChange,
        onMedicineChange = viewModel::onMedicineChange,
        onDosageChange = viewModel::onDosageQuantityChange,
        onFrequencyChange = viewModel::onFrequencyChange,
        onStartDateChange = viewModel::onStartDateChange,
        onEndDateChange = viewModel::onEndDateChange,
        onInstructionsChange = viewModel::onInstructionsChange,
        onAutoReduceStockChange = viewModel::onAutoReduceStockChange,
        onSave = { viewModel.saveAssignment(onSuccess = onNavigateBack) },
        onNavigateBack = onNavigateBack
    )
}

// ──────────────────────────────────────────────
// Content Composable
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignMedicineContent(
    title: String,
    formState: MemberMedicineFormState,
    familyMembers: List<FamilyMember>,
    medicines: List<Medicine>,
    onFamilyMemberChange: (String) -> Unit,
    onMedicineChange: (String) -> Unit,
    onDosageChange: (String) -> Unit,
    onFrequencyChange: (String) -> Unit,
    onStartDateChange: (Long?) -> Unit,
    onEndDateChange: (Long?) -> Unit,
    onInstructionsChange: (String) -> Unit,
    onAutoReduceStockChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
            // Assign Icon
            AssignAvatarSection()

            // Selection Card
            FormSectionHeader(icon = Icons.Default.Person, title = "Select Member & Medicine")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    MemberDropdownField(
                        members = familyMembers,
                        selectedId = formState.familyMemberId,
                        onSelected = onFamilyMemberChange,
                        error = formState.validationErrors["familyMemberId"]
                    )
                    MedicineDropdownField(
                        medicines = medicines,
                        selectedId = formState.medicineId,
                        onSelected = onMedicineChange,
                        error = formState.validationErrors["medicineId"]
                    )
                }
            }

            // Dosage Card
            FormSectionHeader(icon = Icons.Default.Straighten, title = "Dosage Details")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FormTextField(
                            value = formState.dosageQuantity,
                            onValueChange = onDosageChange,
                            label = "Dosage",
                            placeholder = "e.g. 1",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            isError = formState.validationErrors.containsKey("dosageQuantity"),
                            errorMessage = formState.validationErrors["dosageQuantity"]
                        )
                        FormTextField(
                            value = formState.frequencyPerDay,
                            onValueChange = onFrequencyChange,
                            label = "Frequency/Day",
                            placeholder = "e.g. 2",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            isError = formState.validationErrors.containsKey("frequencyPerDay"),
                            errorMessage = formState.validationErrors["frequencyPerDay"]
                        )
                    }

                    // Daily Total
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.06f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Daily Total", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(
                                    "Quantity per day",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${formState.dailyTotalQuantity}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        }
                    }
                }
            }

            // Schedule Card
            FormSectionHeader(icon = Icons.Default.CalendarMonth, title = "Schedule")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    DateField(
                        label = "Start Date *",
                        value = formState.startDate,
                        onValueChange = onStartDateChange,
                        error = formState.validationErrors["startDate"]
                    )
                    DateField(
                        label = "End Date (Optional)",
                        value = formState.endDate,
                        onValueChange = onEndDateChange,
                        error = formState.validationErrors["endDate"]
                    )
                }
            }

            // Instructions Card
            FormSectionHeader(icon = Icons.Default.Description, title = "Instructions")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    FormTextField(
                        value = formState.instructions,
                        onValueChange = onInstructionsChange,
                        label = "Instructions",
                        placeholder = "e.g. Take after food with water",
                        minLines = 3,
                        singleLine = false
                    )
                }
            }

            // Auto Reduce Stock Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto Reduce Stock", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(
                            "Automatically subtract from stock on intake",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = formState.autoReduceStock, onCheckedChange = onAutoReduceStockChange)
                }
            }

            // Save Button
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Save Assignment", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ──────────────────────────────────────────────
// Assign Avatar Section
// ──────────────────────────────────────────────

@Composable
private fun AssignAvatarSection() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(80.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Medication, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(36.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Assign Medicine to Member", fontSize = 13.sp, color = PrimaryGreen, fontWeight = FontWeight.Medium)
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
// Form Text Field
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) }
        } else null,
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        supportingText = errorMessage?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreen,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            cursorColor = PrimaryGreen
        )
    )
}

// ──────────────────────────────────────────────
// Member Dropdown
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemberDropdownField(
    members: List<FamilyMember>,
    selectedId: String,
    onSelected: (String) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedMember = members.find { it.id == selectedId }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedMember?.fullName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Family Member *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Select family member", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.7f)) },
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
            members.forEach { member ->
                DropdownMenuItem(
                    text = { Text(member.fullName, fontWeight = if (member.id == selectedId) FontWeight.SemiBold else FontWeight.Normal) },
                    onClick = { onSelected(member.id); expanded = false }
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
    medicines: List<Medicine>,
    selectedId: String,
    onSelected: (String) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedMedicine = medicines.find { it.id == selectedId }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedMedicine?.let { "${it.name} (${it.strength ?: ""} ${it.unit})" } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Medicine *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Select medicine", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(Icons.Default.Medication, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.7f)) },
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
            medicines.forEach { medicine ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${medicine.name} (${medicine.strength ?: ""} ${medicine.unit})",
                            fontWeight = if (medicine.id == selectedId) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    onClick = { onSelected(medicine.id); expanded = false }
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Date Field
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
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
// Preview
// ──────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun AssignMedicinePreview() {
    MediReminderTheme {
        AssignMedicineContent(
            title = "Assign Medicine",
            formState = MemberMedicineFormState(
                familyMemberId = "",
                medicineId = "",
                dosageQuantity = "1",
                frequencyPerDay = "2",
                instructions = "Take after food with water",
                startDate = System.currentTimeMillis(),
                autoReduceStock = true
            ),
            familyMembers = listOf(
                FamilyMember("1", "John", 30, "Male", "Self (You)", "O+", "1234567890", "", true, 0, 0),
                FamilyMember("2", "Sarah", 28, "Female", "Wife", "A+", "9876543210", "", true, 0, 0),
                FamilyMember("3", "Dad", 60, "Male", "Father", "B+", "5551234", "", true, 0, 0),
                FamilyMember("4", "Mom", 55, "Female", "Mother", "AB+", "5555678", "", true, 0, 0)
            ),
            medicines = listOf(
                Medicine("m1", "Napa", "Paracetamol", "Tablet", "500mg", "Tablet", "Beximco", "", true, 0, 0),
                Medicine("m2", "Metformin", "Metformin HCl", "Tablet", "500mg", "Tablet", "Square", "", true, 0, 0)
            ),
            onFamilyMemberChange = {},
            onMedicineChange = {},
            onDosageChange = {},
            onFrequencyChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onInstructionsChange = {},
            onAutoReduceStockChange = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}
