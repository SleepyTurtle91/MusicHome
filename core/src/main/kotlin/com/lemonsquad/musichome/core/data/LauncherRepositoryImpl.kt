package com.lemonsquad.musichome.core.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.lemonsquad.musichome.core.domain.AppInfo
import com.lemonsquad.musichome.core.domain.LauncherRepository

class LauncherRepositoryImpl(private val context: Context) : LauncherRepository {

    override fun getInstalledApps(): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        return pm.queryIntentActivities(intent, 0).map { resolveInfo ->
            AppInfo(
                label = resolveInfo.loadLabel(pm).toString(),
                packageName = resolveInfo.activityInfo.packageName,
                icon = resolveInfo.loadIcon(pm)
            )
        }.filter { it.packageName != context.packageName } // Don't list itself
         .sortedBy { it.label }
    }

    override fun launchApp(packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        }
    }
}
