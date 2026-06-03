package com.example.medireminder.features.family.presentation.screen

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
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
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
import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.presentation.viewmodel.FamilyMemberViewModel
import com.example.medireminder.ui.theme.MediReminderTheme
import com.example.medireminder.ui.theme.PrimaryGreen
import com.example.medireminder.ui.theme.SecondaryGreen

private val avatarColors = listOf(
    PrimaryGreen, Color(0xFF1565C0), Color(0xFF6A1B9A),
    Color(0xFFE65100), Color(0xFFC62828), Color(0xFF2E7D32)
)

// ──────────────────────────────────────────────
// Screen Composable
// ──────────────────────────────────────────────

@Composable
fun FamilyMemberDetailsScreen(
    memberId: String,
    onNavigateBack: () -> Unit,
    onEditMember: (String) -> Unit,
    onAssignMedicine: (String) -> Unit = {},
    viewModel: FamilyMemberViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(memberId) {
        viewModel.loadMemberDetails(memberId)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    FamilyMemberDetailsContent(
        member = uiState.selectedMember,
        isLoading = uiState.isLoading,
        onNavigateBack = onNavigateBack,
        onEditMember = { onEditMember(memberId) },
        onDeleteMember = { showDeleteDialog = true },
        onToggleActive = { viewModel.toggleActiveStatus(memberId, uiState.selectedMember?.isActive ?: true) },
        onAssignMedicine = { onAssignMedicine(memberId) },
        snackbarHostState = snackbarHostState
    )

    if (showDeleteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Member") },
            text = { Text("Are you sure you want to delete ${uiState.selectedMember?.fullName}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        uiState.selectedMember?.let { member ->
                            viewModel.deleteMember(member) {
                                onNavigateBack()
                            }
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
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

// ──────────────────────────────────────────────
// Content Composable
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyMemberDetailsContent(
    member: FamilyMember?,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onEditMember: () -> Unit,
    onDeleteMember: () -> Unit,
    onToggleActive: () -> Unit,
    onAssignMedicine: () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Member Details", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditMember) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryGreen)
                    }
                    IconButton(onClick = onDeleteMember) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE53935))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (member != null) {
                val avatarColor = avatarColors[member.fullName.hashCode().mod(avatarColors.size).let { if (it < 0) it + avatarColors.size else it }]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // ═══ Profile Section ═══
                    ProfileSection(member = member, avatarColor = avatarColor)

                    // ═══ Status Card ═══
                    StatusCard(member = member, onToggleActive = onToggleActive)

                    // ═══ Stats Row ═══
                    StatsRow()

                    // ═══ Personal Information ═══
                    InfoCard(
                        title = "Personal Information",
                        icon = Icons.Default.Person
                    ) {
                        InfoRow(icon = Icons.Default.Face, label = "Gender", value = member.gender ?: "Not specified")
                        InfoRow(icon = Icons.Default.Call, label = "Phone", value = member.phone ?: "Not specified")
                        InfoRow(icon = Icons.Default.Bloodtype, label = "Blood Group", value = member.bloodGroup ?: "Not specified")
                    }

                    // ═══ Health Notes ═══
                    InfoCard(
                        title = "Health Notes",
                        icon = Icons.Default.Warning
                    ) {
                        Text(
                            text = member.notes ?: "No health notes available.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (member.notes != null) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    // ═══ Assigned Medicines ═══
                    InfoCard(
                        title = "Assigned Medicines",
                        icon = Icons.Default.Medication
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MedicineAssignedItem(
                                name = "Napa 500mg",
                                dosage = "1 tablet",
                                schedule = "8:00 AM, 8:00 PM",
                                status = "Active"
                            )
                            MedicineAssignedItem(
                                name = "Metformin 500mg",
                                dosage = "1 tablet",
                                schedule = "8:00 AM (After breakfast)",
                                status = "Active"
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = onAssignMedicine,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen
                                )
                            ) {
                                Icon(
                                    Icons.Default.Medication,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Assign Medicine", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else if (!isLoading) {
                Text(
                    text = "Member not found",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
// Profile Section
// ──────────────────────────────────────────────

@Composable
private fun ProfileSection(member: FamilyMember, avatarColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(avatarColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.fullName.firstOrNull()?.toString()?.uppercase() ?: "?",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = avatarColor
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Name
        Text(
            text = member.fullName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Relationship & Age
        Text(
            text = "${member.relationship ?: "Family Member"} • ${member.age ?: "—"} Years",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ──────────────────────────────────────────────
// Status Card
// ──────────────────────────────────────────────

@Composable
private fun StatusCard(member: FamilyMember, onToggleActive: () -> Unit) {
    val isActive = member.isActive
    val bgColor = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val statusColor = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828)
    val statusText = if (isActive) "Active" else "Inactive"
    val statusDesc = if (isActive) "Receiving reminders" else "Reminders disabled"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                Text(
                    text = statusText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = statusColor
                )
                Text(
                    text = statusDesc,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Stats Row
// ──────────────────────────────────────────────

@Composable
private fun StatsRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        StatCard(
            icon = Icons.Default.Notifications,
            label = "Schedule",
            value = "12 Reminders",
            color = PrimaryGreen,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.CheckCircle,
            label = "Compliance",
            value = "85%",
            color = Color(0xFF1565C0),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.Medication,
            label = "Medicines",
            value = "2 Active",
            color = Color(0xFF6A1B9A),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ──────────────────────────────────────────────
// Info Card
// ──────────────────────────────────────────────

@Composable
private fun InfoCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
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
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            content()
        }
    }
}

// ──────────────────────────────────────────────
// Info Row
// ──────────────────────────────────────────────

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PrimaryGreen.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ──────────────────────────────────────────────
// Medicine Assigned Item
// ──────────────────────────────────────────────

@Composable
private fun MedicineAssignedItem(
    name: String,
    dosage: String,
    schedule: String,
    status: String
) {
    val isActive = status == "Active"
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isActive) PrimaryGreen.copy(alpha = 0.1f) else Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Medication,
                contentDescription = null,
                tint = if (isActive) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = dosage, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = schedule, fontSize = 12.sp, color = PrimaryGreen)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isActive) Color(0xFFE8F5E9) else Color(0xFFF5F5F5))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isActive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ──────────────────────────────────────────────
// Previews
// ──────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun FamilyMemberDetailsPreview() {
    MediReminderTheme {
        FamilyMemberDetailsContent(
            member = FamilyMember(
                id = "1",
                fullName = "Dad",
                age = 60,
                gender = "Male",
                relationship = "Father",
                bloodGroup = "AB+",
                phone = "+1 555 123 4567",
                notes = "Has high blood pressure. Takes medication twice daily. Avoid salty foods.",
                isActive = true,
                createdAt = 0,
                updatedAt = 0
            ),
            isLoading = false,
            onNavigateBack = {},
            onEditMember = {},
            onDeleteMember = {},
            onToggleActive = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FamilyMemberDetailsInactivePreview() {
    MediReminderTheme {
        FamilyMemberDetailsContent(
            member = FamilyMember(
                id = "2",
                fullName = "Mom",
                age = 55,
                gender = "Female",
                relationship = "Mother",
                bloodGroup = "B+",
                phone = "+1 555 987 6543",
                notes = null,
                isActive = false,
                createdAt = 0,
                updatedAt = 0
            ),
            isLoading = false,
            onNavigateBack = {},
            onEditMember = {},
            onDeleteMember = {},
            onToggleActive = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
