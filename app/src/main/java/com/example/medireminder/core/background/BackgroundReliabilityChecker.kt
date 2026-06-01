package com.example.medireminder.core.background

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class BackgroundReliabilityChecker(
    private val context: Context
) {

    fun checkStatus(): ReliabilityStatus {
        return ReliabilityStatus(
            exactAlarmAllowed = ExactAlarmPermissionHelper.canScheduleExactAlarms(context),
            notificationAllowed = isNotificationPermissionGranted(),
            batteryOptimizationIgnored = BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context),
            bootReceiverEnabled = isBootReceiverEnabled()
        )
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun isBootReceiverEnabled(): Boolean {
        val packageManager = context.packageManager
        val componentName = android.content.ComponentName(
            context,
            com.example.medireminder.core.alarm.BootCompletedReceiver::class.java
        )
        return try {
            packageManager.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                packageManager.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
        } catch (e: Exception) {
            true
        }
    }
}
