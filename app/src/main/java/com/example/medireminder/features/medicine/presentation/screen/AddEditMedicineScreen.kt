package com.example.medireminder.features.medicine.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import com.example.medireminder.features.medicine.presentation.state.MedicineFormState
import com.example.medireminder.features.medicine.presentation.viewmodel.MedicineViewModel
import com.example.medireminder.ui.theme.MediReminderTheme
import com.example.medireminder.ui.theme.PrimaryGreen

// ──────────────────────────────────────────────
// Screen Composable
// ──────────────────────────────────────────────

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

// ──────────────────────────────────────────────
// Content Composable
// ──────────────────────────────────────────────

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
                title = {
                    Text(
                        text = if (isEditMode) "Edit Medicine" else "Add Medicine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            // Medicine Icon Avatar
            MedicineAvatarSection(name = formState.name, isEditMode = isEditMode)

            // Basic Information
            FormSectionHeader(icon = Icons.Default.Medication, title = "Basic Information")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FormTextField(
                        value = formState.name,
                        onValueChange = onNameChange,
                        label = "Medicine Name",
                        placeholder = "e.g. Napa",
                        isRequired = true,
                        leadingIcon = Icons.Default.Medication
                    )
                    FormTextField(
                        value = formState.genericName,
                        onValueChange = onGenericNameChange,
                        label = "Generic Name",
                        placeholder = "e.g. Paracetamol",
                        leadingIcon = Icons.Default.Science
                    )
                }
            }

            // Type & Strength
            FormSectionHeader(icon = Icons.Default.Category, title = "Type & Strength")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MedicineTypeDropdown(
                            selectedType = formState.medicineType,
                            onTypeSelected = onTypeChange,
                            options = medicineTypes,
                            modifier = Modifier.weight(1f)
                        )
                        FormTextField(
                            value = formState.strength,
                            onValueChange = onStrengthChange,
                            label = "Strength",
                            placeholder = "e.g. 500mg",
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Default.Straighten
                        )
                    }

                    MedicineUnitDropdown(
                        selectedUnit = formState.unit,
                        onUnitSelected = onUnitChange,
                        options = units,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Additional Info
            FormSectionHeader(icon = Icons.Default.Business, title = "Additional Info")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FormTextField(
                        value = formState.manufacturer,
                        onValueChange = onManufacturerChange,
                        label = "Manufacturer",
                        placeholder = "e.g. Beximco",
                        leadingIcon = Icons.Default.Business
                    )

                    FormTextField(
                        value = formState.notes,
                        onValueChange = onNotesChange,
                        label = "Notes",
                        placeholder = "Any additional notes...",
                        minLines = 3,
                        singleLine = false,
                        leadingIcon = Icons.Default.Description
                    )
                }
            }

            // Active Status (edit mode only)
            if (isEditMode) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Active Status",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Toggle to enable or disable this medicine",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = formState.isActive,
                            onCheckedChange = onActiveStatusChange
                        )
                    }
                }
            }

            // Save Button
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text(
                    text = if (isEditMode) "Update Medicine" else "Save Medicine",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ──────────────────────────────────────────────
// Medicine Avatar Section
// ──────────────────────────────────────────────

@Composable
private fun MedicineAvatarSection(name: String, isEditMode: Boolean) {
    val initial = name.firstOrNull()?.toString()?.uppercase() ?: "💊"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(PrimaryGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Medication,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isEditMode) "Edit Medicine" else "New Medicine",
            fontSize = 13.sp,
            color = PrimaryGreen,
            fontWeight = FontWeight.Medium
        )
    }
}

// ──────────────────────────────────────────────
// Medicine Type Dropdown
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicineTypeDropdown(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Type *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Select type", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(Icons.Default.Category, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.7f))
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                cursorColor = PrimaryGreen
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontWeight = if (option == selectedType) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onTypeSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Medicine Unit Dropdown
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicineUnitDropdown(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedUnit,
            onValueChange = {},
            readOnly = true,
            label = { Text("Unit *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Select unit", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(Icons.Default.Straighten, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.7f))
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                cursorColor = PrimaryGreen
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontWeight = if (option == selectedUnit) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onUnitSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Form Section Header
// ──────────────────────────────────────────────

@Composable
private fun FormSectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
    isRequired: Boolean = false,
    leadingIcon: ImageVector? = null,
    minLines: Int = 1,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = if (isRequired) "$label *" else label,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) }
        } else null,
        leadingIcon = leadingIcon?.let {
            { Icon(it, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.7f)) }
        },
        modifier = modifier.fillMaxWidth(),
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
// Previews
// ──────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun AddMedicinePreview() {
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

@Preview(showBackground = true)
@Composable
private fun EditMedicinePreview() {
    MediReminderTheme {
        AddEditMedicineContent(
            isEditMode = true,
            formState = MedicineFormState(
                name = "Napa",
                genericName = "Paracetamol",
                medicineType = "Tablet",
                strength = "500mg",
                unit = "Tablet",
                manufacturer = "Beximco",
                notes = "Take after food with water",
                isActive = true
            ),
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
