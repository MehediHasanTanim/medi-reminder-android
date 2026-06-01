package com.example.medireminder.features.medicine.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.medicine.presentation.state.MedicineFormState
import com.example.medireminder.features.medicine.presentation.viewmodel.MedicineViewModel
import com.example.medireminder.ui.theme.MediReminderTheme

@Composable
fun AddEditMedicineScreen(
    medicineId: String?,
    onNavigateBack: () -> Unit,
    viewModel: MedicineViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val isEditMode = medicineId != null

    LaunchedEffect(medicineId) {
        if (isEditMode && medicineId != null) {
            viewModel.loadMedicineDetails(medicineId)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    AddEditMedicineContent(
        isEditMode = isEditMode,
        formState = formState,
        onNameChange = viewModel::onNameChange,
        onGenericNameChange = viewModel::onGenericNameChange,
        onTypeChange = viewModel::onTypeChange,
        onStrengthChange = viewModel::onStrengthChange,
        onUnitChange = viewModel::onUnitChange,
        onManufacturerChange = viewModel::onManufacturerChange,
        onNotesChange = viewModel::onNotesChange,
        onActiveStatusChange = viewModel::onActiveStatusChange,
        onSave = { viewModel.saveMedicine(medicineId) {} },
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedicineContent(
    isEditMode: Boolean,
    formState: MedicineFormState,
    onNameChange: (String) -> Unit,
    onGenericNameChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onStrengthChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onManufacturerChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onActiveStatusChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val medicineTypes = listOf("Tablet", "Capsule", "Syrup", "Injection", "Drops", "Inhaler", "Cream", "Powder", "Other")
    val units = listOf("Tablet", "Capsule", "ml", "mg", "drop", "puff", "vial", "sachet", "unit", "other")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Medicine" else "Add Medicine", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
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
            OutlinedTextField(
                value = formState.name,
                onValueChange = onNameChange,
                label = { Text("Medicine Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = formState.genericName,
                onValueChange = onGenericNameChange,
                label = { Text("Generic Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DropdownField(
                    label = "Type *",
                    options = medicineTypes,
                    selectedOption = formState.medicineType,
                    onOptionSelected = onTypeChange,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = formState.strength,
                    onValueChange = onStrengthChange,
                    label = { Text("Strength") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("e.g. 500mg") },
                    singleLine = true
                )
            }

            DropdownField(
                label = "Unit *",
                options = units,
                selectedOption = formState.unit,
                onOptionSelected = onUnitChange,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.manufacturer,
                onValueChange = onManufacturerChange,
                label = { Text("Manufacturer") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = formState.notes,
                onValueChange = onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (isEditMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active Status")
                    Switch(
                        checked = formState.isActive,
                        onCheckedChange = onActiveStatusChange
                    )
                }
            }

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (isEditMode) "Update Medicine" else "Save Medicine")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditMedicinePreview() {
    MediReminderTheme {
        AddEditMedicineContent(
            isEditMode = false,
            formState = MedicineFormState(),
            onNameChange = {},
            onGenericNameChange = {},
            onTypeChange = {},
            onStrengthChange = {},
            onUnitChange = {},
            onManufacturerChange = {},
            onNotesChange = {},
            onActiveStatusChange = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}
