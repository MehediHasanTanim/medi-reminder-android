package com.example.medireminder.features.family.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.presentation.components.FamilyMemberCard
import com.example.medireminder.features.family.presentation.viewmodel.FamilyMemberViewModel
import com.example.medireminder.ui.theme.MediReminderTheme

@Composable
fun FamilyListScreen(
    onAddMember: () -> Unit,
    onMemberClick: (String) -> Unit,
    viewModel: FamilyMemberViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    FamilyListContent(
        members = uiState.members,
        isLoading = uiState.isLoading,
        onAddMember = onAddMember,
        onMemberClick = onMemberClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyListContent(
    members: List<FamilyMember>,
    isLoading: Boolean,
    onAddMember: () -> Unit,
    onMemberClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family Members", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMember) {
                Icon(Icons.Default.Add, contentDescription = "Add Member")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (members.isEmpty() && !isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No family members added yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onAddMember) {
                        Text("Add Your First Member")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(members) { member ->
                        FamilyMemberCard(
                            member = member,
                            onClick = { onMemberClick(member.id) }
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

@Preview(showBackground = true)
@Composable
fun FamilyListPreview() {
    MediReminderTheme {
        FamilyListContent(
            members = listOf(
                FamilyMember(
                    id = "1",
                    fullName = "John Doe",
                    age = 30,
                    gender = "Male",
                    relationship = "Self",
                    bloodGroup = "O+",
                    phone = "123456789",
                    notes = "Test notes",
                    isActive = true,
                    createdAt = 0,
                    updatedAt = 0
                ),
                FamilyMember(
                    id = "2",
                    fullName = "Sarah Doe",
                    age = 28,
                    gender = "Female",
                    relationship = "Wife",
                    bloodGroup = "A+",
                    phone = "987654321",
                    notes = "Notes",
                    isActive = false,
                    createdAt = 0,
                    updatedAt = 0
                )
            ),
            isLoading = false,
            onAddMember = {},
            onMemberClick = {}
        )
    }
}
