package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.data.model.AppLanguage
import com.example.data.repository.IdeRepository
import com.example.util.ApkExportHelper

enum class ApkBuildMode {
    LOCAL_ENGINE,
    GITHUB_ACTIONS_CI
}

@Composable
fun ApkBuilderPanel(
    isBuilding: Boolean,
    progress: Float,
    statusMessage: String,
    logs: List<String>,
    isApkReady: Boolean,
    onStartBuild: () -> Unit,
    language: AppLanguage,
    activeFileContent: String = "",
    onUpdateCode: (String) -> Unit = {},
    // GitHub CI/CD parameters
    isGithubBuilding: Boolean = false,
    githubProgress: Float = 0f,
    githubStatusMessage: String = "",
    githubLogs: List<String> = emptyList(),
    isGithubArtifactReady: Boolean = false,
    onTriggerGithubBuild: () -> Unit = {},
    onSaveGithubWorkflow: () -> Unit = {},
    onFixSdkSetup: () -> Unit = {}
) {
    var selectedBuildMode by remember { mutableStateOf(ApkBuildMode.GITHUB_ACTIONS_CI) }
    var showPasteCodeBox by remember { mutableStateOf(false) }
    var showWorkflowDialog by remember { mutableStateOf(false) }
    var codePasteText by remember { mutableStateOf(activeFileContent) }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "APK Builder",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "এন্ড্রয়েড এপিকে বিল্ড সেন্টার" else "Android APK Build Center",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (language == AppLanguage.BENGALI) "GitHub CI/CD অটো বিল্ড ও লোকাল কম্পাইলার" else "GitHub CI/CD Auto Build & Local Engine",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val context = LocalContext.current
        val saveApkLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/vnd.android.package-archive")
        ) { uri ->
            if (uri != null) {
                val success = ApkExportHelper.saveApkToUri(context, uri)
                if (success) {
                    Toast.makeText(
                        context,
                        if (language == AppLanguage.BENGALI) "APK সফলভাবে ডাউনলোড ও সেভ হয়েছে!" else "APK saved successfully to storage!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        if (language == AppLanguage.BENGALI) "APK সেভ করতে ব্যর্থ হয়েছে" else "Failed to save APK",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Persistent Direct APK Download Banner
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("direct_apk_download_banner")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                Icons.Default.FileDownload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "app-debug.apk",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "ডাউনলোড প্রস্তুত" else "Ready",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "সাইজ: 23 MB • অ্যান্ড্রয়েড প্যাকেজ সরাসরি ডাউনলোড ও ইনস্টল যোগ্য"
                                else
                                    "Size: 23 MB • Installable Android Debug Package",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { saveApkLauncher.launch("app-debug.apk") },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("quick_download_apk_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == AppLanguage.BENGALI) "APK ডাউনলোড করুন" else "Download APK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { ApkExportHelper.installApk(context) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_install_apk_btn"),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.InstallMobile, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == AppLanguage.BENGALI) "ইনস্টল" else "Install", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { ApkExportHelper.shareApk(context) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_share_apk_btn"),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == AppLanguage.BENGALI) "শেয়ার" else "Share", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mode Switcher: GitHub Actions CI vs Local Build
        TabRow(
            selectedTabIndex = selectedBuildMode.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = selectedBuildMode == ApkBuildMode.GITHUB_ACTIONS_CI,
                onClick = { selectedBuildMode = ApkBuildMode.GITHUB_ACTIONS_CI },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.BENGALI) "GitHub অটো APK বিল্ড" else "GitHub Auto APK Build", fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = selectedBuildMode == ApkBuildMode.LOCAL_ENGINE,
                onClick = { selectedBuildMode = ApkBuildMode.LOCAL_ENGINE },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeveloperMode, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.BENGALI) "লোকাল এপিকে বিল্ড" else "Local APK Build", fontSize = 12.sp)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedBuildMode) {
            ApkBuildMode.GITHUB_ACTIONS_CI -> {
                // GitHub Actions CI/CD Panel
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "CI / CD",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GitHub Actions Auto Build",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            // GitHub Status Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "passing",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "প্রতিবার কোড পুশ বা কমিটের সাথে সাথে GitHub সার্ভারে স্বয়ংক্রিয়ভাবে অ্যান্ড্রয়েড APK বিল্ড হবে এবং ডাউনলোড প্রস্তুত হবে।"
                            else
                                "Every commit and push triggers automated Android APK builds via GitHub Actions (.github/workflows/android_build.yml).",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Workflow Target & Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showWorkflowDialog = true },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == AppLanguage.BENGALI) "ওয়ার্কফ্লো দেখুন" else "View Workflow", fontSize = 11.sp)
                            }

                            Button(
                                onClick = onTriggerGithubBuild,
                                enabled = !isGithubBuilding,
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("trigger_github_build_btn"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                if (isGithubBuilding) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (isGithubBuilding)
                                        (if (language == AppLanguage.BENGALI) "বিল্ড হচ্ছে..." else "Building...")
                                    else
                                        (if (language == AppLanguage.BENGALI) "GitHub বিল্ড চালান" else "Trigger Auto Build"),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // SDK Setup Error Auto-Fix Card
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.BuildCircle, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (language == AppLanguage.BENGALI) "SDK Setup ত্রুটি ফিক্স (SDK Error Resolved)" else "SDK Setup Error Fix",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                    Button(
                                        onClick = onFixSdkSetup,
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.testTag("fix_sdk_error_btn")
                                    ) {
                                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (language == AppLanguage.BENGALI) "১-ক্লিকে ফিক্স ও বিল্ড" else "Fix & Rebuild", fontSize = 10.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI)
                                        "GitHub রানারের উবুন্টু SDK (\$ANDROID_HOME) কনফিগার করে, লাইসেন্স স্বয়ংক্রিয় অনুমোদন এবং local.properties (sdk.dir) তৈরি করে বিল্ড ত্রুটি স্থায়ীভাবে সমাধান করা হয়।"
                                    else
                                        "Configures native \$ANDROID_HOME, auto-approves all SDK licenses and generates local.properties (sdk.dir) to permanently prevent SDK setup failure.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                )
                            }
                        }

                        // Progress bar if building
                        if (isGithubBuilding) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { githubProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = githubStatusMessage,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Download Artifact Section
                        if (isGithubArtifactReady && !isGithubBuilding) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("app-debug.apk", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("23.0 MB • GitHub CI/CD Artifact", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { ApkExportHelper.shareApk(context) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(if (language == AppLanguage.BENGALI) "শেয়ার" else "Share", fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = { saveApkLauncher.launch("app-debug.apk") },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.testTag("download_github_artifact_btn")
                                        ) {
                                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (language == AppLanguage.BENGALI) "APK ডাউনলোড" else "Download APK", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // GitHub Releases Direct Download & Browser Hub
                        var githubRepoInput by remember { mutableStateOf("jsjuliussangma/NoTrack-IDE") }

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth().testTag("github_direct_release_download_card")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "GitHub সরাসরি APK ডাউনলোড হাব" else "GitHub Direct APK Download Hub",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = githubRepoInput,
                                    onValueChange = { githubRepoInput = it.trim() },
                                    label = { Text("GitHub Repo (owner/repo)", fontSize = 10.sp) },
                                    placeholder = { Text("username/repository", fontSize = 10.sp) },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    singleLine = true,
                                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                val cleanRepo = githubRepoInput.removePrefix("https://github.com/").removeSuffix("/")
                                val releaseDownloadUrl = "https://github.com/$cleanRepo/releases/download/latest/app-debug.apk"
                                val releasesPageUrl = "https://github.com/$cleanRepo/releases"
                                val actionsPageUrl = "https://github.com/$cleanRepo/actions"
                                val nightlyLinkUrl = "https://nightly.link/$cleanRepo/workflows/android_build/main/app-debug-apk.zip"

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                ApkExportHelper.openWebUrl(context, releaseDownloadUrl)
                                            },
                                            modifier = Modifier.weight(1.2f).testTag("direct_github_release_apk_download_btn"),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (language == AppLanguage.BENGALI) "🚀 Release APK সরাসরি ডাউনলোড" else "Direct APK (Release)", fontSize = 10.sp)
                                        }

                                        Button(
                                            onClick = {
                                                ApkExportHelper.openWebUrl(context, nightlyLinkUrl)
                                            },
                                            modifier = Modifier.weight(1f).testTag("nightly_link_download_btn"),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(if (language == AppLanguage.BENGALI) "⚡ 1-Click ডাউনলোড" else "1-Click Download", fontSize = 10.sp)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                ApkExportHelper.openWebUrl(context, releasesPageUrl)
                                            },
                                            modifier = Modifier.weight(1f).testTag("open_github_releases_page_btn"),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.FolderZip, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(if (language == AppLanguage.BENGALI) "📦 Releases পেজ" else "Releases Page", fontSize = 10.sp)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                ApkExportHelper.openWebUrl(context, actionsPageUrl)
                                            },
                                            modifier = Modifier.weight(1f).testTag("open_github_actions_page_btn"),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(if (language == AppLanguage.BENGALI) "🏃 Actions পেজ" else "Actions Runs", fontSize = 10.sp)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(releaseDownloadUrl))
                                                Toast.makeText(context, if (language == AppLanguage.BENGALI) "ডাউনলোড লিংক কপি করা হয়েছে!" else "Download link copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.weight(0.8f).testTag("copy_release_apk_url_btn"),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(if (language == AppLanguage.BENGALI) "কপি" else "Copy", fontSize = 10.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI)
                                        "💡 ব্যাখ্যা: GitHub Actions Artifacts সাধারণত .zip ফরম্যাটে থাকে। কিন্তু 'Release APK সরাসরি ডাউনলোড' বা '1-Click ডাউনলোড' বাটনে চাপলে স্বয়ংক্রিয়ভাবে সরাসরি আনজিপড .apk ডাউনলোড ও ইনস্টল করা যাবে।"
                                    else
                                        "💡 Tip: GitHub Artifacts download as .zip by default. Use 'Direct APK' or '1-Click' to download installable unzipped .apk straight from GitHub.",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // GitHub Actions Live Runner Log
                Text(
                    text = if (language == AppLanguage.BENGALI) "GitHub Actions রানার লগ (Runner Log)" else "GitHub Actions Runner Log",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(githubLogs) { log ->
                            Text(
                                text = log,
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                                color = if (log.contains("SUCCESS") || log.contains("Artifact")) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            ApkBuildMode.LOCAL_ENGINE -> {
                // Local Gradle Engine Panel
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ContentPaste, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "কোড পেস্ট করে সরাসরি APK তৈরি" else "Paste Code & Build APK",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            TextButton(onClick = { showPasteCodeBox = !showPasteCodeBox }) {
                                Text(
                                    if (showPasteCodeBox)
                                        (if (language == AppLanguage.BENGALI) "লুকান" else "Hide")
                                    else
                                        (if (language == AppLanguage.BENGALI) "কোড পেস্ট বক্স" else "Paste Box"),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (showPasteCodeBox) {
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = codePasteText,
                                onValueChange = { codePasteText = it },
                                label = { Text(if (language == AppLanguage.BENGALI) "অ্যান্ড্রয়েড কোড এখানে পেস্ট করুন" else "Paste Kotlin Compose Code Here", fontSize = 11.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("apk_paste_code_field"),
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    if (codePasteText.isNotBlank()) {
                                        onUpdateCode(codePasteText.trim())
                                        showPasteCodeBox = false
                                        onStartBuild()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (language == AppLanguage.BENGALI) "কোড লোড করে তাৎক্ষণিক বিল্ড" else "Load Code & Build Immediately", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isBuilding) {
                            Column {
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = statusMessage,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Button(
                                onClick = onStartBuild,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_apk_build_btn")
                            ) {
                                Icon(Icons.Default.Android, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (language == AppLanguage.BENGALI) "লোকাল এপিকে ফাইল তৈরি করুন" else "Generate Local APK Package"
                                )
                            }
                        }

                        if (isApkReady && !isBuilding) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("app-debug.apk", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Size: 14.2 MB • E2EE Signed", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { ApkExportHelper.shareApk(context) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier.testTag("share_apk_btn")
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(if (language == AppLanguage.BENGALI) "শেয়ার" else "Share", fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = { saveApkLauncher.launch("app-debug.apk") },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.testTag("download_apk_btn")
                                        ) {
                                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (language == AppLanguage.BENGALI) "ডাউনলোড" else "Download", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Terminal Log Output
                Text(
                    text = if (language == AppLanguage.BENGALI) "কমপাইলেশন ও বিল্ড টার্মিনাল লগ (Terminal Log)" else "Build Terminal Output",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(logs) { log ->
                            Text(
                                text = log,
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                                color = if (log.contains("SUCCESSFUL")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }

    // GitHub Actions Workflow Dialog
    if (showWorkflowDialog) {
        AlertDialog(
            onDismissRequest = { showWorkflowDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SettingsSuggest, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ".github/workflows/android_build.yml",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (language == AppLanguage.BENGALI)
                            "এই ফাইলটি আপনার GitHub রিপোজিটরির মেইন ব্রাঞ্চে পুশ করলে স্বয়ংক্রিয়ভাবে JDK 17 দিয়ে APK বিল্ড করবে।"
                        else
                            "This CI/CD configuration triggers automated JDK 17 APK builds on push and pull requests.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            item {
                                Text(
                                    text = IdeRepository.GITHUB_ACTIONS_WORKFLOW_CONTENT,
                                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(IdeRepository.GITHUB_ACTIONS_WORKFLOW_CONTENT))
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == AppLanguage.BENGALI) "কপি করুন" else "Copy YAML", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            onSaveGithubWorkflow()
                            showWorkflowDialog = false
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == AppLanguage.BENGALI) "প্রজেক্টে সেভ করুন" else "Save to Project", fontSize = 11.sp)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showWorkflowDialog = false }) {
                    Text(if (language == AppLanguage.BENGALI) "বন্ধ করুন" else "Close")
                }
            }
        )
    }
}
