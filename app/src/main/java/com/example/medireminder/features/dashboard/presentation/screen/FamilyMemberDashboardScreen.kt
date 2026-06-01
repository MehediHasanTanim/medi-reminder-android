package com.example.medireminder.features.dashboard.presentation.screen

import androidx.compose.runtime.Composable
import com.example.medireminder.features.family.presentation.screen.FamilyMemberDetailsScreen

@Composable
fun FamilyMemberDashboardScreen(
    memberId: String,
    onNavigateBack: () -> Unit,
    onEditMember: (String) -> Unit
) {
    FamilyMemberDetailsScreen(
        memberId = memberId,
        onNavigateBack = onNavigateBack,
        onEditMember = onEditMember
    )
}
