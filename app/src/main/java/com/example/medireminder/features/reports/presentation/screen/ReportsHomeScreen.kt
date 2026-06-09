package com.example.medireminder.features.reports.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medireminder.features.reports.presentation.components.ReportSummaryCard

private data class ReportCategory(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val items: List<ReportItem>
)

private data class ReportItem(
    val route: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsHomeScreen(onReportClick: (String) -> Unit) {
    val categories = listOf(
        ReportCategory(
            title = "Reminder Reports",
            icon = Icons.Filled.Analytics,
            color = Color(0xFF1565C0),
            items = listOf(
                ReportItem("reports/daily", "Daily Reminder Report", "Overview of today's reminders, actions, and adherence", Icons.Filled.CalendarMonth),
                ReportItem("reports/weekly", "Weekly Compliance Report", "Week-over-week adherence and reminder completion", Icons.Filled.CalendarViewWeek),
                ReportItem("reports/monthly", "Monthly Compliance Report", "Monthly adherence trends and patterns", Icons.Filled.Analytics)
            )
        ),
        ReportCategory(
            title = "Member & Medicine Reports",
            icon = Icons.Filled.Group,
            color = Color(0xFF2E7D32),
            items = listOf(
                ReportItem("reports/member-wise", "Member-wise Report", "Per-member adherence and reminder statistics", Icons.Filled.Group),
                ReportItem("reports/medicine-wise", "Medicine-wise Report", "Per-medicine consumption and compliance", Icons.Filled.MedicalServices)
            )
        ),
        ReportCategory(
            title = "Stock Reports",
            icon = Icons.Filled.Inventory,
            color = Color(0xFFEF6C00),
            items = listOf(
                ReportItem("reports/stock-usage", "Stock Usage Report", "Consumption, refills, and remaining days", Icons.Filled.TrendingDown),
                ReportItem("reports/low-stock", "Low Stock Report", "Medicines below threshold levels", Icons.Filled.Warning),
                ReportItem("reports/expired", "Expired Medicine Report", "Expired or near-expiry medicines", Icons.Filled.Warning)
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Analytics,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reports & Analytics")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Intro card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Analytics,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Generate Reports",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Gain insights into medicine adherence, consumption patterns, and stock levels",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            categories.forEach { category ->
                item {
                    CategoryHeader(
                        title = category.title,
                        icon = category.icon,
                        color = category.color
                    )
                }
                items(category.items) { report ->
                    ReportSummaryCard(
                        title = report.title,
                        subtitle = report.description,
                        icon = report.icon,
                        color = category.color,
                        onClick = { onReportClick(report.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(
    title: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
