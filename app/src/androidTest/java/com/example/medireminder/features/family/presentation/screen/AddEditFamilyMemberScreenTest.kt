package com.example.medireminder.features.family.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class AddEditFamilyMemberScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun screenRendersWithTitle() {
        composeRule.setContent {
            com.example.medireminder.features.family.presentation.screen.AddEditFamilyMemberScreen(
                memberId = null,
                onNavigateBack = {}
            )
        }

        composeRule.onNodeWithText("Add Family Member").assertIsDisplayed()
    }

    @Test
    fun editScreenRendersWithExistingMemberTitle() {
        composeRule.setContent {
            com.example.medireminder.features.family.presentation.screen.AddEditFamilyMemberScreen(
                memberId = "test-id",
                onNavigateBack = {}
            )
        }

        composeRule.onNodeWithText("Edit Family Member").assertIsDisplayed()
    }
}
