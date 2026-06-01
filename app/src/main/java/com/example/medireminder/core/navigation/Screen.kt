package com.example.medireminder.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object MedicineDashboard : Screen("medicine_dashboard/{medicineId}") {
        fun createRoute(medicineId: String) = "medicine_dashboard/$medicineId"
    }
    object FamilyDashboard : Screen("family_dashboard/{memberId}") {
        fun createRoute(memberId: String) = "family_dashboard/$memberId"
    }

    // Family
    object FamilyMembers : Screen("family_members")
    object FamilyMemberAdd : Screen("family_member_add")
    object FamilyMemberEdit : Screen("family_member_edit/{memberId}") {
        fun createRoute(memberId: String) = "family_member_edit/$memberId"
    }
    object FamilyMemberDetails : Screen("family_member_details/{memberId}") {
        fun createRoute(memberId: String) = "family_member_details/$memberId"
    }

    // Medicine
    object Medicines : Screen("medicines")
    object MedicineAdd : Screen("medicine_add")
    object MedicineEdit : Screen("medicine_edit/{medicineId}") {
        fun createRoute(medicineId: String) = "medicine_edit/$medicineId"
    }
    object MedicineDetails : Screen("medicine_details/{medicineId}") {
        fun createRoute(medicineId: String) = "medicine_details/$medicineId"
    }

    // Member Medicine Assignment
    object MemberMedicines : Screen("member_medicines")
    object MemberMedicineAdd : Screen("member_medicine_add")
    object MemberMedicineEdit : Screen("member_medicine_edit/{assignmentId}") {
        fun createRoute(assignmentId: String) = "member_medicine_edit/$assignmentId"
    }
    object MemberMedicineDetails : Screen("member_medicine_details/{assignmentId}") {
        fun createRoute(assignmentId: String) = "member_medicine_details/$assignmentId"
    }

    // Reminders
    object Reminders : Screen("reminders")
    object ReminderAdd : Screen("reminder_add")
    object ReminderEdit : Screen("reminder_edit/{reminderId}") {
        fun createRoute(reminderId: String) = "reminder_edit/$reminderId"
    }
    object ReminderDetails : Screen("reminder_details/{reminderId}") {
        fun createRoute(reminderId: String) = "reminder_details/$reminderId"
    }

    // Stock
    object StockDashboard : Screen("stock_dashboard")
    object StockAdd : Screen("stock_add")
    object StockDetails : Screen("stock_details/{medicineId}") {
        fun createRoute(medicineId: String) = "stock_details/$medicineId"
    }
    object StockRefill : Screen("stock_refill/{medicineId}") {
        fun createRoute(medicineId: String) = "stock_refill/$medicineId"
    }
    object StockTransactions : Screen("stock_transactions/{medicineId}") {
        fun createRoute(medicineId: String) = "stock_transactions/$medicineId"
    }
    object LowStock : Screen("low_stock")

    object Reminder : Screen("reminder") // Keep for backward compatibility if used in bottom nav
    object Stock : Screen("stock") // Keep for backward compatibility if used in bottom nav
    object History : Screen("history")
    object ReminderHistory : Screen("reminder_history")
    object ReminderHistoryDetails : Screen("reminder_history_details/{historyId}") {
        fun createRoute(historyId: String) = "reminder_history_details/$historyId"
    }
    object Reports : Screen("reports")
    object ReportsDaily : Screen("reports/daily")
    object ReportsWeekly : Screen("reports/weekly")
    object ReportsMonthly : Screen("reports/monthly")
    object ReportsMemberWise : Screen("reports/member-wise")
    object ReportsMedicineWise : Screen("reports/medicine-wise")
    object ReportsStockUsage : Screen("reports/stock-usage")
    object ReportsLowStock : Screen("reports/low-stock")
    object ReportsExpired : Screen("reports/expired")
    object Settings : Screen("settings")
    object SettingsAppearance : Screen("settings/appearance")
    object SettingsReminder : Screen("settings/reminder")
    object SettingsNotification : Screen("settings/notification")
    object SettingsStock : Screen("settings/stock")
}
