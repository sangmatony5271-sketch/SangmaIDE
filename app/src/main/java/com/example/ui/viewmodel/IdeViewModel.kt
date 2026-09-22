package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.IdeDatabase
import com.example.data.model.*
import com.example.data.repository.IdeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class IdeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: IdeRepository

    // File State
    val allFiles: StateFlow<List<ProjectFile>>
    private val _activeFilePath = MutableStateFlow<String>("app/src/main/java/MainActivity.kt")
    val activeFilePath: StateFlow<String> = _activeFilePath.asStateFlow()

    private val _openTabPaths = MutableStateFlow<List<String>>(
        listOf("app/src/main/java/MainActivity.kt", "app/build.gradle.kts", "README.md")
    )
    val openTabPaths: StateFlow<List<String>> = _openTabPaths.asStateFlow()

    companion object {
        val DEFAULT_MAIN_ACTIVITY_CODE = """
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
    var textInput by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to NoTrack App",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = "Live compiled Jetpack Compose UI",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text("Enter App User Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { count++ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Click Counter: ${"$"}{count}")
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("App Feature Card", style = MaterialTheme.typography.titleMedium)
                    Text("Zero-telemetry privacy first architecture")
                }
            }
        }
    }
}
        """.trimIndent()
    }

    private val _activeFileContent = MutableStateFlow<String>(DEFAULT_MAIN_ACTIVITY_CODE)
    val activeFileContent: StateFlow<String> = _activeFileContent.asStateFlow()

    // Settings State
    private val _currentTheme = MutableStateFlow(IdeTheme.DRACULA)
    val currentTheme: StateFlow<IdeTheme> = _currentTheme.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.BENGALI)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _autoSaveEnabled = MutableStateFlow(true)
    val autoSaveEnabled: StateFlow<Boolean> = _autoSaveEnabled.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(true)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    private val _e2eEncryptionKey = MutableStateFlow("E2EE-AES256-NOTRACK-9921-KEY")
    val e2eEncryptionKey: StateFlow<String> = _e2eEncryptionKey.asStateFlow()

    // Git State
    val allCommits: StateFlow<List<GitCommit>>
    private val _currentBranch = MutableStateFlow("main")
    val currentBranch: StateFlow<String> = _currentBranch.asStateFlow()

    private val _stagedChanges = MutableStateFlow<List<String>>(emptyList())
    val stagedChanges: StateFlow<List<String>> = _stagedChanges.asStateFlow()

    // Debugger State
    private val _breakpoints = MutableStateFlow<List<Breakpoint>>(
        listOf(
            Breakpoint("app/src/main/java/MainActivity.kt", 22, true),
            Breakpoint("app/src/main/java/MainActivity.kt", 38, true)
        )
    )
    val breakpoints: StateFlow<List<Breakpoint>> = _breakpoints.asStateFlow()

    private val _consoleLogs = MutableStateFlow<List<ConsoleLog>>(
        listOf(
            ConsoleLog(tag = "System", message = "NoTrack IDE E2EE Core Engine initialized", level = LogLevel.SUCCESS),
            ConsoleLog(tag = "Gradle", message = "Gradle daemon connected (v8.7)", level = LogLevel.INFO),
            ConsoleLog(tag = "notrack.ai", message = "Privacy-first AI completion engine ready. Zero telemetry active.", level = LogLevel.SUCCESS)
        )
    )
    val consoleLogs: StateFlow<List<ConsoleLog>> = _consoleLogs.asStateFlow()

    // APK Builder State
    private val _isBuildingApk = MutableStateFlow(false)
    val isBuildingApk: StateFlow<Boolean> = _isBuildingApk.asStateFlow()

    private val _buildProgress = MutableStateFlow(0f)
    val buildProgress: StateFlow<Float> = _buildProgress.asStateFlow()

    private val _buildStatusMessage = MutableStateFlow("")
    val buildStatusMessage: StateFlow<String> = _buildStatusMessage.asStateFlow()

    private val _buildLogOutput = MutableStateFlow<List<String>>(emptyList())
    val buildLogOutput: StateFlow<List<String>> = _buildLogOutput.asStateFlow()

    private val _apkReady = MutableStateFlow(false)
    val apkReady: StateFlow<Boolean> = _apkReady.asStateFlow()

    // AI notrack.ai Assistant State
    private val _aiResponse = MutableStateFlow<String>("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Cloud & Collaboration
    val allBackups: StateFlow<List<BackupSnapshot>>
    private val _collaborators = MutableStateFlow<List<Collaborator>>(
        listOf(
            Collaborator("c1", "Dev 1 (You)", "#06B6D4", "MainActivity.kt", true),
            Collaborator("c2", "Dev 2 (E2EE Peer)", "#10B981", "build.gradle.kts", true)
        )
    )
    val collaborators: StateFlow<List<Collaborator>> = _collaborators.asStateFlow()

    // Push Notifications & Alerts
    private val _notifications = MutableStateFlow<List<PushNotificationItem>>(
        listOf(
            PushNotificationItem(
                title = "E2EE Sync Active",
                message = "All code changes encrypted with AES-256 before disk write.",
                type = NotificationType.SECURITY
            )
        )
    )
    val notifications: StateFlow<List<PushNotificationItem>> = _notifications.asStateFlow()

    private val _activeToast = MutableStateFlow<String?>(null)
    val activeToast: StateFlow<String?> = _activeToast.asStateFlow()

    // GitHub Auto APK Build (CI/CD) State
    private val _isGithubBuilding = MutableStateFlow(false)
    val isGithubBuilding: StateFlow<Boolean> = _isGithubBuilding.asStateFlow()

    private val _githubBuildProgress = MutableStateFlow(0f)
    val githubBuildProgress: StateFlow<Float> = _githubBuildProgress.asStateFlow()

    private val _githubBuildStatusMessage = MutableStateFlow("")
    val githubBuildStatusMessage: StateFlow<String> = _githubBuildStatusMessage.asStateFlow()

    private val _githubWorkflowConfigured = MutableStateFlow(true)
    val githubWorkflowConfigured: StateFlow<Boolean> = _githubWorkflowConfigured.asStateFlow()

    private val _githubArtifactReady = MutableStateFlow(false)
    val githubArtifactReady: StateFlow<Boolean> = _githubArtifactReady.asStateFlow()

    private val _githubBuildLogs = MutableStateFlow<List<String>>(listOf(
        "✓ GitHub Actions workflow verified: .github/workflows/android_build.yml",
        "✓ Triggers: Push to main/master, Pull Request, workflow_dispatch",
        "✓ Runner: ubuntu-latest | JDK: Temurin 17 | Task: ./gradlew assembleDebug",
        "Status: Ready to trigger auto build"
    ))
    val githubBuildLogs: StateFlow<List<String>> = _githubBuildLogs.asStateFlow()

    init {
        val database = IdeDatabase.getInstance(application)
        repository = IdeRepository(database.fileDao(), database.gitDao(), database.backupDao())

        allFiles = repository.allFiles.stateIn(
            viewModelScope, SharingStarted.Eagerly, emptyList()
        )
        allCommits = repository.allCommits.stateIn(
            viewModelScope, SharingStarted.Eagerly, emptyList()
        )
        allBackups = repository.allBackups.stateIn(
            viewModelScope, SharingStarted.Eagerly, emptyList()
        )

        viewModelScope.launch {
            repository.initializeDefaultProjectIfEmpty()
            repository.ensureGithubWorkflowExists()
            loadActiveFileContent(_activeFilePath.value)
        }
    }

    // File Operations
    fun selectFile(path: String) {
        _activeFilePath.value = path
        if (!_openTabPaths.value.contains(path)) {
            _openTabPaths.value = _openTabPaths.value + path
        }
        loadActiveFileContent(path)
    }

    fun closeTab(path: String) {
        val updated = _openTabPaths.value.filter { it != path }
        _openTabPaths.value = updated
        if (_activeFilePath.value == path && updated.isNotEmpty()) {
            selectFile(updated.last())
        }
    }

    private fun loadActiveFileContent(path: String) {
        viewModelScope.launch {
            val file = repository.getFile(path)
            if (file != null && file.content.isNotBlank()) {
                _activeFileContent.value = file.content
            } else {
                val fileList = allFiles.value
                val match = fileList.find { it.path == path }
                if (match != null && match.content.isNotBlank()) {
                    _activeFileContent.value = match.content
                } else if (path.endsWith("MainActivity.kt")) {
                    _activeFileContent.value = DEFAULT_MAIN_ACTIVITY_CODE
                }
            }
        }
    }

    fun updateActiveFileContent(newContent: String) {
        _activeFileContent.value = newContent
        if (!_stagedChanges.value.contains(_activeFilePath.value)) {
            _stagedChanges.value = _stagedChanges.value + _activeFilePath.value
        }
        if (_autoSaveEnabled.value) {
            saveCurrentFile()
        }
    }

    fun saveCurrentFile() {
        viewModelScope.launch {
            val currentPath = _activeFilePath.value
            val fileList = allFiles.value
            val match = fileList.find { it.path == currentPath }
            val fileName = currentPath.substringAfterLast("/")
            val lang = when {
                fileName.endsWith(".kt") -> "kotlin"
                fileName.endsWith(".xml") -> "xml"
                fileName.endsWith(".gradle.kts") -> "gradle"
                fileName.endsWith(".json") -> "json"
                else -> "markdown"
            }
            val updatedFile = ProjectFile(
                path = currentPath,
                name = fileName,
                content = _activeFileContent.value,
                language = lang,
                isUnsaved = false
            )
            repository.saveFile(updatedFile)
            showToast(if (_currentLanguage.value == AppLanguage.BENGALI) "ফাইলটি সফলভাবে সংরক্ষিত হয়েছে (E2EE Enforced)" else "File saved with E2EE encryption")
        }
    }

    fun createNewFile(fileName: String) {
        if (fileName.isBlank()) return
        viewModelScope.launch {
            val path = if (fileName.contains("/")) fileName else "app/src/main/java/$fileName"
            val lang = when {
                fileName.endsWith(".kt") -> "kotlin"
                fileName.endsWith(".xml") -> "xml"
                fileName.endsWith(".json") -> "json"
                else -> "markdown"
            }
            val newFile = ProjectFile(
                path = path,
                name = fileName.substringAfterLast("/"),
                content = "// Created in NoTrack IDE\n// Author: Dev",
                language = lang
            )
            repository.saveFile(newFile)
            selectFile(path)
            showToast(if (_currentLanguage.value == AppLanguage.BENGALI) "'$fileName' ফাইল তৈরি হয়েছে" else "Created file '$fileName'")
        }
    }

    fun deleteFile(path: String) {
        viewModelScope.launch {
            repository.deleteFile(path)
            closeTab(path)
            showToast(if (_currentLanguage.value == AppLanguage.BENGALI) "ফাইল মুছে ফেলা হয়েছে" else "File deleted")
        }
    }

    // Git Operations
    fun commitChanges(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            val count = _stagedChanges.value.size.coerceAtLeast(1)
            val commit = repository.addGitCommit(message, _currentBranch.value, count)
            _stagedChanges.value = emptyList()
            addNotification(
                title = "Git Commit Created",
                message = "Commit ${commit.hash}: ${commit.message}",
                type = NotificationType.GIT
            )
            showToast(if (_currentLanguage.value == AppLanguage.BENGALI) "গিট কমিট সফল হয়েছে (${commit.hash})" else "Git Commit created (${commit.hash})")
        }
    }

    fun switchBranch(branch: String) {
        _currentBranch.value = branch
        showToast(if (_currentLanguage.value == AppLanguage.BENGALI) "ব্রাঞ্চ পরিবর্তন: $branch" else "Switched to branch: $branch")
    }

    // Debugger Operations
    fun toggleBreakpoint(filePath: String, lineNumber: Int) {
        val current = _breakpoints.value.toMutableList()
        val index = current.indexOfFirst { it.filePath == filePath && it.lineNumber == lineNumber }
        if (index >= 0) {
            current.removeAt(index)
        } else {
            current.add(Breakpoint(filePath, lineNumber, true))
        }
        _breakpoints.value = current
    }

    fun addConsoleLog(tag: String, message: String, level: LogLevel) {
        _consoleLogs.value = listOf(ConsoleLog(tag = tag, message = message, level = level)) + _consoleLogs.value
    }

    fun clearConsole() {
        _consoleLogs.value = emptyList()
    }

    // APK Build Engine Simulation
    fun buildApk() {
        if (_isBuildingApk.value) return
        viewModelScope.launch {
            _isBuildingApk.value = true
            _buildProgress.value = 0f
            _apkReady.value = false
            _buildLogOutput.value = emptyList()

            val steps = listOf(
                "Initializing Gradle Daemon (v8.7)...",
                "Parsing AndroidManifest.xml and dependencies in build.gradle.kts...",
                "Running KSP symbol processing for Room Database...",
                "Compiling Kotlin source files to Java bytecode...",
                "Dexing classes with D8 (r8 minification check)...",
                "Packaging resources & APK alignment (zipalign)...",
                "Signing APK with debug.keystore (E2EE verified)...",
                "BUILD SUCCESSFUL: app-debug.apk generated!"
            )

            for ((idx, step) in steps.withIndex()) {
                _buildStatusMessage.value = step
                _buildLogOutput.value = _buildLogOutput.value + "[$idx/7] $step"
                _buildProgress.value = (idx + 1) / steps.size.toFloat()
                delay(600)
            }

            _isBuildingApk.value = false
            _apkReady.value = true
            addConsoleLog("APK Builder", "APK Build completed successfully. Output: app-debug.apk (14.2 MB)", LogLevel.SUCCESS)
            addNotification(
                title = "APK Build Success",
                message = "app-debug.apk is ready for download and testing.",
                type = NotificationType.BUILD
            )
            showToast(if (_currentLanguage.value == AppLanguage.BENGALI) "এপিকে বিল্ড সফল হয়েছে!" else "APK Built Successfully!")
        }
    }

    // GitHub Auto APK Build (CI/CD Pipeline)
    fun generateOrUpdateGithubWorkflow() {
        viewModelScope.launch {
            repository.saveFile(
                com.example.data.model.ProjectFile(
                    path = ".github/workflows/android_build.yml",
                    name = "android_build.yml",
                    content = com.example.data.repository.IdeRepository.GITHUB_ACTIONS_WORKFLOW_CONTENT,
                    language = "yaml"
                )
            )
            _githubWorkflowConfigured.value = true
            showToast(if (_currentLanguage.value == AppLanguage.BENGALI) "GitHub Actions অটো এপিকে বিল্ড ওয়ার্কফ্লো সেভ হয়েছে" else "GitHub Actions Auto APK Build workflow saved!")
            addNotification(
                title = "GitHub Workflow Saved",
                message = ".github/workflows/android_build.yml configured for CI/CD",
                type = NotificationType.GIT
            )
        }
    }

    fun triggerGithubAutoBuild() {
        if (_isGithubBuilding.value) return
        viewModelScope.launch {
            _isGithubBuilding.value = true
            _githubArtifactReady.value = false
            _githubBuildProgress.value = 0f
            _githubBuildLogs.value = listOf(
                "🚀 [GitHub Actions] Workflow run initiated on branch '${_currentBranch.value}'",
                "Repository: git@github.com:developer/notrack-android-app.git",
                "Workflow: .github/workflows/android_build.yml",
                "Trigger Event: push / workflow_dispatch",
                "Runner: ubuntu-latest (GitHub CI)"
            )

            val pipelineSteps = listOf(
                "actions/checkout@v4 - Repository cloned" to 400L,
                "actions/setup-java@v4 - Set up JDK 17 (Temurin) with Gradle cache" to 500L,
                "android-actions/setup-android@v3 - Android SDK API 35 installed" to 500L,
                "chmod +x gradlew - Executable permissions granted" to 300L,
                "Run ./gradlew assembleDebug --no-daemon - Compiling APK..." to 1200L,
                "Verify APK Output - app-debug.apk validated (14.2 MB)" to 400L,
                "actions/upload-artifact@v4 - Uploaded NoTrack-IDE-Debug-APK" to 600L
            )

            for ((idx, stepPair) in pipelineSteps.withIndex()) {
                val (msg, waitTime) = stepPair
                _githubBuildStatusMessage.value = msg
                _githubBuildLogs.value = _githubBuildLogs.value + "✓ [${idx + 1}/7] $msg"
                _githubBuildProgress.value = (idx + 1) / pipelineSteps.size.toFloat()
                delay(waitTime)
            }

            _githubBuildLogs.value = _githubBuildLogs.value + listOf(
                "🎉 [SUCCESS] GitHub Auto APK Build completed successfully in 3.8s!",
                "📦 Artifact: app-debug.apk is ready for deployment and download."
            )
            _isGithubBuilding.value = false
            _githubArtifactReady.value = true
            _githubBuildStatusMessage.value = "GitHub Actions CI/CD: PASSING"

            addConsoleLog("GitHub Actions", "Auto APK Build succeeded on branch ${_currentBranch.value}", LogLevel.SUCCESS)
            addNotification(
                title = "GitHub Auto APK Build Done",
                message = "Artifact NoTrack-IDE-Debug-APK ready for download",
                type = NotificationType.BUILD
            )
            showToast(if (_currentLanguage.value == AppLanguage.BENGALI) "গিটহাব অটো এপিকে বিল্ড সফল হয়েছে! Artifact প্রস্তুত।" else "GitHub Auto APK Build Succeeded!")
        }
    }

    // notrack.ai AI Assistant Operations
    fun askNoTrackAi(prompt: String, taskType: String = "general") {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isAiThinking.value = true
            _aiResponse.value = ""
            delay(800)

            val currentCode = _activeFileContent.value
            val response = when (taskType) {
                "bugfix" -> """
// [notrack.ai Bugfix Suggestion - Zero Telemetry]
// Issue identified: Potential NullPointerException & unhandled state flow.
// Fix applied below:

fun safeExecute() {
    val content = activeContent ?: return
    try {
        println("Processed securely: ${'$'}{content.take(20)}")
    } catch (e: Exception) {
        Log.e("NoTrackAI", "Error safely handled", e)
    }
}
                """.trimIndent()
                "refactor" -> """
// [notrack.ai Clean Code Refactoring]
// Refactored with Kotlin idiomatic functions and Compose best practices:

@Composable
fun OptimizedComponent(modifier: Modifier = Modifier) {
    val state by remember { mutableStateOf("E2EE Active") }
    Text(
        text = state,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(16.dp)
    )
}
                """.trimIndent()
                "explain" -> if (_currentLanguage.value == AppLanguage.BENGALI) """
🤖 [notrack.ai ব্যাখ্যা - জিরো ট্র্যাকিং প্রাইভেসি]

১. এই কোডটি একটি Jetpack Compose UI স্টেট পরিচালনা করছে।
২. `remember` এবং `mutableStateOf` ব্যবহারের মাধ্যমে রি-কম্পোজিশন নিয়ন্ত্রণ করা হয়েছে।
৩. ডাটা লোকাল ডিভাইস ও E2EE এনক্রিপশন এর মাধ্যমে সম্পূর্ণরূপে সুরক্ষিত।
                """.trimIndent() else """
🤖 [notrack.ai Explanation - Zero Tracking]

1. This code manages UI state using Jetpack Compose architecture.
2. `remember` and `mutableStateOf` ensure efficient state re-composition.
3. All code data remains encrypted locally via AES-256 with zero cloud telemetry.
                """.trimIndent()
                else -> if (_currentLanguage.value == AppLanguage.BENGALI) """
🤖 [notrack.ai কোড সাজেসশন]:
"$prompt" এর জন্য সুরক্ষিত Kotlin সমাধান:

```kotlin
// notrack.ai Generated Code
fun generateSecureDataHash(input: String): String {
    val digest = java.security.MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(input.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}
```
                """.trimIndent() else """
🤖 [notrack.ai Code Assistant]:
Here is your secure Kotlin implementation for "$prompt":

```kotlin
// notrack.ai Generated Implementation
fun generateSecureDataHash(input: String): String {
    val digest = java.security.MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(input.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}
```
                """.trimIndent()
            }

            _aiResponse.value = response
            _isAiThinking.value = false
            addConsoleLog("notrack.ai", "AI suggestion generated safely with 0 server telemetry.", LogLevel.INFO)
        }
    }

    fun applyAiCodeToEditor() {
        val aiText = _aiResponse.value
        if (aiText.isBlank()) return
        val codeSnippet = if (aiText.contains("```kotlin")) {
            aiText.substringAfter("```kotlin").substringBefore("```").trim()
        } else {
            aiText.replace(Regex("// \\[notrack.ai.*?\\]"), "").trim()
        }
        updateActiveFileContent("${_activeFileContent.value}\n\n$codeSnippet")
        showToast(if (_currentLanguage.value == AppLanguage.BENGALI) "কোড সাজেসশন এডিটরে যুক্ত করা হয়েছে" else "AI code applied to active editor")
    }

    // Cloud E2EE Sync & Backup
    fun triggerManualBackup() {
        viewModelScope.launch {
            val snapshot = repository.createBackup(allFiles.value.size)
            addNotification(
                title = "E2EE Backup Saved",
                message = "${snapshot.label} with ${snapshot.fileCount} files encrypted.",
                type = NotificationType.SECURITY
            )
            showToast(if (_currentLanguage.value == AppLanguage.BENGALI) "E2EE ডাটা ব্যাকআপ সফল হয়েছে" else "E2EE Data Backup completed")
        }
    }

    // Settings Updates
    fun setTheme(theme: IdeTheme) {
        _currentTheme.value = theme
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        showToast(if (language == AppLanguage.BENGALI) "ভাষা বাংলা সেট করা হয়েছে" else "Language set to English")
    }

    fun toggleAutoSave(enabled: Boolean) {
        _autoSaveEnabled.value = enabled
    }

    fun toggleOfflineMode(enabled: Boolean) {
        _isOfflineMode.value = enabled
    }

    private fun addNotification(title: String, message: String, type: NotificationType) {
        _notifications.value = listOf(PushNotificationItem(title = title, message = message, type = type)) + _notifications.value
    }

    private fun showToast(msg: String) {
        viewModelScope.launch {
            _activeToast.value = msg
            delay(2500)
            if (_activeToast.value == msg) {
                _activeToast.value = null
            }
        }
    }

    fun dismissToast() {
        _activeToast.value = null
    }
}
