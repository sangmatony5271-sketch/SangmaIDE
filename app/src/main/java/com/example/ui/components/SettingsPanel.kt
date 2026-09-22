package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.IdeTheme

@Composable
fun SettingsPanel(
    currentTheme: IdeTheme,
    onSelectTheme: (IdeTheme) -> Unit,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    autoSaveEnabled: Boolean,
    onToggleAutoSave: (Boolean) -> Unit,
    isOfflineMode: Boolean,
    onToggleOfflineMode: (Boolean) -> Unit,
    language: AppLanguage
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.BENGALI) "আইডিই সেটিংস (IDE Preferences)" else "IDE Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Theme Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "কাস্টম থিম ও ডার্ক মোড (Theme)" else "Theme & Color Scheme",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    IdeTheme.values().forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(theme.displayName, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            RadioButton(
                                selected = currentTheme == theme,
                                onClick = { onSelectTheme(theme) },
                                modifier = Modifier.testTag("theme_radio_${theme.name}")
                            )
                        }
                    }
                }
            }
        }

        // Multi-Language Option
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ভাষা (Multi-Language Support)" else "Language (ভাষাগত রূপান্তর)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    AppLanguage.values().forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(lang.displayName, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            RadioButton(
                                selected = currentLanguage == lang,
                                onClick = { onSelectLanguage(lang) },
                                modifier = Modifier.testTag("lang_radio_${lang.code}")
                            )
                        }
                    }
                }
            }
        }

        // Editor & Offline Toggles
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "অফলাইন মোড ও অটো-সেভ" else "Offline Mode & Auto-Save",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(if (language == AppLanguage.BENGALI) "অটো-সেভ অপশন (Auto-Save)" else "Auto-Save Changes", fontWeight = FontWeight.Medium)
                            Text(if (language == AppLanguage.BENGALI) "টাইপ করার সাথে সাথে লোকাল ডেটাবেজে সংরক্ষণ" else "Instantly persist code on typing", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Switch(
                            checked = autoSaveEnabled,
                            onCheckedChange = onToggleAutoSave,
                            modifier = Modifier.testTag("auto_save_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(if (language == AppLanguage.BENGALI) "অফলাইন মোড (Offline Mode)" else "Offline First Mode", fontWeight = FontWeight.Medium)
                            Text(if (language == AppLanguage.BENGALI) "ইন্টারনেট ছাড়াও লোকাল ডাটাবেজে সম্পূর্ণ ফাংশনাল" else "Local SQLite database storage", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Switch(
                            checked = isOfflineMode,
                            onCheckedChange = onToggleOfflineMode,
                            modifier = Modifier.testTag("offline_mode_switch")
                        )
                    }
                }
            }
        }
    }
}
