package com.example.medireminder.features.membermedicine.data.mapper

import com.example.medireminder.core.database.entities.MemberMedicineEntity
import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine

fun MemberMedicineEntity.toDomain(
    familyMemberName: String? = null,
    medicineName: String? = null,
    medicineUnit: String? = null,
    medicineType: String? = null
): MemberMedicine {
    return MemberMedicine(
        id = id,
        familyMemberId = familyMemberId,
        medicineId = medicineId,
        familyMemberName = familyMemberName,
        medicineName = medicineName,
        medicineUnit = medicineUnit,
        medicineType = medicineType,
        dosageQuantity = dosageQuantity,
        frequencyPerDay = frequencyPerDay,
        dailyTotalQuantity = dailyTotalQuantity,
        instructions = instructions,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        autoReduceStock = autoReduceStock,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun MemberMedicine.toEntity(): MemberMedicineEntity {
    return MemberMedicineEntity(
        id = id,
        familyMemberId = familyMemberId,
        medicineId = medicineId,
        dosageQuantity = dosageQuantity,
        frequencyPerDay = frequencyPerDay,
        dailyTotalQuantity = dailyTotalQuantity,
        instructions = instructions,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        autoReduceStock = autoReduceStock,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
