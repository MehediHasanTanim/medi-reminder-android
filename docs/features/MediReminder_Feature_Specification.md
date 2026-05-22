# MediReminder — Native Android Medicine Reminder App
## Complete Product Feature Specification

---

# 1. Project Overview

## App Name

**MediReminder**

## Platform

Native Android Application

## Primary Goal

A family-focused medicine reminder application that allows users to:

- Manage family members
- Assign medicines to members
- Schedule medicine reminders
- Receive alarms, vibration, and local notifications
- Track medicine consumption
- Maintain centralized medicine inventory/stock
- Automatically reduce medicine stock based on dosage
- Monitor low stock and expiry alerts

---

# 2. Technology Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | Clean Architecture + MVVM |
| Local Database | Room |
| Dependency Injection | Hilt |
| Background Tasks | WorkManager |
| Exact Alarm Scheduling | AlarmManager |
| Notifications | NotificationManager |
| Navigation | Navigation Compose |
| Async | Kotlin Coroutines + Flow |
| State Management | StateFlow |
| Preferences | DataStore |
| Testing | JUnit + Compose UI Test |

---

# 3. Core Modules

---

# 3.1 Family Member Management

## Features

- Add family member
- Edit family member
- Delete family member
- View member details
- Activate/deactivate member
- Assign medicines to member

## Fields

```text
id
full_name
photo
age
gender
relationship
blood_group
phone
notes
is_active
created_at
updated_at
```

---

# 3.2 Medicine Management

## Features

- Add medicine
- Edit medicine
- Delete medicine
- View medicine details
- Maintain centralized medicine stock
- Assign medicine to multiple family members

## Medicine Fields

```text
id
name
generic_name
medicine_type
strength
unit
manufacturer
description
notes
is_active
created_at
updated_at
```

## Medicine Types

- Tablet
- Capsule
- Syrup
- Injection
- Drops
- Inhaler
- Cream
- Powder

---

# 3.3 Member Medicine Assignment

This module links medicines with family members.

## Features

- Assign medicine to member
- Configure dosage
- Configure frequency
- Enable auto stock reduction
- Set active date range

## Fields

```text
id
family_member_id
medicine_id
dosage_quantity
frequency_per_day
daily_total_quantity
instructions
start_date
end_date
is_active
auto_reduce_stock
created_at
updated_at
```

## Example

```text
Father → Napa 500mg
Dosage: 1 tablet
Frequency: 2 times/day
Daily Total: 2 tablets
```

---

# 3.4 Medicine Reminder Management

## Features

- Add reminder
- Edit reminder
- Delete reminder
- Pause reminder
- Resume reminder
- Repeat reminders
- Multi-time reminders

## Reminder Types

- One-time
- Daily
- Weekly
- Specific weekdays
- Every X hours
- Custom schedule

## Reminder Fields

```text
id
member_medicine_id
reminder_time
repeat_type
repeat_days
start_date
end_date
alarm_tone
vibration_enabled
notification_enabled
snooze_enabled
snooze_duration
is_active
created_at
updated_at
```

---

# 3.5 Alarm and Notification System

## Behavior

At reminder time:

- Trigger exact alarm
- Vibrate phone
- Play alarm sound
- Show local notification
- Show full-screen reminder screen
- Wake device if needed

## Notification Actions

- Taken
- Snooze
- Skip
- Dismiss

## Notification Information

```text
Member Name
Medicine Name
Dosage
Instructions
Reminder Time
```

## Alarm Features

- Custom ringtone
- Continuous ringing until dismissed
- Vibration patterns
- Full-screen urgent mode
- Notification channels

---

# 3.6 Reminder History

## Features

Track all reminder actions:

- Taken
- Missed
- Skipped
- Snoozed
- Dismissed

## Fields

```text
id
reminder_id
member_id
medicine_id
scheduled_time
action_taken
action_time
notes
created_at
```

---

# 3.7 Global Medicine Stock Management

## Important Design Decision

Medicine stock is centralized globally.

Stock does NOT belong to a specific family member.

Multiple family members can consume from the same medicine stock.

---

# 3.8 Medicine Stock Features

## Features

- Maintain total medicine stock
- Auto reduce stock
- Manual stock adjustment
- Refill stock
- Low stock alerts
- Expiry alerts
- Stock transaction history
- Estimated remaining days
- Daily consumption tracking

---

# 3.9 Medicine Stock Fields

```text
id
medicine_id
current_quantity
unit
low_stock_threshold
expiry_date
auto_reduce_enabled
last_refill_date
created_at
updated_at
```

---

# 3.10 Auto Stock Reduction

## Rule

Stock should reduce when:

```text
Medicine reminder is marked as Taken
```

## Optional Mode

App setting:

```text
Stock Reduction Mode

1. Reduce on Taken
2. Auto reduce daily based on active dosage
```

---

# 3.11 Stock Calculation Logic

## Daily Consumption

```text
Daily Consumption =
Sum of all active member dosage for the medicine
```

## Estimated Remaining Days

```text
Remaining Days =
Current Stock / Daily Consumption
```

## Example

```text
Current Stock = 90 tablets
Daily Consumption = 3 tablets

Remaining Days = 30 days
```

---

# 3.12 Stock Transaction History

## Features

Track every stock change.

## Transaction Types

```text
INITIAL_STOCK
DOSE_TAKEN
AUTO_DAILY_REDUCTION
REFILL
MANUAL_ADJUSTMENT
EXPIRED_REMOVAL
WASTED
```

## Fields

```text
id
medicine_id
transaction_type
quantity
previous_quantity
new_quantity
reason
created_at
```

---

# 3.13 Low Stock Alerts

## Trigger Conditions

Notify when:

- Stock is low
- Stock is finished
- Medicine is near expiry
- Medicine has expired

## Example Notification

```text
Low Stock Alert

Napa 500mg has only 3 tablets left.
Please refill soon.
```

---

# 3.14 Dashboard Module

## Main Dashboard

Display:

- Today’s reminders
- Upcoming reminders
- Missed reminders
- Low stock medicines
- Expiring medicines
- Family member overview

## Medicine Dashboard

Display:

- Current stock
- Daily consumption
- Remaining days
- Assigned members
- Reminder statistics

---

# 3.15 Calendar View

## Features

- Daily view
- Weekly view
- Monthly view
- Missed reminder indicators
- Medicine schedule overview

---

# 3.16 Reports and Analytics

## Reports

- Daily medicine report
- Weekly compliance report
- Monthly compliance report
- Missed medicine report
- Member-wise medicine report
- Medicine consumption report
- Stock usage report

---

# 3.17 Settings Module

## General Settings

- Light/Dark mode
- Notification settings
- Vibration settings
- Alarm ringtone
- Time format
- Language selection

## Reminder Settings

- Snooze duration
- Full-screen reminders
- Auto-dismiss timeout
- Repeat alarm until dismissed

## Stock Settings

- Stock reduction mode
- Low stock threshold defaults
- Expiry alert days

---

# 4. Required Android Permissions

## Permissions

```xml
POST_NOTIFICATIONS
VIBRATE
SCHEDULE_EXACT_ALARM
USE_EXACT_ALARM
RECEIVE_BOOT_COMPLETED
WAKE_LOCK
FOREGROUND_SERVICE
```

---

# 5. Important Technical Requirements

## Exact Alarm Handling

Use:

```text
AlarmManager.setExactAndAllowWhileIdle()
```

## Phone Restart Handling

Re-register alarms after reboot using:

```text
BOOT_COMPLETED receiver
```

## Battery Optimization

Handle:

- Doze Mode
- Battery Optimization Restrictions
- Background Execution Limits

---

# 6. Database Design

## Tables

```text
family_members
medicines
member_medicines
medicine_reminders
medicine_stock
stock_transactions
reminder_logs
settings
```

---

# 7. Suggested App Screens

## Authentication

- Splash Screen
- Onboarding
- Optional PIN/Lock Screen

## Main Screens

1. Home Dashboard
2. Family Members List
3. Add/Edit Family Member
4. Medicine List
5. Add/Edit Medicine
6. Member Medicine Assignment
7. Reminder List
8. Add/Edit Reminder
9. Full-Screen Alarm UI
10. Reminder History
11. Medicine Stock Dashboard
12. Refill Stock Screen
13. Stock History
14. Reports
15. Calendar View
16. Settings

---

# 8. Recommended Clean Architecture Structure

```text
app/
 └── src/main/java/com/example/medireminder/
     ├── core/
     │   ├── alarm/
     │   ├── notification/
     │   ├── database/
     │   ├── permissions/
     │   ├── utils/
     │   ├── datastore/
     │   └── di/
     │
     ├── features/
     │   ├── auth/
     │   ├── dashboard/
     │   ├── family/
     │   ├── medicine/
     │   ├── stock/
     │   ├── reminder/
     │   ├── history/
     │   ├── reports/
     │   └── settings/
     │
     └── MainActivity.kt
```

---

# 9. MVP Scope

## MVP Features

### Family Management

- Add/edit/delete family members

### Medicine Management

- Add/edit/delete medicines

### Reminder System

- Daily reminders
- Alarm + vibration
- Local notifications
- Snooze
- Skip
- Taken action

### Stock Management

- Global medicine stock
- Manual refill
- Auto stock reduction
- Low stock alerts
- Expiry alerts
- Stock history

### Dashboard

- Today reminders
- Upcoming reminders
- Low stock medicines

---

# 10. Future Features

## Planned Enhancements

- Cloud backup
- Firebase sync
- Multi-device sync
- Wear OS support
- Barcode scanning
- OCR prescription scanning
- Voice reminder
- AI medicine assistant
- Doctor prescription upload
- Caregiver notifications
- WhatsApp/SMS alerts
- Shared family access
- Medicine purchase tracking

---

# 11. Recommended Development Phases

## Phase 1

- Project setup
- Architecture setup
- Navigation
- Theme setup
- Room database

## Phase 2

- Family member CRUD
- Medicine CRUD

## Phase 3

- Reminder scheduling
- AlarmManager integration
- Notifications

## Phase 4

- Full-screen reminder UI
- Reminder actions
- Reminder history

## Phase 5

- Medicine stock management
- Auto stock reduction
- Low stock alerts

## Phase 6

- Reports
- Calendar
- Analytics

## Phase 7

- App optimization
- Battery optimization handling
- Production release preparation

---

# 12. Production Readiness Checklist

## Functional

- Exact alarm works reliably
- Notification works in background
- Alarm works during sleep mode
- Reboot recovery works
- Stock calculations are accurate

## Performance

- Optimized Room queries
- Efficient alarm scheduling
- Battery optimization handling

## Security

- Encrypted local database
- Secure DataStore usage
- Optional app lock

---

# 13. Summary

MediReminder is a production-grade native Android medicine reminder system designed for family medicine management.

Key strengths:

- Multi-member medicine management
- Reliable exact reminders
- Centralized medicine inventory
- Auto stock reduction
- Low stock and expiry monitoring
- Full offline capability
- Clean scalable architecture
