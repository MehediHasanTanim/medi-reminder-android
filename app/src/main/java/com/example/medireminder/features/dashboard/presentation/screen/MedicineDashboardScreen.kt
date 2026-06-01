package com.example.medireminder.features.dashboard.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.dashboard.presentation.components.MedicineDashboardCard
import com.example.medireminder.features.dashboard.presentation.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDashboardScreen(
    medicineId: String,
    onNavigateBack: () -> Unit,
    onViewStockDetails: (String) -> Unit,
    onViewTransactions: (String) -> Unit,
    onViewAssignedMembers: (String) -> Unit,
    onViewHistory: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.medicineDashboardState.collectAsState()
    val dashboard = state.dashboard

    LaunchedEffect(medicineId) {
        viewModel.loadMedicineDashboard(medicineId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Dashboard") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (dashboard == null) {
                Text(state.errorMessage ?: "Medicine dashboard unavailable")
            } else {
                MedicineDashboardCard(dashboard = dashboard)
                if (dashboard.currentStock <= dashboard.lowStockThreshold) {
                    Text("Low stock warning", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
                dashboard.expiryDate?.let {
                    Text("Expiry date is set for this medicine", color = MaterialTheme.colorScheme.error)
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Assigned members", fontWeight = FontWeight.Bold)
                        Text(dashboard.assignedMembers.ifEmpty { listOf("None") }.joinToString(", "))
                    }
                }

                Button(onClick = { onViewStockDetails(medicineId) }, modifier = Modifier.fillMaxWidth()) {
                    Text("View Stock Details")
                }
                OutlinedButton(onClick = { onViewTransactions(medicineId) }, modifier = Modifier.fillMaxWidth()) {
                    Text("View Transactions")
                }
                OutlinedButton(onClick = { onViewAssignedMembers(medicineId) }, modifier = Modifier.fillMaxWidth()) {
                    Text("View Assigned Members")
                }
                OutlinedButton(onClick = onViewHistory, modifier = Modifier.fillMaxWidth()) {
                    Text("View History")
                }
            }
        }
    }
}
