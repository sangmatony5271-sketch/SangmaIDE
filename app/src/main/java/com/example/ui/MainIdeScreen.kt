package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.ConsoleLog
import com.example.data.model.LogLevel
import com.example.data.model.ProjectFile
import com.example.ui.components.*
import com.example.data.model.IdeTheme
import com.example.ui.theme.NoTrackIdeTheme
import com.example.ui.viewmodel.IdeViewModel

enum class IdeTab(val titleEn: String, val titleBn: String, val icon: ImageVector) {
    PREVIEW("Live Preview", "অ্যাপ প্রিভিউ", Icons.Default.PhoneAndroid),
    APK_BUILDER("APK Builder", "এপিকে বিল্ডার", Icons.Default.Build),
    EDITOR("Editor", "কোড এডিটর", Icons.Default.Code),
    FILES("Files", "ফাইল", Icons.Default.Folder),
    GIT("Git", "গিট", Icons.Default.AccountTree),
    DEBUGGER("Debugger", "ডিবাগার", Icons.Default.BugReport),
    NOTRACK_AI("notrack.ai", "notrack.ai", Icons.Default.AutoAwesome),
    SECURITY_SYNC("E2EE Sync", "ই২ইই সিঙ্ক", Icons.Default.Security),
    SETTINGS("Settings", "সেটিংস", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainIdeScreen(viewModel: IdeViewModel? = null) {
    // Collect from viewModel when available; fall back to interactive preview state in Android Studio Preview
    var previewActivePath by remember { mutableStateOf("app/src/main/java/MainActivity.kt") }
    var previewActiveContent by remember { mutableStateOf(IdeViewModel.DEFAULT_MAIN_ACTIVITY_CODE) }

    val activeFilePath = viewModel?.activeFilePath?.collectAsState()?.value ?: previewActivePath
    val activeFileContent = viewModel?.activeFileContent?.collectAsState()?.value ?: previewActiveContent
    val allFiles = viewModel?.allFiles?.collectAsState()?.value ?: listOf(
        ProjectFile("app/src/main/java/MainActivity.kt", "MainActivity.kt", IdeViewModel.DEFAULT_MAIN_ACTIVITY_CODE, "kotlin"),
        ProjectFile(".github/workflows/android_build.yml", "android_build.yml", "// github actions", "yaml"),
        ProjectFile("app/build.gradle.kts", "build.gradle.kts", "// build.gradle", "kotlin"),
        ProjectFile("README.md", "README.md", "# NoTrack IDE Project", "markdown")
    )
    val currentTheme = viewModel?.currentTheme?.collectAsState()?.value ?: IdeTheme.DRACULA
    val currentLanguage = viewModel?.currentLanguage?.collectAsState()?.value ?: AppLanguage.BENGALI
    val autoSaveEnabled = viewModel?.autoSaveEnabled?.collectAsState()?.value ?: true
    val isOfflineMode = viewModel?.isOfflineMode?.collectAsState()?.value ?: true
    val e2eKey = viewModel?.e2eEncryptionKey?.collectAsState()?.value ?: "E2EE-AES256-NOTRACK-9921-KEY"
    val currentBranch = viewModel?.currentBranch?.collectAsState()?.value ?: "main"
    val stagedChanges = viewModel?.stagedChanges?.collectAsState()?.value ?: emptyList()
    val allCommits = viewModel?.allCommits?.collectAsState()?.value ?: emptyList()
    val breakpoints = viewModel?.breakpoints?.collectAsState()?.value ?: emptyList()
    val consoleLogs = viewModel?.consoleLogs?.collectAsState()?.value ?: listOf(
        ConsoleLog(tag = "System", message = "NoTrack IDE E2EE Core Engine initialized", level = LogLevel.SUCCESS),
        ConsoleLog(tag = "Gradle", message = "Gradle daemon connected (v8.7)", level = LogLevel.INFO),
        ConsoleLog(tag = "GitHub CI", message = "GitHub Auto APK workflow active", level = LogLevel.SUCCESS),
        ConsoleLog(tag = "notrack.ai", message = "Privacy-first AI completion ready", level = LogLevel.SUCCESS)
    )
    val isBuildingApk = viewModel?.isBuildingApk?.collectAsState()?.value ?: false
    val buildProgress = viewModel?.buildProgress?.collectAsState()?.value ?: 0f
    val buildStatusMessage = viewModel?.buildStatusMessage?.collectAsState()?.value ?: ""
    val buildLogOutput = viewModel?.buildLogOutput?.collectAsState()?.value ?: listOf(
        "> Task :app:preBuild UP-TO-DATE",
        "> Task :app:compileDebugKotlin UP-TO-DATE",
        "BUILD SUCCESSFUL in 1.2s"
    )
    val apkReady = viewModel?.apkReady?.collectAsState()?.value ?: false

    // GitHub Auto APK Build State
    val isGithubBuilding = viewModel?.isGithubBuilding?.collectAsState()?.value ?: false
    val githubProgress = viewModel?.githubBuildProgress?.collectAsState()?.value ?: 0f
    val githubStatusMessage = viewModel?.githubBuildStatusMessage?.collectAsState()?.value ?: ""
    val githubLogs = viewModel?.githubBuildLogs?.collectAsState()?.value ?: listOf(
        "✓ GitHub Actions workflow verified: .github/workflows/android_build.yml",
        "✓ Triggers: Push to main/master, Pull Request, workflow_dispatch",
        "✓ Runner: ubuntu-latest | JDK: Temurin 17 | Task: ./gradlew assembleDebug",
        "Status: Ready to trigger auto build"
    )
    val isGithubArtifactReady = viewModel?.githubArtifactReady?.collectAsState()?.value ?: false

    val aiResponse = viewModel?.aiResponse?.collectAsState()?.value ?: ""
    val isAiThinking = viewModel?.isAiThinking?.collectAsState()?.value ?: false
    val collaborators = viewModel?.collaborators?.collectAsState()?.value ?: emptyList()
    val backups = viewModel?.allBackups?.collectAsState()?.value ?: emptyList()
    val toastMessage = viewModel?.activeToast?.collectAsState()?.value

    // Start with PREVIEW tab selected so the live Android App Preview displays immediately
    var activeTab by remember { mutableStateOf(IdeTab.PREVIEW) }
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 600

    NoTrackIdeTheme(ideTheme = currentTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "NoTrack IDE",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.BENGALI) "GitHub CI/CD অটো এপিকে বিল্ড সহ E2EE আইডিই" else "Privacy-First E2EE Android IDE with GitHub CI/CD",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (isOfflineMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOfflineMode) "OFFLINE E2EE" else "ONLINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        IdeTab.values().take(5).forEach { tab ->
                            val isSelected = activeTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { activeTab = tab },
                                icon = { Icon(tab.icon, contentDescription = tab.titleEn) },
                                label = { Text(if (currentLanguage == AppLanguage.BENGALI) tab.titleBn else tab.titleEn, fontSize = 10.sp) },
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    // Desktop / Tablet Split View Layout
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Navigation Rail
                        NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                            IdeTab.values().forEach { tab ->
                                NavigationRailItem(
                                    selected = activeTab == tab,
                                    onClick = { activeTab = tab },
                                    icon = { Icon(tab.icon, contentDescription = tab.titleEn) },
                                    label = { Text(if (currentLanguage == AppLanguage.BENGALI) tab.titleBn else tab.titleEn, fontSize = 10.sp) }
                                )
                            }
                        }

                        // Left Explorer Panel
                        Box(
                            modifier = Modifier
                                .width(240.dp)
                                .fillMaxHeight()
                        ) {
                            FileTreeDrawer(
                                files = allFiles,
                                activeFilePath = activeFilePath,
                                onSelectFile = { path ->
                                    if (viewModel != null) {
                                        viewModel.selectFile(path)
                                    } else {
                                        previewActivePath = path
                                    }
                                },
                                onCreateFile = { viewModel?.createNewFile(it) },
                                onDeleteFile = { viewModel?.deleteFile(it) },
                                language = currentLanguage
                            )
                        }

                        VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        // Center Main Panel (Editor)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            CodeEditorView(
                                filePath = activeFilePath,
                                content = activeFileContent,
                                onContentChange = { newContent ->
                                    if (viewModel != null) {
                                        viewModel.updateActiveFileContent(newContent)
                                    } else {
                                        previewActiveContent = newContent
                                    }
                                },
                                onSave = { viewModel?.saveCurrentFile() },
                                breakpoints = breakpoints,
                                onToggleBreakpoint = { file, line -> viewModel?.toggleBreakpoint(file, line) },
                                language = currentLanguage,
                                onQuickAiPrompt = { viewModel?.askNoTrackAi(it) }
                            )
                        }

                        VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        // Right Secondary Panel (Live Preview or active tool)
                        Box(
                            modifier = Modifier
                                .width(380.dp)
                                .fillMaxHeight()
                        ) {
                            when (activeTab) {
                                IdeTab.NOTRACK_AI -> NoTrackAiPanel(
                                    aiResponse = aiResponse,
                                    isThinking = isAiThinking,
                                    onAskAi = { prompt, type -> viewModel?.askNoTrackAi(prompt, type) },
                                    onApplyCode = { viewModel?.applyAiCodeToEditor() },
                                    language = currentLanguage
                                )
                                IdeTab.GIT -> GitPanel(
                                    currentBranch = currentBranch,
                                    onBranchChange = { viewModel?.switchBranch(it) },
                                    stagedFiles = stagedChanges,
                                    commits = allCommits,
                                    onCommit = { viewModel?.commitChanges(it) },
                                    language = currentLanguage,
                                    onTriggerGithubBuild = { viewModel?.triggerGithubAutoBuild() }
                                )
                                IdeTab.DEBUGGER -> DebuggerPanel(
                                    breakpoints = breakpoints,
                                    onToggleBreakpoint = { file, line -> viewModel?.toggleBreakpoint(file, line) },
                                    consoleLogs = consoleLogs,
                                    onClearConsole = { viewModel?.clearConsole() },
                                    language = currentLanguage
                                )
                                IdeTab.APK_BUILDER -> ApkBuilderPanel(
                                    isBuilding = isBuildingApk,
                                    progress = buildProgress,
                                    statusMessage = buildStatusMessage,
                                    logs = buildLogOutput,
                                    isApkReady = apkReady,
                                    onStartBuild = { viewModel?.buildApk() },
                                    language = currentLanguage,
                                    activeFileContent = activeFileContent,
                                    onUpdateCode = { newContent ->
                                        if (viewModel != null) {
                                            viewModel.updateActiveFileContent(newContent)
                                        } else {
                                            previewActiveContent = newContent
                                        }
                                    },
                                    isGithubBuilding = isGithubBuilding,
                                    githubProgress = githubProgress,
                                    githubStatusMessage = githubStatusMessage,
                                    githubLogs = githubLogs,
                                    isGithubArtifactReady = isGithubArtifactReady,
                                    onTriggerGithubBuild = { viewModel?.triggerGithubAutoBuild() },
                                    onSaveGithubWorkflow = { viewModel?.generateOrUpdateGithubWorkflow() },
                                    onFixSdkSetup = { viewModel?.applySdkSetupFix() }
                                )
                                else -> LivePreviewView(
                                    activeFileContent = activeFileContent,
                                    language = currentLanguage
                                )
                            }
                        }
                    }
                } else {
                    // Mobile View Layout
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Quick Action Top Bar Tabs for Mobile
                        ScrollableTabRow(
                            selectedTabIndex = activeTab.ordinal,
                            edgePadding = 8.dp,
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            IdeTab.values().forEach { tab ->
                                Tab(
                                    selected = activeTab == tab,
                                    onClick = { activeTab = tab },
                                    text = { Text(if (currentLanguage == AppLanguage.BENGALI) tab.titleBn else tab.titleEn, fontSize = 11.sp) },
                                    icon = { Icon(tab.icon, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            when (activeTab) {
                                IdeTab.PREVIEW -> LivePreviewView(
                                    activeFileContent = activeFileContent,
                                    language = currentLanguage
                                )
                                IdeTab.APK_BUILDER -> ApkBuilderPanel(
                                    isBuilding = isBuildingApk,
                                    progress = buildProgress,
                                    statusMessage = buildStatusMessage,
                                    logs = buildLogOutput,
                                    isApkReady = apkReady,
                                    onStartBuild = { viewModel?.buildApk() },
                                    language = currentLanguage,
                                    activeFileContent = activeFileContent,
                                    onUpdateCode = { newContent ->
                                        if (viewModel != null) {
                                            viewModel.updateActiveFileContent(newContent)
                                        } else {
                                            previewActiveContent = newContent
                                        }
                                    },
                                    isGithubBuilding = isGithubBuilding,
                                    githubProgress = githubProgress,
                                    githubStatusMessage = githubStatusMessage,
                                    githubLogs = githubLogs,
                                    isGithubArtifactReady = isGithubArtifactReady,
                                    onTriggerGithubBuild = { viewModel?.triggerGithubAutoBuild() },
                                    onSaveGithubWorkflow = { viewModel?.generateOrUpdateGithubWorkflow() },
                                    onFixSdkSetup = { viewModel?.applySdkSetupFix() }
                                )
                                IdeTab.EDITOR -> Column(modifier = Modifier.fillMaxSize()) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        CodeEditorView(
                                            filePath = activeFilePath,
                                            content = activeFileContent,
                                            onContentChange = { newContent ->
                                                if (viewModel != null) {
                                                    viewModel.updateActiveFileContent(newContent)
                                                } else {
                                                    previewActiveContent = newContent
                                                }
                                            },
                                            onSave = { viewModel?.saveCurrentFile() },
                                            breakpoints = breakpoints,
                                            onToggleBreakpoint = { file, line -> viewModel?.toggleBreakpoint(file, line) },
                                            language = currentLanguage,
                                            onQuickAiPrompt = {
                                                viewModel?.askNoTrackAi(it)
                                                activeTab = IdeTab.NOTRACK_AI
                                            }
                                        )
                                    }
                                }
                                IdeTab.FILES -> FileTreeDrawer(
                                    files = allFiles,
                                    activeFilePath = activeFilePath,
                                    onSelectFile = { path ->
                                        if (viewModel != null) {
                                            viewModel.selectFile(path)
                                        } else {
                                            previewActivePath = path
                                        }
                                        activeTab = IdeTab.EDITOR
                                    },
                                    onCreateFile = { viewModel?.createNewFile(it) },
                                    onDeleteFile = { viewModel?.deleteFile(it) },
                                    language = currentLanguage
                                )
                                IdeTab.GIT -> GitPanel(
                                    currentBranch = currentBranch,
                                    onBranchChange = { viewModel?.switchBranch(it) },
                                    stagedFiles = stagedChanges,
                                    commits = allCommits,
                                    onCommit = { viewModel?.commitChanges(it) },
                                    language = currentLanguage,
                                    onTriggerGithubBuild = { viewModel?.triggerGithubAutoBuild() }
                                )
                                IdeTab.DEBUGGER -> DebuggerPanel(
                                    breakpoints = breakpoints,
                                    onToggleBreakpoint = { file, line -> viewModel?.toggleBreakpoint(file, line) },
                                    consoleLogs = consoleLogs,
                                    onClearConsole = { viewModel?.clearConsole() },
                                    language = currentLanguage
                                )
                                IdeTab.NOTRACK_AI -> NoTrackAiPanel(
                                    aiResponse = aiResponse,
                                    isThinking = isAiThinking,
                                    onAskAi = { prompt, type -> viewModel?.askNoTrackAi(prompt, type) },
                                    onApplyCode = { viewModel?.applyAiCodeToEditor() },
                                    language = currentLanguage
                                )
                                IdeTab.SECURITY_SYNC -> SecuritySyncPanel(
                                    e2eKey = e2eKey,
                                    collaborators = collaborators,
                                    backups = backups,
                                    onCreateBackup = { viewModel?.triggerManualBackup() },
                                    language = currentLanguage
                                )
                                IdeTab.SETTINGS -> SettingsPanel(
                                    currentTheme = currentTheme,
                                    onSelectTheme = { viewModel?.setTheme(it) },
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = { viewModel?.setLanguage(it) },
                                    autoSaveEnabled = autoSaveEnabled,
                                    onToggleAutoSave = { viewModel?.toggleAutoSave(it) },
                                    isOfflineMode = isOfflineMode,
                                    onToggleOfflineMode = { viewModel?.toggleOfflineMode(it) },
                                    language = currentLanguage
                                )
                            }
                        }
                    }
                }

                NotificationToastOverlay(
                    toastMessage = toastMessage,
                    onDismiss = { viewModel?.dismissToast() }
                )
            }
        }
    }
}

@Preview(name = "1. Live App Preview", showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun MainIdeScreenPhonePreview() {
    MainIdeScreen()
}

@Preview(name = "2. NoTrack IDE Tablet Preview", showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun MainIdeScreenTabletPreview() {
    MainIdeScreen()
}
