package com.lemonsquad.musichome.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lemonsquad.musichome.core.domain.model.NavigationMode

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 0, // Single row
    val navigationMode: String = NavigationMode.AUTO.name,
    val theme: String = "walkman",
    val updatedAt: Long = System.currentTimeMillis()
)
