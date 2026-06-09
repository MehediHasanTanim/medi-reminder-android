package com.example.medireminder.features.stock.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medireminder.features.stock.domain.model.MedicineStock
import com.example.medireminder.ui.theme.ErrorRed
import com.example.medireminder.ui.theme.SuccessGreen
import com.example.medireminder.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockCard(
    stock: MedicineStock,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stockRatio = if (stock.lowStockThreshold > 0) {
        (stock.currentQuantity / stock.lowStockThreshold).toFloat().coerceIn(0f, 2f)
    } else {
        1f
    }

    val barColor = when {
        stock.isExpired -> ErrorRed
        stock.currentQuantity <= 0 -> ErrorRed
        stock.isLowStock && stock.currentQuantity <= stock.lowStockThreshold * 0.5 -> ErrorRed
        stock.isLowStock -> WarningOrange
        else -> SuccessGreen
    }

    val barProgress by animateFloatAsState(
        targetValue = stockRatio / 2f,
        animationSpec = tween(800),
        label = "stockBar"
    )

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // ── Top Row: Medicine Name + Badge ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Medicine avatar circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .then(
                                Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .let { mod ->
                                        if (stock.isExpired) mod.then(
                                            Modifier
                                        ) else mod
                                    }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = barColor.copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (stock.medicineName ?: "?").take(2).uppercase(),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = barColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stock.medicineName ?: "Unknown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (stock.medicineType != null) {
                            Text(
                                text = stock.medicineType,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                if (stock.isExpired) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Expired", fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = ErrorRed,
                            containerColor = ErrorRed.copy(alpha = 0.1f)
                        ),
                        border = null
                    )
                } else if (stock.isLowStock) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Low Stock", fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = WarningOrange,
                            containerColor = WarningOrange.copy(alpha = 0.1f)
                        ),
                        border = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Stock Progress Bar ──
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stock.currentQuantity.toInt()} ${stock.unit}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = barColor
                    )
                    Text(
                        text = stock.estimatedRemainingDays?.let {
                            "~${it.toInt()} days left"
                        } ?: "N/A",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = { barProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = barColor,
                    trackColor = barColor.copy(alpha = 0.12f),
                )
            }

            // ── Bottom Row: Threshold info ──
            if (stock.dailyConsumption > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Threshold: ${stock.lowStockThreshold.toInt()} ${stock.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Daily: ${stock.dailyConsumption} ${stock.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
