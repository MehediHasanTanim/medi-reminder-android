package com.example.medireminder.core.background

data class ReliabilityStatus(
    val exactAlarmAllowed: Boolean,
    val notificationAllowed: Boolean,
    val batteryOptimizationIgnored: Boolean,
    val bootReceiverEnabled: Boolean = true
) {
    val needsUserAction: Boolean
        get() = !exactAlarmAllowed || !notificationAllowed || !batteryOptimizationIgnored

    val warnings: List<String>
        get() = buildList {
            if (!exactAlarmAllowed) add("Exact alarm permission is not granted. Reminders may not trigger on time.")
            if (!notificationAllowed) add("Notification permission is not granted. Reminder alerts will be blocked.")
            if (!batteryOptimizationIgnored) add("Battery optimization may delay or prevent reminder alarms.")
            if (!bootReceiverEnabled) add("Boot receiver is disabled. Reminders will not survive a device restart.")
        }
}
