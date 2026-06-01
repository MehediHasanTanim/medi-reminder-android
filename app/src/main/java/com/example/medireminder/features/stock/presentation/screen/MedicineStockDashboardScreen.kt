package com.example.medireminder.features.stock.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.stock.presentation.components.StockCard
import com.example.medireminder.features.stock.presentation.viewmodel.MedicineStockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineStockDashboardScreen(
    onAddStock: () -> Unit,
    onStockClick: (String) -> Unit,
    onViewLowStock: () -> Unit,
    viewModel: MedicineStockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Stock") },
                actions = {
                    if (uiState.lowStockMedicines.isNotEmpty()) {
                        IconButton(onClick = onViewLowStock) {
                            BadgedBox(
                                badge = { Badge { Text("${uiState.lowStockMedicines.size}") } }
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = "Low Stock")
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddStock) {
                Icon(Icons.Default.Add, contentDescription = "Add Stock")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.stocks.isEmpty()) {
                Text(
                    text = "No stock records found",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.stocks) { stock ->
                        StockCard(
                            stock = stock,
                            onClick = { onStockClick(stock.medicineId) }
                        )
                    }
                }
            }
        }
    }
}
