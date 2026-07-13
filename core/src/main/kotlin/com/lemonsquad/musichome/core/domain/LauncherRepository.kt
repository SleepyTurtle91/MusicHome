package com.lemonsquad.musichome.core.domain

import android.graphics.drawable.Drawable

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable?
)

interface LauncherRepository {
    fun getInstalledApps(): List<AppInfo>
    fun launchApp(packageName: String)
}
