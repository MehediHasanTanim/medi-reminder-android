package com.example.medireminder.features.family.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.family.presentation.state.FamilyMemberFormState
import com.example.medireminder.features.family.presentation.viewmodel.FamilyMemberViewModel
import com.example.medireminder.ui.theme.MediReminderTheme

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
                title = { Text(if (isEditMode) "Edit Member" else "Add Family Member", fontWeight = FontWeight.Bold) },
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
                value = formState.fullName,
                onValueChange = onFullNameChange,
                label = { Text("Full Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.fullNameError != null,
                supportingText = formState.fullNameError?.let { { Text(it) } }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = formState.age,
                    onValueChange = onAgeChange,
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = formState.ageError != null,
                    supportingText = formState.ageError?.let { { Text(it) } }
                )
                OutlinedTextField(
                    value = formState.gender,
                    onValueChange = onGenderChange,
                    label = { Text("Gender") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = formState.relationship,
                    onValueChange = onRelationshipChange,
                    label = { Text("Relationship") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = formState.bloodGroup,
                    onValueChange = onBloodGroupChange,
                    label = { Text("Blood Group") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = formState.phone,
                onValueChange = onPhoneChange,
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
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
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
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
                Text(if (isEditMode) "Update Member" else "Add Member")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddFamilyMemberPreview() {
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
