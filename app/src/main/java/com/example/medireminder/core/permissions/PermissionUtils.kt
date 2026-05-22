package com.example.medireminder.core.permissions

import android.Manifest
import android.os.Build

object PermissionUtils {
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else {
        null
    }

    val requiredPermissions = listOfNotNull(
        notificationPermission,
        Manifest.permission.VIBRATE,
        Manifest.permission.RECEIVE_BOOT_COMPLETED
    )
}
