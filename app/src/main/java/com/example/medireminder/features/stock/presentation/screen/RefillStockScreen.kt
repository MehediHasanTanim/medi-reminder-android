package com.example.medireminder.features.stock.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.stock.presentation.viewmodel.MedicineStockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefillStockScreen(
    medicineId: String,
    onNavigateBack: () -> Unit,
    viewModel: MedicineStockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.refillFormState.collectAsState()
    val stock = uiState.selectedStock

    LaunchedEffect(medicineId) {
        viewModel.loadStockDetails(medicineId)
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            onNavigateBack()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Refill Stock") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            if (stock != null) {
                Text(
                    text = stock.medicineName ?: "Unknown",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current: ${stock.currentQuantity} ${stock.unit}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider()

                OutlinedTextField(
                    value = formState.quantity,
                    onValueChange = viewModel::onRefillQuantityChange,
                    label = { Text("Refill Quantity (${stock.unit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = formState.validationErrors.containsKey("quantity"),
                    supportingText = { formState.validationErrors["quantity"]?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = formState.reason,
                    onValueChange = viewModel::onRefillReasonChange,
                    label = { Text("Reason / Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.refillStock(medicineId, onSuccess = {}) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Add to Stock")
                }
            } else if (uiState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}
