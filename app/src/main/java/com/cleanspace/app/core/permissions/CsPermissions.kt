package com.cleanspace.app.core.permissions

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Process
import android.provider.Settings

/**
 * Central place for CleanSpace's storage/app permissions (design spec § Fase 1.1).
 *
 * Three independent grants are needed for full functionality:
 *  1. Media read access  — runtime permissions (granular on API 33+).
 *  2. All-files access   — [Environment.isExternalStorageManager], granted from a
 *                          dedicated Settings screen (not a runtime dialog).
 *  3. Usage access       — AppOps `GET_USAGE_STATS`, also Settings-driven.
 *
 * The app must degrade gracefully: media access alone already enables the core
 * photo/video/duplicate/WhatsApp cleanup. All-files + usage unlock hidden
 * folders and per-app cache sizing.
 */
object CsPermissions {

    /** Runtime permissions to request for media access, per SDK level. */
    val mediaPermissions: List<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO,
            )
        } else {
            listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    /** True once the app can read media (granular OR all-files on 33+). */
    fun hasMediaAccess(context: Context): Boolean {
        if (hasAllFilesAccess()) return true
        return mediaPermissions.all { perm ->
            context.checkPermission(perm, Process.myPid(), Process.myUid()) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    /** All-files access (MANAGE_EXTERNAL_STORAGE). Always false below API 30. */
    fun hasAllFilesAccess(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()

    /** Usage Access (needed for app cache size & last-used time). */
    fun hasUsageAccess(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName,
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName,
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /** Everything granted = full feature set available. */
    fun hasAll(context: Context): Boolean =
        hasMediaAccess(context) && hasAllFilesAccess() && hasUsageAccess(context)

    /** At least the minimum (media) to be useful. */
    fun hasMinimum(context: Context): Boolean = hasMediaAccess(context)

    // ---- Settings intents (for Settings-driven grants) ----

    /** Opens the All-files-access settings page, scoped to this app when possible. */
    fun allFilesAccessIntent(context: Context): Intent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                .setData(Uri.parse("package:${context.packageName}"))
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:${context.packageName}"))
        }

    /** Opens the system Usage-Access settings list. */
    fun usageAccessIntent(): Intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

    /** App details settings (fallback / per-app cache clearing). */
    fun appDetailsIntent(context: Context): Intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.parse("package:${context.packageName}"))
}
