package com.example.medireminder.features.medicine.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.presentation.components.MedicineCard
import com.example.medireminder.features.medicine.presentation.viewmodel.MedicineViewModel
import com.example.medireminder.ui.theme.MediReminderTheme

@Composable
fun MedicineListScreen(
    onAddMedicine: () -> Unit,
    onMedicineClick: (String) -> Unit,
    viewModel: MedicineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MedicineListContent(
        medicines = uiState.medicines,
        isLoading = uiState.isLoading,
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onAddMedicine = onAddMedicine,
        onMedicineClick = onMedicineClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineListContent(
    medicines: List<Medicine>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddMedicine: () -> Unit,
    onMedicineClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Medicines", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMedicine) {
                Icon(Icons.Default.Add, contentDescription = "Add Medicine")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by name or generic name") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (medicines.isEmpty() && !isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isBlank()) "No medicines added yet" else "No matching medicines found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(medicines) { medicine ->
                            MedicineCard(
                                medicine = medicine,
                                onClick = { onMedicineClick(medicine.id) }
                            )
                        }
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicineListPreview() {
    MediReminderTheme {
        MedicineListContent(
            medicines = listOf(
                Medicine(
                    id = "1",
                    name = "Napa",
                    genericName = "Paracetamol",
                    medicineType = "Tablet",
                    strength = "500mg",
                    unit = "Tablet",
                    isActive = true,
                    createdAt = 0,
                    updatedAt = 0,
                    manufacturer = "Beximco",
                    notes = ""
                )
            ),
            isLoading = false,
            searchQuery = "",
            onSearchQueryChange = {},
            onAddMedicine = {},
            onMedicineClick = {}
        )
    }
}
