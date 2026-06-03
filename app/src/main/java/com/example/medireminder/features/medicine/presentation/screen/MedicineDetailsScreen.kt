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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.presentation.viewmodel.MedicineViewModel
import com.example.medireminder.ui.theme.MediReminderTheme
import com.example.medireminder.ui.theme.PrimaryGreen
import com.example.medireminder.ui.theme.SecondaryGreen

// ──────────────────────────────────────────────
// Screen Composable
// ──────────────────────────────────────────────

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
                            viewModel.deleteMedicine(medicine) { onNavigateBack() }
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Deactivate") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ──────────────────────────────────────────────
// Content Composable
// ──────────────────────────────────────────────

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
                title = { Text("Medicine Details", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditMedicine) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryGreen)
                    }
                    IconButton(onClick = onDeleteMedicine) {
                        Icon(Icons.Default.Delete, contentDescription = "Deactivate", tint = Color(0xFFE53935))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (medicine != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // ═══ Profile Section ═══
                    MedicineProfileSection(medicine = medicine)

                    // ═══ Status Card ═══
                    MedicineStatusCard(medicine = medicine, onToggleActive = onToggleActive)

                    // ═══ Stock Overview Stats ═══
                    MedicineStatsRow()

                    // ═══ Medicine Information ═══
                    DetailInfoCard(title = "Medicine Information", icon = Icons.Default.Medication) {
                        DetailInfoRow(icon = Icons.Default.Category, label = "Type", value = medicine.medicineType)
                        DetailInfoRow(icon = Icons.Default.Straighten, label = "Strength", value = medicine.strength ?: "Not specified")
                        DetailInfoRow(icon = Icons.Default.Inventory2, label = "Unit", value = medicine.unit)
                        DetailInfoRow(icon = Icons.Default.Business, label = "Manufacturer", value = medicine.manufacturer ?: "Not specified")
                    }

                    // ═══ Notes ═══
                    DetailInfoCard(title = "Notes", icon = Icons.Default.Description) {
                        Text(
                            text = medicine.notes ?: "No notes available.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (medicine.notes != null) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    // ═══ Used By Family Members ═══
                    DetailInfoCard(title = "Used By Family Members", icon = Icons.Default.Person) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            UsedByMemberItem(name = "Dad", dosage = "1 tablet", schedule = "8:00 AM, 8:00 PM")
                            UsedByMemberItem(name = "Mom", dosage = "1 tablet", schedule = "8:00 AM")
                        }
                    }

                    // ═══ Reminder Schedules ═══
                    DetailInfoCard(title = "Reminder Schedules", icon = Icons.Default.Notifications) {
                        Text(
                            text = "2 active reminder schedules",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else if (!isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Medicine not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryGreen
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Medicine Profile Section
// ──────────────────────────────────────────────

@Composable
private fun MedicineProfileSection(medicine: Medicine) {
    val isActive = medicine.isActive
    val color = if (isActive) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(if (isActive) PrimaryGreen.copy(alpha = 0.1f) else Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Medication,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))

        val displayName = if (medicine.strength != null) "${medicine.name} ${medicine.strength}" else medicine.name
        Text(
            text = displayName,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        if (!medicine.genericName.isNullOrBlank()) {
            Text(
                text = medicine.genericName,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "${medicine.medicineType}",
            fontSize = 13.sp,
            color = PrimaryGreen
        )
    }
}

// ──────────────────────────────────────────────
// Status Card
// ──────────────────────────────────────────────

@Composable
private fun MedicineStatusCard(medicine: Medicine, onToggleActive: () -> Unit) {
    val isActive = medicine.isActive
    val bgColor = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val statusColor = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828)
    val statusText = if (isActive) "Active" else "Inactive"
    val statusDesc = if (isActive) "Available for reminders" else "Disabled"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = statusText, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = statusColor)
                Text(text = statusDesc, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = onToggleActive,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) Color(0xFFE53935) else PrimaryGreen
                )
            ) {
                Text(
                    text = if (isActive) "Deactivate" else "Activate",
                    color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Stats Row
// ──────────────────────────────────────────────

@Composable
private fun MedicineStatsRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        MiniStatCard(icon = Icons.Default.Inventory2, label = "In Stock", value = "45 ${"pills"}", color = PrimaryGreen, modifier = Modifier.weight(1f))
        MiniStatCard(icon = Icons.Default.Groups, label = "Members", value = "2", color = Color(0xFF6A1B9A), modifier = Modifier.weight(1f))
        MiniStatCard(icon = Icons.Default.Notifications, label = "Reminders", value = "2 Active", color = Color(0xFF1565C0), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MiniStatCard(icon: ImageVector, label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(34.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ──────────────────────────────────────────────
// Detail Info Card
// ──────────────────────────────────────────────

@Composable
private fun DetailInfoCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            content()
        }
    }
}

// ──────────────────────────────────────────────
// Detail Info Row
// ──────────────────────────────────────────────

@Composable
private fun DetailInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryGreen.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

// ──────────────────────────────────────────────
// Used By Member Item
// ──────────────────────────────────────────────

@Composable
private fun UsedByMemberItem(name: String, dosage: String, schedule: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = dosage, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = schedule, fontSize = 12.sp, color = PrimaryGreen)
        }
    }
}

// ──────────────────────────────────────────────
// Preview
// ──────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun MedicineDetailsPreview() {
    MediReminderTheme {
        MedicineDetailsContent(
            medicine = Medicine(
                id = "1", name = "Napa", genericName = "Paracetamol",
                medicineType = "Tablet", strength = "500mg", unit = "Tablet",
                isActive = true, createdAt = 0, updatedAt = 0,
                manufacturer = "Beximco", notes = "Take after food with water. Do not exceed 8 tablets in 24 hours."
            ),
            isLoading = false, onNavigateBack = {}, onEditMedicine = {},
            onDeleteMedicine = {}, onToggleActive = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}