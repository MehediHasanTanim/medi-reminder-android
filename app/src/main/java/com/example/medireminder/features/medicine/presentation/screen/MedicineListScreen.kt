package com.example.medireminder.features.medicine.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.presentation.components.MedicineCard
import com.example.medireminder.features.medicine.presentation.viewmodel.MedicineViewModel
import com.example.medireminder.ui.theme.MediReminderTheme
import com.example.medireminder.ui.theme.PrimaryGreen

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
                title = {
                    Text("Medicines", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMedicine,
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Medicine")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                placeholder = {
                    Text(
                        "Search medicines...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = PrimaryGreen.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    cursorColor = PrimaryGreen
                )
            )

            // Count chip
            if (medicines.isNotEmpty()) {
                Text(
                    text = "${medicines.size} medicines found",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (medicines.isEmpty() && !isLoading) {
                    EmptyMedicineState(
                        searchQuery = searchQuery,
                        onAddMedicine = onAddMedicine
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp, end = 20.dp, top = 8.dp, bottom = 88.dp
                        ),
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMedicineState(
    searchQuery: String,
    onAddMedicine: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (searchQuery.isBlank()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No medicines added yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add your first medicine to start\ntracking your inventory",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "No matching medicines found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Try a different search term",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                    id = "1", name = "Napa", genericName = "Paracetamol",
                    medicineType = "Tablet", strength = "500mg", unit = "Tablet",
                    isActive = true, createdAt = 0, updatedAt = 0,
                    manufacturer = "Beximco", notes = ""
                ),
                Medicine(
                    id = "2", name = "Metformin", genericName = "Metformin HCl",
                    medicineType = "Tablet", strength = "500mg", unit = "Tablet",
                    isActive = true, createdAt = 0, updatedAt = 0,
                    manufacturer = "Square", notes = ""
                ),
                Medicine(
                    id = "3", name = "Azithromycin", genericName = "Azithromycin",
                    medicineType = "Tablet", strength = "250mg", unit = "Tablet",
                    isActive = true, createdAt = 0, updatedAt = 0,
                    manufacturer = "ACI", notes = ""
                ),
                Medicine(
                    id = "4", name = "Calcium + D3", genericName = "Calcium Carbonate + Vitamin D3",
                    medicineType = "Tablet", strength = null, unit = "Tablet",
                    isActive = false, createdAt = 0, updatedAt = 0,
                    manufacturer = "Incepta", notes = ""
                ),
                Medicine(
                    id = "5", name = "Vitamin C", genericName = "Ascorbic Acid",
                    medicineType = "Tablet", strength = "500mg", unit = "Tablet",
                    isActive = true, createdAt = 0, updatedAt = 0,
                    manufacturer = "Opsonin", notes = ""
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
