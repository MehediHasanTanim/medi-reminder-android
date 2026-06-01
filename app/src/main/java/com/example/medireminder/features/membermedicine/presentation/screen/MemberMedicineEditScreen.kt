package com.example.medireminder.features.membermedicine.presentation.screen

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.membermedicine.presentation.viewmodel.MemberMedicineViewModel

@Composable
fun MemberMedicineEditScreen(
    assignmentId: String,
    onNavigateBack: () -> Unit,
    viewModel: MemberMedicineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(assignmentId) {
        viewModel.loadAssignmentDetails(assignmentId)
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            onNavigateBack()
            viewModel.clearMessages()
        }
    }

    AssignMedicineContent(
        title = "Edit Assignment",
        formState = formState,
        familyMembers = uiState.familyMembers,
        medicines = uiState.medicines,
        onFamilyMemberChange = viewModel::onFamilyMemberChange,
        onMedicineChange = viewModel::onMedicineChange,
        onDosageChange = viewModel::onDosageQuantityChange,
        onFrequencyChange = viewModel::onFrequencyChange,
        onStartDateChange = viewModel::onStartDateChange,
        onEndDateChange = viewModel::onEndDateChange,
        onInstructionsChange = viewModel::onInstructionsChange,
        onAutoReduceStockChange = viewModel::onAutoReduceStockChange,
        onSave = { viewModel.saveAssignment(assignmentId, onSuccess = onNavigateBack) },
        onNavigateBack = onNavigateBack
    )
}
