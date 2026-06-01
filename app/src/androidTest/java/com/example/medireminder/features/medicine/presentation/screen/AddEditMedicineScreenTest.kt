package com.example.medireminder.features.medicine.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class AddEditMedicineScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun screenRendersWithTitle() {
        composeRule.setContent {
            com.example.medireminder.features.medicine.presentation.screen.AddEditMedicineScreen(
                medicineId = null,
                onNavigateBack = {}
            )
        }

        composeRule.onNodeWithText("Add Medicine").assertIsDisplayed()
    }

    @Test
    fun editScreenRendersWithExistingMedicineTitle() {
        composeRule.setContent {
            com.example.medireminder.features.medicine.presentation.screen.AddEditMedicineScreen(
                medicineId = "test-med-id",
                onNavigateBack = {}
            )
        }

        composeRule.onNodeWithText("Edit Medicine").assertIsDisplayed()
    }
}
