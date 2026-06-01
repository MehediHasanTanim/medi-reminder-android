package com.example.medireminder.features.medicine.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.presentation.viewmodel.MedicineViewModel
import com.example.medireminder.ui.theme.MediReminderTheme

@Composable
fun MedicineDetailsScreen(
    medicineId: String,
    onNavigateBack: () -> Unit,
    onEditMedicine: (String) -> Unit,
    viewModel: MedicineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(medicineId) {
        viewModel.loadMedicineDetails(medicineId)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    MedicineDetailsContent(
        medicine = uiState.selectedMedicine,
        isLoading = uiState.isLoading,
        onNavigateBack = onNavigateBack,
        onEditMedicine = { onEditMedicine(medicineId) },
        onDeleteMedicine = { showDeleteDialog = true },
        onToggleActive = { viewModel.toggleActiveStatus(medicineId, uiState.selectedMedicine?.isActive ?: true) },
        snackbarHostState = snackbarHostState
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Deactivate Medicine") },
            text = { Text("Are you sure you want to deactivate ${uiState.selectedMedicine?.name}? It will no longer be available for new assignments.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        uiState.selectedMedicine?.let { medicine ->
                            viewModel.deleteMedicine(medicine) {
                                onNavigateBack()
                            }
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Deactivate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailsContent(
    medicine: Medicine?,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onEditMedicine: () -> Unit,
    onDeleteMedicine: () -> Unit,
    onToggleActive: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Medicine Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditMedicine) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDeleteMedicine) {
                        Icon(Icons.Default.Delete, contentDescription = "Deactivate", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (medicine != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Section
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                if (medicine.isActive) MaterialTheme.colorScheme.primaryContainer 
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = medicine.name.firstOrNull()?.toString() ?: "",
                            style = MaterialTheme.typography.displaySmall,
                            color = if (medicine.isActive) MaterialTheme.colorScheme.onPrimaryContainer 
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(text = medicine.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    if (!medicine.genericName.isNullOrBlank()) {
                        Text(
                            text = medicine.genericName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Status and Toggle
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (medicine.isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (medicine.isActive) "Active" else "Inactive",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (medicine.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = if (medicine.isActive) "Available for reminders" else "Disabled",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Button(
                                onClick = onToggleActive,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (medicine.isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(if (medicine.isActive) "Deactivate" else "Activate")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info Sections
                    MedicineInfoCard(title = "Medicine Information") {
                        MedicineInfoRow(label = "Type", value = medicine.medicineType)
                        MedicineInfoRow(label = "Strength", value = medicine.strength ?: "Not specified")
                        MedicineInfoRow(label = "Unit", value = medicine.unit)
                        MedicineInfoRow(label = "Manufacturer", value = medicine.manufacturer ?: "Not specified")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    MedicineInfoCard(title = "Notes") {
                        Text(
                            text = medicine.notes ?: "No notes available.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Placeholder Sections
                    MedicineInfoCard(title = "Stock Information") {
                        Text("No stock information available.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    MedicineInfoCard(title = "Used By Family Members") {
                        Text("No family members assigned yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    MedicineInfoCard(title = "Reminder Schedules") {
                        Text("No reminder schedules found.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else if (!isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Medicine not found")
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun MedicineInfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}

@Composable
fun MedicineInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Preview(showBackground = true)
@Composable
fun MedicineDetailsPreview() {
    MediReminderTheme {
        MedicineDetailsContent(
            medicine = Medicine(
                id = "1",
                name = "Napa",
                genericName = "Paracetamol",
                medicineType = "Tablet",
                strength = "500mg",
                unit = "Tablet",
                manufacturer = "Beximco",
                notes = "Common painkiller.",
                isActive = true,
                createdAt = 0,
                updatedAt = 0
            ),
            isLoading = false,
            onNavigateBack = {},
            onEditMedicine = {},
            onDeleteMedicine = {},
            onToggleActive = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
