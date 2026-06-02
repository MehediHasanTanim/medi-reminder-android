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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.medireminder.features.family.presentation.state.FamilyMemberFormState
import com.example.medireminder.features.family.presentation.viewmodel.FamilyMemberViewModel
import com.example.medireminder.ui.theme.MediReminderTheme
import com.example.medireminder.ui.theme.PrimaryGreen

// ──────────────────────────────────────────────
// Screen Composable
// ──────────────────────────────────────────────

@Composable
fun AddEditFamilyMemberScreen(
    memberId: String?,
    onNavigateBack: () -> Unit,
    viewModel: FamilyMemberViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val isEditMode = memberId != null

    LaunchedEffect(memberId) {
        if (isEditMode && memberId != null) {
            viewModel.loadMemberDetails(memberId)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    AddEditFamilyMemberContent(
        isEditMode = isEditMode,
        formState = formState,
        onFullNameChange = viewModel::onFullNameChange,
        onAgeChange = viewModel::onAgeChange,
        onGenderChange = viewModel::onGenderChange,
        onRelationshipChange = viewModel::onRelationshipChange,
        onBloodGroupChange = viewModel::onBloodGroupChange,
        onPhoneChange = viewModel::onPhoneChange,
        onNotesChange = viewModel::onNotesChange,
        onActiveStatusChange = viewModel::onActiveStatusChange,
        onSave = {
            viewModel.saveMember(memberId) {
                onNavigateBack()
            }
        },
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState
    )
}

// ──────────────────────────────────────────────
// Content Composable
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFamilyMemberContent(
    isEditMode: Boolean,
    formState: FamilyMemberFormState,
    onFullNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onRelationshipChange: (String) -> Unit,
    onBloodGroupChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onActiveStatusChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Member" else "Add Family Member",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
            // Profile Avatar
            ProfileAvatarSection(
                fullName = formState.fullName,
                isEditMode = isEditMode
            )

            // Personal Information Section
            FormSectionHeader(icon = Icons.Default.Person, title = "Personal Information")
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
                        value = formState.fullName,
                        onValueChange = onFullNameChange,
                        label = "Full Name",
                        placeholder = "Enter full name",
                        isRequired = true,
                        isError = formState.fullNameError != null,
                        errorMessage = formState.fullNameError,
                        leadingIcon = Icons.Default.Person
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FormTextField(
                            value = formState.age,
                            onValueChange = onAgeChange,
                            label = "Age",
                            placeholder = "Age",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            isError = formState.ageError != null,
                            errorMessage = formState.ageError,
                            leadingIcon = Icons.Default.Cake
                        )
                        GenderDropdown(
                            selectedGender = formState.gender,
                            onGenderSelected = onGenderChange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Family & Contact Section
            FormSectionHeader(icon = Icons.Default.Groups, title = "Family & Contact")
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
                        FormTextField(
                            value = formState.relationship,
                            onValueChange = onRelationshipChange,
                            label = "Relationship",
                            placeholder = "e.g. Father",
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Default.Groups
                        )
                        FormTextField(
                            value = formState.bloodGroup,
                            onValueChange = onBloodGroupChange,
                            label = "Blood Group",
                            placeholder = "e.g. O+",
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Default.Bloodtype
                        )
                    }

                    FormTextField(
                        value = formState.phone,
                        onValueChange = onPhoneChange,
                        label = "Phone Number",
                        placeholder = "Enter phone number",
                        keyboardType = KeyboardType.Phone,
                        leadingIcon = Icons.Default.Call
                    )
                }
            }

            // Notes Section
            FormSectionHeader(icon = Icons.Default.Person, title = "Additional Notes")
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
                        value = formState.notes,
                        onValueChange = onNotesChange,
                        label = "Notes",
                        placeholder = "Any medical notes or allergies...",
                        minLines = 3,
                        singleLine = false
                    )
                }
            }

            // Active Status toggle (edit mode only)
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
                                text = "Toggle to enable or disable this member",
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
                    text = if (isEditMode) "Update Member" else "Save Member",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ──────────────────────────────────────────────
// Profile Avatar Section
// ──────────────────────────────────────────────

@Composable
private fun ProfileAvatarSection(
    fullName: String,
    isEditMode: Boolean
) {
    val initial = fullName.firstOrNull()?.toString()?.uppercase() ?: "?"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(PrimaryGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isEditMode) "Edit Profile Picture" else "Add Profile Picture",
            fontSize = 13.sp,
            color = PrimaryGreen,
            fontWeight = FontWeight.Medium
        )
    }
}

// ──────────────────────────────────────────────
// Gender Dropdown
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenderDropdown(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val genders = listOf("Male", "Female", "Other")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = {},
            readOnly = true,
            label = { Text("Gender", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Select gender", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Face,
                    contentDescription = null,
                    tint = PrimaryGreen.copy(alpha = 0.7f)
                )
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
            genders.forEach { gender ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Face,
                                contentDescription = null,
                                tint = if (gender == selectedGender) PrimaryGreen else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = gender,
                                fontWeight = if (gender == selectedGender) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    },
                    onClick = {
                        onGenderSelected(gender)
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
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
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
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) }
        } else null,
        leadingIcon = leadingIcon?.let {
            { Icon(it, contentDescription = null, tint = if (isError) MaterialTheme.colorScheme.error else PrimaryGreen.copy(alpha = 0.7f)) }
        },
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        supportingText = errorMessage?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        minLines = minLines,
        singleLine = singleLine,
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
private fun AddFamilyMemberPreview() {
    MediReminderTheme {
        AddEditFamilyMemberContent(
            isEditMode = false,
            formState = FamilyMemberFormState(),
            onFullNameChange = {},
            onAgeChange = {},
            onGenderChange = {},
            onRelationshipChange = {},
            onBloodGroupChange = {},
            onPhoneChange = {},
            onNotesChange = {},
            onActiveStatusChange = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditFamilyMemberPreview() {
    MediReminderTheme {
        AddEditFamilyMemberContent(
            isEditMode = true,
            formState = FamilyMemberFormState(
                fullName = "John Doe",
                age = "30",
                gender = "Male",
                relationship = "Self (You)",
                bloodGroup = "O+",
                phone = "+1 234 567 890",
                notes = "No known allergies",
                isActive = true
            ),
            onFullNameChange = {},
            onAgeChange = {},
            onGenderChange = {},
            onRelationshipChange = {},
            onBloodGroupChange = {},
            onPhoneChange = {},
            onNotesChange = {},
            onActiveStatusChange = {},
            onSave = {},
            onNavigateBack = {}
        )
    }
}
