package com.example.medireminder.features.membermedicine.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.membermedicine.presentation.state.MemberMedicineFormState
import com.example.medireminder.features.membermedicine.presentation.viewmodel.MemberMedicineViewModel
import java.text.SimpleDateFormat
import java.util.*

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
                title = { Text(title, fontWeight = FontWeight.Bold) },
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
            // Member Selection
            MemberDropdown(
                members = familyMembers,
                selectedId = formState.familyMemberId,
                onSelected = onFamilyMemberChange,
                error = formState.validationErrors["familyMemberId"]
            )

            // Medicine Selection
            MedicineDropdown(
                medicines = medicines,
                selectedId = formState.medicineId,
                onSelected = onMedicineChange,
                error = formState.validationErrors["medicineId"]
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = formState.dosageQuantity,
                    onValueChange = onDosageChange,
                    label = { Text("Dosage Quantity") },
                    modifier = Modifier.weight(1f),
                    isError = formState.validationErrors.containsKey("dosageQuantity"),
                    supportingText = { formState.validationErrors["dosageQuantity"]?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = formState.frequencyPerDay,
                    onValueChange = onFrequencyChange,
                    label = { Text("Frequency/Day") },
                    modifier = Modifier.weight(1f),
                    isError = formState.validationErrors.containsKey("frequencyPerDay"),
                    supportingText = { formState.validationErrors["frequencyPerDay"]?.let { Text(it) } }
                )
            }

            // Daily Total Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Total Quantity", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${formState.dailyTotalQuantity}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Date Pickers
            DatePickerField(
                label = "Start Date *",
                value = formState.startDate,
                onValueChange = onStartDateChange,
                error = formState.validationErrors["startDate"]
            )

            DatePickerField(
                label = "End Date (Optional)",
                value = formState.endDate,
                onValueChange = onEndDateChange,
                error = formState.validationErrors["endDate"]
            )

            OutlinedTextField(
                value = formState.instructions,
                onValueChange = onInstructionsChange,
                label = { Text("Instructions") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Auto Reduce Stock", fontWeight = FontWeight.SemiBold)
                    Text("Automatically subtract from stock on intake", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = formState.autoReduceStock, onCheckedChange = onAutoReduceStockChange)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Save Assignment")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDropdown(
    members: List<FamilyMember>,
    selectedId: String,
    onSelected: (String) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedMember = members.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedMember?.fullName ?: "Select Family Member",
            onValueChange = {},
            readOnly = true,
            label = { Text("Family Member *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
            isError = error != null,
            supportingText = { error?.let { Text(it) } }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            members.forEach { member ->
                DropdownMenuItem(
                    text = { Text(member.fullName) },
                    onClick = {
                        onSelected(member.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDropdown(
    medicines: List<Medicine>,
    selectedId: String,
    onSelected: (String) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedMedicine = medicines.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedMedicine?.name ?: "Select Medicine",
            onValueChange = {},
            readOnly = true,
            label = { Text("Medicine *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
            isError = error != null,
            supportingText = { error?.let { Text(it) } }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            medicines.forEach { medicine ->
                DropdownMenuItem(
                    text = { Text("${medicine.name} (${medicine.strength} ${medicine.unit})") },
                    onClick = {
                        onSelected(medicine.id)
                        expanded = false
                    }
                )
            }
        }
    }
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
