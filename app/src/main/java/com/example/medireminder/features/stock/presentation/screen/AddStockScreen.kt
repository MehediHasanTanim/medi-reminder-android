package com.example.medireminder.features.stock.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.medicine.presentation.viewmodel.MedicineViewModel
import com.example.medireminder.features.settings.presentation.viewmodel.SettingsViewModel
import com.example.medireminder.features.stock.presentation.viewmodel.MedicineStockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockScreen(
    onNavigateBack: () -> Unit,
    stockViewModel: MedicineStockViewModel = hiltViewModel(),
    medicineViewModel: MedicineViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val medicineState by medicineViewModel.uiState.collectAsState()
    val stockUiState by stockViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val defaultThreshold = settingsState.settings.defaultLowStockThreshold

    var selectedMedicineId by remember { mutableStateOf("") }
    var initialQuantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var threshold by remember { mutableStateOf(defaultThreshold.toString()) }
    var autoReduce by remember { mutableStateOf(true) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(defaultThreshold) {
        if (threshold.isBlank() || threshold == "5" || threshold == "5.0") {
            threshold = defaultThreshold.toString()
        }
    }

    LaunchedEffect(stockUiState.successMessage) {
        stockUiState.successMessage?.let {
            onNavigateBack()
            stockViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medicine Stock") },
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
            Text("Initialize stock for a medicine", style = MaterialTheme.typography.bodyMedium)

            // Medicine Selection
            var expanded by remember { mutableStateOf(false) }
            val medicines = medicineState.medicines
            val selectedMedicine = medicines.find { it.id == selectedMedicineId }

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
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    medicines.forEach { medicine ->
                        DropdownMenuItem(
                            text = { Text(medicine.name) },
                            onClick = {
                                selectedMedicineId = medicine.id
                                unit = medicine.unit
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = initialQuantity,
                onValueChange = { initialQuantity = it },
                label = { Text("Initial Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text(unit) }
            )

            OutlinedTextField(
                value = threshold,
                onValueChange = { threshold = it },
                label = { Text("Low Stock Threshold") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                supportingText = { Text("Notify me when stock falls below this") }
            )

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = autoReduce, onCheckedChange = { autoReduce = it })
                Text("Enable auto stock reduction")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val qty = initialQuantity.toDoubleOrNull() ?: 0.0
                    val thr = threshold.toDoubleOrNull() ?: 5.0
                    validationError = when {
                        selectedMedicineId.isBlank() -> "Select a medicine"
                        unit.isBlank() -> "Medicine unit is required"
                        initialQuantity.toDoubleOrNull() == null -> "Enter a valid quantity"
                        threshold.toDoubleOrNull() == null -> "Enter a valid threshold"
                        qty < 0 -> "Initial quantity cannot be negative"
                        thr < 0 -> "Threshold cannot be negative"
                        else -> null
                    }
                    if (validationError == null) {
                        stockViewModel.addStock(
                            medicineId = selectedMedicineId,
                            initialQuantity = qty,
                            unit = unit,
                            lowStockThreshold = thr,
                            expiryDate = null,
                            autoReduceEnabled = autoReduce,
                            onSuccess = {}
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedMedicineId.isNotBlank()
            ) {
                Text("Save Stock Configuration")
            }

            validationError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            
            if (stockUiState.errorMessage != null) {
                Text(stockUiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
