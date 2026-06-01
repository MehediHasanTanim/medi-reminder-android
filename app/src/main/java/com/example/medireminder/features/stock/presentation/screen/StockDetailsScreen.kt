package com.example.medireminder.features.stock.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.stock.presentation.viewmodel.MedicineStockViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailsScreen(
    medicineId: String,
    onNavigateBack: () -> Unit,
    onRefillStock: (String) -> Unit,
    onViewHistory: (String) -> Unit,
    viewModel: MedicineStockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stock = uiState.selectedStock

    LaunchedEffect(medicineId) {
        viewModel.loadStockDetails(medicineId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stock Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (stock != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Medicine Summary
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stock.medicineName ?: "Unknown",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Current Inventory",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Stock Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            label = "Current Stock",
                            value = "${stock.currentQuantity} ${stock.unit}",
                            modifier = Modifier.weight(1f),
                            highlight = stock.isLowStock
                        )
                        StatCard(
                            label = "Remaining Days",
                            value = stock.estimatedRemainingDays?.let { "~$it days" } ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Detail Information
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoRow(label = "Unit", value = stock.unit)
                            InfoRow(label = "Low Stock Threshold", value = "${stock.lowStockThreshold} ${stock.unit}")
                            InfoRow(
                                label = "Daily Consumption", 
                                value = "${stock.dailyConsumption} ${stock.unit}/day"
                            )
                            
                            val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            InfoRow(
                                label = "Expiry Date", 
                                value = stock.expiryDate?.let { df.format(Date(it)) } ?: "Not set"
                            )
                            InfoRow(
                                label = "Last Refilled", 
                                value = stock.lastRefillDate?.let { df.format(Date(it)) } ?: "Never"
                            )
                            InfoRow(
                                label = "Auto-Reduce Enabled", 
                                value = if (stock.autoReduceEnabled) "Yes" else "No"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Actions
                    Button(
                        onClick = { onRefillStock(medicineId) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Refill Stock")
                    }

                    OutlinedButton(
                        onClick = { onViewHistory(medicineId) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Transaction History")
                    }
                }
            } else if (!uiState.isLoading) {
                Text(
                    text = "Stock record not found",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) MaterialTheme.colorScheme.errorContainer 
                             else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(
                text = value, 
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (highlight) MaterialTheme.colorScheme.onErrorContainer 
                        else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
