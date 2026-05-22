# MediReminder — Technical Implementation Plan
## Native Android Medicine Reminder App

---

# Phase 1: Project Setup & Base Architecture

## Goal

Create a scalable Android project foundation.

## Tasks

### 1. Create Android Project

Use:

```text
Kotlin
Jetpack Compose
Minimum SDK: 26+
Target SDK: Latest
```

### 2. Add Core Dependencies

Required libraries:

```text
Jetpack Compose
Navigation Compose
Room Database
Hilt
Kotlin Coroutines
DataStore
WorkManager
Accompanist Permissions
JUnit
Compose UI Test
```

### 3. Suggested Package Structure

```text
com.example.medireminder/
 ├── core/
 │   ├── database/
 │   ├── alarm/
 │   ├── notification/
 │   ├── permissions/
 │   ├── datastore/
 │   ├── common/
 │   └── di/
 ├── features/
 │   ├── dashboard/
 │   ├── family/
 │   ├── medicine/
 │   ├── reminder/
 │   ├── stock/
 │   ├── history/
 │   ├── reports/
 │   └── settings/
 └── MainActivity.kt
```

## Deliverables

- Project runs successfully
- Compose theme configured
- Navigation configured
- Hilt configured
- Room database base setup done

---

# Phase 2: Database Design with Room

## Goal

Create local database entities and DAOs.

## Entities

- FamilyMemberEntity
- MedicineEntity
- MemberMedicineEntity
- MedicineStockEntity
- ReminderEntity
- ReminderLogEntity
- StockTransactionEntity

## Deliverables

- Room entities created
- DAOs created
- Database migration ready
- Repository layer created

---

# Phase 3: Family Member Module

## Goal

Allow user to manage family members.

## Features

- Add family member
- Edit family member
- Delete family member
- View details
- Activate/deactivate member

## Screens

```text
FamilyMemberListScreen
AddEditFamilyMemberScreen
FamilyMemberDetailsScreen
```

## Technical Components

```text
FamilyMemberDao
FamilyMemberRepository
AddFamilyMemberUseCase
UpdateFamilyMemberUseCase
DeleteFamilyMemberUseCase
GetFamilyMembersUseCase
FamilyMemberViewModel
```

## Validation Rules

- Name required
- Age cannot be negative
- Phone optional
- Relationship optional

## Testing

- Add member test
- Update member test
- Delete member test
- Empty name validation test

---

# Phase 4: Medicine Module

## Goal

Allow user to create global medicine records.

## Features

- Add medicine
- Edit medicine
- Delete medicine
- Activate/deactivate medicine
- View medicine details

## Screens

```text
MedicineListScreen
AddEditMedicineScreen
MedicineDetailsScreen
```

## Important Rule

Medicine is global and shared by all members.

---

# Phase 5: Member Medicine Assignment Module

## Goal

Assign global medicines to family members with dosage rules.

## Features

- Select family member
- Select medicine
- Set dosage
- Set frequency
- Set start/end date
- Enable auto stock reduction
- Set instructions

## Business Logic

```text
dailyTotalQuantity = dosageQuantity * frequencyPerDay
```

## Example

```text
Dosage = 1 tablet
Frequency = 3 times/day

Daily Total = 3 tablets
```

---

# Phase 6: Reminder Scheduling Module

## Goal

Create reminder schedules for assigned medicines.

## Features

- Daily reminder
- Multiple reminders per day
- Weekly reminders
- Specific weekday reminders
- Snooze
- Skip
- Taken

## Technical Components

```text
ReminderDao
ReminderRepository
ScheduleReminderUseCase
CancelReminderUseCase
UpdateReminderUseCase
ReminderScheduler
AlarmReceiver
```

## Alarm Scheduling Flow

```text
User creates reminder
        ↓
Save reminder in Room
        ↓
Schedule AlarmManager alarm
        ↓
AlarmReceiver triggers at time
        ↓
Show notification + vibrate
        ↓
User taps Taken / Snooze / Skip
        ↓
Save reminder log
        ↓
Update stock if Taken
```

---

# Phase 7: Alarm, Vibration, and Notification Module

## Goal

Trigger reliable medicine alerts.

## Android Components

```text
AlarmManager
BroadcastReceiver
NotificationManager
VibratorManager
Full-screen Activity
```

## Notification Actions

```text
Taken
Snooze
Skip
Dismiss
```

## Reminder Action Logic

### Taken

```text
Stop alarm
Create reminder log
Reduce medicine stock
Schedule next reminder
```

### Snooze

```text
Stop alarm
Create temporary alarm
Reschedule reminder
```

### Skip

```text
Stop alarm
Create skipped log
Do not reduce stock
```

---

# Phase 8: Global Medicine Stock Module

## Goal

Maintain centralized stock per medicine.

## Features

- Add stock
- Refill stock
- Manual adjustment
- View stock
- View stock history
- Low stock alert
- Expiry alert
- Estimated remaining days

## Screens

```text
MedicineStockDashboardScreen
StockDetailsScreen
RefillStockScreen
StockTransactionHistoryScreen
LowStockScreen
```

## Stock Reduction Rule

Primary rule:

```text
Reduce stock only when reminder is marked as Taken.
```

Optional rule:

```text
Auto reduce daily based on active dosage.
```

## Daily Consumption Formula

```text
Daily Consumption =
Sum of all active member dosage for the medicine
```

## Estimated Remaining Days Formula

```text
Remaining Days =
Current Stock / Daily Consumption
```

## Validation

```text
If current stock >= required quantity:
    reduce stock
else:
    show insufficient stock warning
```

---

# Phase 9: Reminder History Module

## Goal

Track user medicine actions.

## Features

- View taken medicines
- View missed medicines
- View skipped medicines
- Filter by member
- Filter by medicine
- Filter by date

## Status Types

```text
TAKEN
SKIPPED
MISSED
SNOOZED
DISMISSED
```

---

# Phase 10: Dashboard Module

## Goal

Provide overview and analytics.

## Dashboard Sections

```text
Today’s Reminders
Upcoming Reminders
Missed Reminders
Low Stock Medicines
Expiring Medicines
Family Members
```

## Medicine Dashboard

```text
Current Stock
Daily Consumption
Remaining Days
Assigned Members
Reminder Compliance
```

---

# Phase 11: Reports Module

## Goal

Generate reports and analytics.

## Reports

```text
Daily Reminder Report
Weekly Compliance Report
Monthly Compliance Report
Member-wise Report
Medicine-wise Report
Stock Usage Report
Low Stock Report
Expired Medicine Report
```

## Technical Implementation

Use Room aggregate queries.

Example:

```sql
SELECT medicineId, SUM(quantity)
FROM stock_transactions
WHERE transactionType = 'DOSE_TAKEN'
GROUP BY medicineId
```

---

# Phase 12: Settings Module

## Goal

Allow user customization.

## Settings

```text
Theme
Time format
Default snooze duration
Default vibration setting
Notification sound
Stock reduction mode
Low stock threshold
Expiry alert days
Full-screen alarm enabled
```

## Storage

Use:

```text
DataStore Preferences
```

---

# Phase 13: Boot Recovery & Background Reliability

## Goal

Ensure reminders continue after reboot.

## Components

```text
BootCompletedReceiver
ReminderRescheduler
```

## Flow

```text
Device restarted
        ↓
Load active reminders
        ↓
Re-register alarms
```

---

# Phase 14: Testing Plan

## Unit Tests

- Family member logic
- Medicine logic
- Reminder scheduling
- Stock reduction
- Daily consumption
- Low stock detection

## UI Tests

- Add family member
- Add medicine
- Assign medicine
- Create reminder
- Mark reminder taken
- Refill stock

## Integration Tests

```text
Reminder Taken → Stock Reduced
Reminder Skipped → Stock Not Reduced
Low Stock → Notification Triggered
Reboot → Reminders Rescheduled
```

---

# Phase 15: Release Preparation

## Checklist

```text
App icon
Splash screen
Privacy policy
Permission explanation
Battery optimization handling
Notification testing
Room migration testing
Android 12/13/14 testing
Crash-free validation
```

---

# Recommended Development Order

```text
1. Project setup
2. Room database
3. Family module
4. Medicine module
5. Member medicine assignment
6. Reminder module
7. Alarm + notification
8. Stock module
9. History module
10. Dashboard
11. Reports
12. Settings
13. Testing
14. Release
```

---

# Summary

MediReminder is a scalable native Android medicine reminder application designed for:

- Multi-member medicine management
- Reliable exact reminder alarms
- Centralized medicine stock tracking
- Auto stock reduction
- Low stock and expiry alerts
- Offline-first architecture
- Production-grade scalability
