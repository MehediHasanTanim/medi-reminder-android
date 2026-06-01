package com.example.medireminder.features.stock.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class AddStockScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun screenRendersWithTitle() {
        composeRule.setContent {
            com.example.medireminder.features.stock.presentation.screen.AddStockScreen(
                onNavigateBack = {}
            )
        }

        composeRule.onNodeWithText("Add Stock").assertIsDisplayed()
    }
}
