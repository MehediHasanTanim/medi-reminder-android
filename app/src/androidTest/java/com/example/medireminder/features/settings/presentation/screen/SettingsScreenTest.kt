package com.example.medireminder.features.settings.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.presentation.components.SettingsDropdownRow
import com.example.medireminder.features.settings.presentation.components.SettingsSwitchRow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun settingsScreenRenders() {
        composeRule.setContent {
            SettingsScreen(
                onAppearanceClick = {},
                onReminderClick = {},
                onNotificationClick = {},
                onStockClick = {}
            )
        }

        composeRule.onNodeWithText("Settings").assertIsDisplayed()
        composeRule.onNodeWithText("Appearance").assertIsDisplayed()
        composeRule.onNodeWithText("Reminder").assertIsDisplayed()
        composeRule.onNodeWithText("Notification").assertIsDisplayed()
        composeRule.onNodeWithText("Stock").assertIsDisplayed()
    }

    @Test
    fun settingsRowClickCallsCallback() {
        var clicked = false
        composeRule.setContent {
            SettingsScreen(
                onAppearanceClick = { clicked = true },
                onReminderClick = {},
                onNotificationClick = {},
                onStockClick = {}
            )
        }

        composeRule.onNodeWithText("Theme and time format").performClick()

        assertTrue(clicked)
    }

    @Test
    fun dropdownSelectionCallsCallback() {
        var selected = AppThemeMode.SYSTEM
        composeRule.setContent {
            SettingsDropdownRow(
                title = "Theme",
                selected = selected,
                options = AppThemeMode.entries.toList(),
                label = { it.name },
                onSelected = { selected = it }
            )
        }

        composeRule.onNodeWithText("Theme").performClick()
        composeRule.onNodeWithText("DARK").performClick()

        assertEquals(AppThemeMode.DARK, selected)
    }

    @Test
    fun switchToggleCallsCallback() {
        var enabled = false
        composeRule.setContent {
            SettingsSwitchRow(
                title = "Full-screen alarm",
                checked = enabled,
                onCheckedChange = { enabled = it }
            )
        }

        composeRule.onNodeWithText("Full-screen alarm").performClick()

        assertTrue(enabled)
    }
}
