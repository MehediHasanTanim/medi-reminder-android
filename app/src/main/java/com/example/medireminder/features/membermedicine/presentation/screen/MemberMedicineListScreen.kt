package com.example.medireminder.features.membermedicine.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.membermedicine.presentation.components.MemberMedicineCard
import com.example.medireminder.features.membermedicine.presentation.viewmodel.MemberMedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberMedicineListScreen(
    onAddAssignment: () -> Unit,
    onAssignmentClick: (String) -> Unit,
    viewModel: MemberMedicineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Assignments") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAssignment) {
                Icon(Icons.Default.Add, contentDescription = "Assign Medicine")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.assignments.isEmpty()) {
                Text(
                    text = "No medicines assigned yet",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.assignments) { assignment ->
                        MemberMedicineCard(
                            assignment = assignment,
                            onClick = { onAssignmentClick(assignment.id) }
                        )
                    }
                }
            }
        }
    }
}
