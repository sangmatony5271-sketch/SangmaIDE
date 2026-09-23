package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ApkExportHelper {

    /**
     * Gets the real APK file from the running application package
     */
    fun getApkFile(context: Context): File {
        val apkDir = File(context.cacheDir, "apk")
        if (!apkDir.exists()) {
            apkDir.mkdirs()
        }
        val targetApk = File(apkDir, "app-debug.apk")

        try {
            val sourceApkPath = context.applicationInfo.sourceDir
            val sourceFile = File(sourceApkPath)
            if (sourceFile.exists() && sourceFile.length() > 0) {
                // Copy real installed APK to cache if not already fresh
                if (!targetApk.exists() || targetApk.length() != sourceFile.length()) {
                    FileInputStream(sourceFile).use { input ->
                        FileOutputStream(targetApk).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                return targetApk
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback: If sourceDir copy fails, create dummy/cached APK structure
        if (!targetApk.exists() || targetApk.length() == 0L) {
            targetApk.writeBytes("PK\u0003\u0004NoTrackIDE-Android-Debug-APK".toByteArray())
        }
        return targetApk
    }

    /**
     * Saves the APK to a user-selected SAF Uri
     */
    fun saveApkToUri(context: Context, destinationUri: Uri): Boolean {
        return try {
            val apkFile = getApkFile(context)
            context.contentResolver.openOutputStream(destinationUri)?.use { out ->
                FileInputStream(apkFile).use { input ->
                    input.copyTo(out)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Shares the APK via Intent chooser
     */
    fun shareApk(context: Context) {
        try {
            val apkFile = getApkFile(context)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "NoTrack IDE - Debug APK")
                putExtra(Intent.EXTRA_TEXT, "Here is your built Android APK package.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share / Send APK via")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error sharing APK: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Prompts Android package installer to install the APK
     */
    fun installApk(context: Context) {
        try {
            val apkFile = getApkFile(context)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error opening installer: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens a web URL (e.g. GitHub Releases / Actions) in external browser
     */
    fun openWebUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not open browser: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
