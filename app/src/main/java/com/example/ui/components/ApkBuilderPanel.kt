package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage

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
    onUpdateCode: (String) -> Unit = {}
) {
    var showPasteCodeBox by remember { mutableStateOf(false) }
    var codePasteText by remember { mutableStateOf(activeFileContent) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        // APK Header
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
                Text(
                    text = if (language == AppLanguage.BENGALI) "এন্ড্রয়েড এপিকে বিল্ডার (APK Build Engine)" else "Android APK Build Engine",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Code Paste Section for direct APK Generation
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
                            text = if (language == AppLanguage.BENGALI) "কোড পেস্ট করে এপিকে ফাইল তৈরি করুন" else "Paste Code & Generate APK",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextButton(
                        onClick = { showPasteCodeBox = !showPasteCodeBox },
                        modifier = Modifier.testTag("toggle_paste_code_box_btn")
                    ) {
                        Text(
                            if (showPasteCodeBox) (if (language == AppLanguage.BENGALI) "লুকান" else "Hide")
                            else (if (language == AppLanguage.BENGALI) "কোড পেস্ট করুন" else "Paste Code"),
                            fontSize = 11.sp
                        )
                    }
                }

                if (showPasteCodeBox) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = codePasteText,
                        onValueChange = { codePasteText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("apk_builder_code_paste_input"),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                        placeholder = { Text("Paste Kotlin / Compose code to compile into APK...") }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (codePasteText.isNotBlank()) {
                                onUpdateCode(codePasteText)
                                showPasteCodeBox = false
                                onStartBuild()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("paste_and_build_apk_btn")
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.BENGALI) "কোড পেস্ট করে এপিকে জেনারেট করুন" else "Compile Code & Generate APK")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Build Action Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "গ্রাডেল ও ডেক্স (DEX) কমপাইলেশন টুলস" else "Gradle & DEX Compiler Toolchain",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == AppLanguage.BENGALI) "অ্যান্ড্রয়েড ইনস্টলযোগ্য .apk প্যাকেজ তৈরি করতে চাপ দিন।" else "Compile active code into an installable .apk package.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isBuilding) {
                    Column {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = statusMessage,
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
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
                            if (language == AppLanguage.BENGALI) "এপিকে ফাইল জেনারেট করুন (Generate APK)" else "Generate APK Package Now"
                        )
                    }
                }

                if (isApkReady && !isBuilding) {
                    Spacer(modifier = Modifier.height(12.dp))
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
                                    Text("app-debug.apk", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Size: 14.2 MB • E2EE Signed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                            Button(
                                onClick = { /* Export / Install APK */ },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("download_apk_btn")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == AppLanguage.BENGALI) "ডাউনলোড ও ইনস্টল" else "Export APK", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Terminal Log Output
        Text(
            text = if (language == AppLanguage.BENGALI) "কমপাইলেশন ও বিল্ড টার্মিনাল লগ (Terminal Log)" else "Build Terminal Output",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                        color = if (log.contains("SUCCESSFUL")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
