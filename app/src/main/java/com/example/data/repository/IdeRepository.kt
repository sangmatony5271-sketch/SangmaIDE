package com.example.data.repository

import com.example.data.local.BackupDao
import com.example.data.local.FileDao
import com.example.data.local.GitDao
import com.example.data.model.BackupSnapshot
import com.example.data.model.GitCommit
import com.example.data.model.ProjectFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class IdeRepository(
    private val fileDao: FileDao,
    private val gitDao: GitDao,
    private val backupDao: BackupDao
) {
    val allFiles: Flow<List<ProjectFile>> = fileDao.getAllFiles()
    val allCommits: Flow<List<GitCommit>> = gitDao.getAllCommits()
    val allBackups: Flow<List<BackupSnapshot>> = backupDao.getAllBackups()

    suspend fun initializeDefaultProjectIfEmpty() {
        val existing = allFiles.first()
        if (existing.isEmpty()) {
            val defaultFiles = listOf(
                ProjectFile(
                    path = "app/src/main/java/MainActivity.kt",
                    name = "MainActivity.kt",
                    content = """
package com.aistudio.notrackide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoTrackIdeSampleApp()
        }
    }
}

@Composable
fun NoTrackIdeSampleApp() {
    var count by remember { mutableStateOf(0) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to NoTrack IDE!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Zero-tracking E2EE Android IDE powered by notrack.ai",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { count++ }) {
                Text(text = "Clicks: ${"$"}count")
            }
        }
    }
}
                    """.trimIndent(),
                    language = "kotlin"
                ),
                ProjectFile(
                    path = "app/src/main/java/Theme.kt",
                    name = "Theme.kt",
                    content = """
package com.aistudio.notrackide.ui.theme

import androidx.compose.material3.DarkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkIndigo = Color(0xFF0F172A)
val CyanAccent = Color(0xFF06B6D4)
val PurpleAccent = Color(0xFF8B5CF6)

private val DarkColors = DarkColorScheme(
    primary = CyanAccent,
    secondary = PurpleAccent,
    background = DarkIndigo
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
                    """.trimIndent(),
                    language = "kotlin"
                ),
                ProjectFile(
                    path = "app/build.gradle.kts",
                    name = "build.gradle.kts",
                    content = """
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.aistudio.notrackide"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aistudio.notrackide.dev"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
}
                    """.trimIndent(),
                    language = "gradle"
                ),
                ProjectFile(
                    path = "app/src/main/AndroidManifest.xml",
                    name = "AndroidManifest.xml",
                    content = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="NoTrack IDE App"
        android:theme="@style/Theme.NoTrackIDE">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
                    """.trimIndent(),
                    language = "xml"
                ),
                ProjectFile(
                    path = "README.md",
                    name = "README.md",
                    content = """
# NoTrack IDE Project

Welcome to your privacy-focused Android IDE workspace!

## Key Features
- **notrack.ai Assistant**: Zero telemetry AI code completion, bug fixing, and refactoring.
- **End-to-End Encryption**: E2EE cloud sync & peer collaboration.
- **Built-in APK Builder**: Interactive Gradle log & simulated APK output.
- **GitHub Auto APK Build**: Automated CI/CD workflow (.github/workflows/android_build.yml) on push & PR.
- **Git & Debugger**: Full commit history, branch management, breakpoints & logcat console.
                    """.trimIndent(),
                    language = "markdown"
                ),
                ProjectFile(
                    path = ".github/workflows/android_build.yml",
                    name = "android_build.yml",
                    content = GITHUB_ACTIONS_WORKFLOW_CONTENT,
                    language = "yaml"
                )
            )
            fileDao.insertAllFiles(defaultFiles)

            // Initial Git Commit
            gitDao.insertCommit(
                GitCommit(
                    hash = "a1b2c3d4",
                    message = "Initial commit: Project created with GitHub Auto APK Build CI/CD",
                    author = "Developer",
                    branch = "main",
                    filesChangedCount = 6
                )
            )

            // Initial Backup
            backupDao.insertBackup(
                BackupSnapshot(
                    label = "Initial System Backup (E2EE)",
                    encryptedPayload = "AES256:e2ee_snapshot_001_secure_hash",
                    fileCount = 5
                )
            )
        }
    }

    suspend fun getFile(path: String): ProjectFile? {
        return fileDao.getFileByPath(path)
    }

    suspend fun saveFile(file: ProjectFile) {
        fileDao.insertOrUpdateFile(file)
    }

    suspend fun deleteFile(path: String) {
        fileDao.deleteFile(path)
    }

    suspend fun addGitCommit(message: String, branch: String, filesCount: Int): GitCommit {
        val newHash = java.util.UUID.randomUUID().toString().take(8)
        val commit = GitCommit(
            hash = newHash,
            message = message,
            author = "Developer (Local E2EE)",
            branch = branch,
            filesChangedCount = filesCount
        )
        gitDao.insertCommit(commit)
        return commit
    }

    suspend fun createBackup(fileCount: Int): BackupSnapshot {
        val snapshot = BackupSnapshot(
            label = "Auto-Backup ${System.currentTimeMillis() % 10000}",
            encryptedPayload = "AES256:${java.util.UUID.randomUUID()}",
            fileCount = fileCount
        )
        backupDao.insertBackup(snapshot)
        return snapshot
    }

    suspend fun ensureGithubWorkflowExists(): ProjectFile {
        val existing = fileDao.getFileByPath(".github/workflows/android_build.yml")
        if (existing != null) {
            return existing
        }
        val workflowFile = ProjectFile(
            path = ".github/workflows/android_build.yml",
            name = "android_build.yml",
            content = GITHUB_ACTIONS_WORKFLOW_CONTENT,
            language = "yaml"
        )
        fileDao.insertOrUpdateFile(workflowFile)
        return workflowFile
    }

    companion object {
        val GITHUB_ACTIONS_WORKFLOW_CONTENT = """
name: Android Auto APK Build CI

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]
  workflow_dispatch:

concurrency:
  group: ${'$'}{{ github.workflow }}-${'$'}{{ github.ref }}
  cancel-in-progress: true

jobs:
  build-apk:
    name: Build Android APK
    runs-on: ubuntu-latest

    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4
        with:
          fetch-depth: 1

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'

      - name: Set up Android SDK
        uses: android-actions/setup-android@v3

      - name: Grant execute permission for gradlew
        run: |
          if [ -f "gradlew" ]; then
            chmod +x gradlew
          fi

      - name: Build Debug APK with Gradle
        run: |
          if [ -f "gradlew" ]; then
            ./gradlew assembleDebug --stacktrace --no-daemon
          else
            gradle assembleDebug --stacktrace --no-daemon
          fi

      - name: Verify APK Output
        run: |
          echo "Checking generated APK files:"
          find app/build/outputs/apk/ -name "*.apk" -ls || ls -la app/build/outputs/

      - name: Upload Debug APK Artifact
        uses: actions/upload-artifact@v4
        with:
          name: NoTrack-IDE-Debug-APK
          path: app/build/outputs/apk/debug/*.apk
          retention-days: 30

      - name: Auto-Release APK (On Version Tag)
        if: startsWith(github.ref, 'refs/tags/v')
        uses: softprops/action-gh-release@v2
        with:
          files: app/build/outputs/apk/debug/*.apk
          generate_release_notes: true
        env:
          GITHUB_TOKEN: ${'$'}{{ secrets.GITHUB_TOKEN }}
        """.trimIndent()
    }
}
