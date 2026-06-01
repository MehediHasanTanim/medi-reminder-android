package com.example.medireminder.features.family.presentation.screen

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
import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.presentation.viewmodel.FamilyMemberViewModel
import com.example.medireminder.ui.theme.MediReminderTheme

@Composable
fun FamilyMemberDetailsScreen(
    memberId: String,
    onNavigateBack: () -> Unit,
    onEditMember: (String) -> Unit,
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
        snackbarHostState = snackbarHostState
    )

    if (showDeleteDialog) {
        AlertDialog(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyMemberDetailsContent(
    member: FamilyMember?,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onEditMember: () -> Unit,
    onDeleteMember: () -> Unit,
    onToggleActive: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Member Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditMember) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDeleteMember) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (member != null) {
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
                                if (member.isActive) MaterialTheme.colorScheme.primaryContainer 
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.fullName.firstOrNull()?.toString() ?: "",
                            style = MaterialTheme.typography.displaySmall,
                            color = if (member.isActive) MaterialTheme.colorScheme.onPrimaryContainer 
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(text = member.fullName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${member.relationship ?: "Family Member"} • ${member.age ?: "?"} Years",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Status and Toggle
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (member.isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
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
                                    text = if (member.isActive) "Active" else "Inactive",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (member.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = if (member.isActive) "Receiving reminders" else "Reminders disabled",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Button(
                                onClick = onToggleActive,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (member.isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(if (member.isActive) "Deactivate" else "Activate")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info Sections
                    DetailCard(title = "Personal Information") {
                        DetailRow(label = "Blood Group", value = member.bloodGroup ?: "Not specified")
                        DetailRow(label = "Phone", value = member.phone ?: "Not specified")
                        DetailRow(label = "Gender", value = member.gender ?: "Not specified")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailCard(title = "Health Notes") {
                        Text(
                            text = member.notes ?: "No health notes available.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DetailCard(title = "Assigned Medicines") {
                        Text(
                            text = "No medicines assigned yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (!isLoading) {
                Text("Member not found", modifier = Modifier.align(Alignment.Center))
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun DetailCard(title: String, content: @Composable ColumnScope.() -> Unit) {
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
fun DetailRow(label: String, value: String) {
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
fun FamilyMemberDetailsPreview() {
    MediReminderTheme {
        FamilyMemberDetailsContent(
            member = FamilyMember(
                id = "1",
                fullName = "John Doe",
                age = 30,
                gender = "Male",
                relationship = "Self",
                bloodGroup = "O+",
                phone = "123456789",
                notes = "Diabetic patient.",
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
