package com.example.medireminder.features.stock.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.stock.presentation.components.TransactionCard
import com.example.medireminder.features.stock.presentation.viewmodel.MedicineStockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockTransactionHistoryScreen(
    medicineId: String,
    onNavigateBack: () -> Unit,
    viewModel: MedicineStockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(medicineId) {
        viewModel.loadTransactions(medicineId)
        viewModel.loadStockDetails(medicineId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Transaction History")
                        uiState.selectedStock?.medicineName?.let {
                            Text(it, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.transactions.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.transactions.isEmpty()) {
                Text(
                    text = "No transactions found",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.transactions.sortedByDescending { it.createdAt }) { transaction ->
                        TransactionCard(transaction = transaction)
                    }
                }
            }
        }
    }
}
