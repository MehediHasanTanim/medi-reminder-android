package com.example.medireminder.features.medicine.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.medicine.presentation.viewmodel.MedicineViewModel
import com.example.medireminder.ui.theme.MediReminderTheme

@Composable
fun AddEditMedicineScreen(
    medicineId: String?,
    onNavigateBack: () -> Unit,
    viewModel: MedicineViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(medicineId) {
        if (medicineId != null) {
            viewModel.loadMedicineDetails(medicineId)
        }
    }

    AddEditMedicineContent(
        isEditMode = medicineId != null,
        name = formState.name,
        onNameChange = viewModel::onNameChange,
        genericName = formState.genericName,
        onGenericNameChange = viewModel::onGenericNameChange,
        medicineType = formState.medicineType,
        onMedicineTypeChange = viewModel::onTypeChange,
        strength = formState.strength,
        onStrengthChange = viewModel::onStrengthChange,
        unit = formState.unit,
        onUnitChange = viewModel::onUnitChange,
        manufacturer = formState.manufacturer,
        onManufacturerChange = viewModel::onManufacturerChange,
        notes = formState.notes,
        onNotesChange = viewModel::onNotesChange,
        isActive = formState.isActive,
        onActiveChange = viewModel::onActiveStatusChange,
        onSave = {
            viewModel.saveMedicine(medicineId) {
                onNavigateBack()
            }
        },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedicineContent(
    isEditMode: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    genericName: String,
    onGenericNameChange: (String) -> Unit,
    medicineType: String,
    onMedicineTypeChange: (String) -> Unit,
    strength: String,
    onStrengthChange: (String) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    manufacturer: String,
    onManufacturerChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val medicineTypes = listOf("Tablet", "Capsule", "Syrup", "Injection", "Drops", "Inhaler", "Cream", "Powder")
    val units = listOf("mg", "ml", "mcg", "Unit", "Tablet", "Capsule")

    var typeExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    Scaffold(
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
                value = name,
                onValueChange = onNameChange,
                label = { Text("Medicine Name *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = genericName,
                onValueChange = onGenericNameChange,
                label = { Text("Generic Name") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded }
            ) {
                OutlinedTextField(
                    value = medicineType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    medicineTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                onMedicineTypeChange(type)
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = strength,
                    onValueChange = onStrengthChange,
                    label = { Text("Strength") },
                    modifier = Modifier.weight(1f)
                )
                
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = !unitExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        units.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u) },
                                onClick = {
                                    onUnitChange(u)
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = manufacturer,
                onValueChange = onManufacturerChange,
                label = { Text("Manufacturer") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Active", fontWeight = FontWeight.SemiBold)
                Switch(checked = isActive, onCheckedChange = onActiveChange)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (isEditMode) "Update Medicine" else "Add Medicine")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicinePreview() {
    MediReminderTheme {
        AddEditMedicineContent(
            isEditMode = false,
            name = "",
            onNameChange = {},
            genericName = "",
            onGenericNameChange = {},
            medicineType = "Tablet",
            onMedicineTypeChange = {},
            strength = "",
            onStrengthChange = {},
            unit = "mg",
            onUnitChange = {},
            manufacturer = "",
            onManufacturerChange = {},
            notes = "",
            onNotesChange = {},
            isActive = true,
            onActiveChange = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}
