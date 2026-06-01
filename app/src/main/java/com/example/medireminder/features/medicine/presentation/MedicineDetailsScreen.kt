package com.example.medireminder.features.medicine.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    val medicine = uiState.selectedMedicine

    LaunchedEffect(medicineId) {
        viewModel.loadMedicineDetails(medicineId)
    }

    MedicineDetailsContent(
        medicine = medicine,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onNavigateBack = onNavigateBack,
        onEditClick = { onEditMedicine(medicineId) },
        onDeleteClick = { med ->
            viewModel.deleteMedicine(med) {
                onNavigateBack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailsContent(
    medicine: Medicine?,
    isLoading: Boolean,
    errorMessage: String?,
    onNavigateBack: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: (Medicine) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { 
                        medicine?.let { onDeleteClick(it) }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
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
                    // Header
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (medicine.medicineType == "Syrup") "🧴" else "💊",
                            fontSize = 40.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(text = medicine.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = medicine.medicineType,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MedicineActionItem(icon = Icons.Default.Edit, label = "Edit", onClick = onEditClick)
                        MedicineActionItem(icon = Icons.Default.Info, label = "Stock", onClick = { /* Navigate to stock */ })
                        MedicineActionItem(icon = Icons.Default.Delete, label = "Delete", color = MaterialTheme.colorScheme.error, onClick = {
                            onDeleteClick(medicine)
                        })
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Info Section
                    InfoSection(title = "Medicine Information") {
                        InfoRow(label = "Generic Name", value = medicine.genericName ?: "N/A")
                        InfoRow(label = "Strength", value = medicine.strength ?: "N/A")
                        InfoRow(label = "Unit", value = medicine.unit)
                        InfoRow(label = "Manufacturer", value = medicine.manufacturer ?: "N/A")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoSection(title = "Notes") {
                        Text(
                            text = medicine.notes ?: "No notes available.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            } else if (!isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "Medicine not found")
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun MedicineActionItem(icon: ImageVector, label: String, color: Color = MaterialTheme.colorScheme.primary, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color)
        }
        Text(label, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun InfoSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
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
                name = "Paracetamol",
                genericName = "Acetaminophen",
                medicineType = "Tablet",
                strength = "500",
                unit = "mg",
                manufacturer = "Beximco Pharma",
                notes = "Take after meal",
                isActive = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            isLoading = false,
            errorMessage = null,
            onNavigateBack = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
