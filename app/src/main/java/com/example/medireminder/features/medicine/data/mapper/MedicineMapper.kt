package com.example.medireminder.features.medicine.data.mapper

import com.example.medireminder.core.database.entities.MedicineEntity
import com.example.medireminder.features.medicine.domain.model.Medicine

fun MedicineEntity.toDomain(): Medicine {
    return Medicine(
        id = id,
        name = name,
        genericName = genericName,
        medicineType = medicineType,
        strength = strength,
        unit = unit,
        manufacturer = manufacturer,
        notes = notes,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Medicine.toEntity(): MedicineEntity {
    return MedicineEntity(
        id = id,
        name = name,
        genericName = genericName,
        medicineType = medicineType,
        strength = strength,
        unit = unit,
        manufacturer = manufacturer,
        notes = notes,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
