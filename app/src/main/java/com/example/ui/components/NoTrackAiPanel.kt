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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage

@Composable
fun NoTrackAiPanel(
    aiResponse: String,
    isThinking: Boolean,
    onAskAi: (String, String) -> Unit,
    onApplyCode: () -> Unit,
    language: AppLanguage
) {
    var promptInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        // AI Header & Zero Tracking Privacy Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "notrack.ai",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "notrack.ai Assistant",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (language == AppLanguage.BENGALI) "১০০% গোপনীয়তা • জিরো ডেটা ট্র্যাকিং (Privacy-First)" else "100% E2EE • Zero Data Telemetry",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI Quick Actions Row
        Text(
            text = if (language == AppLanguage.BENGALI) "এ আই কুইক অ্যাকশন (AI Quick Actions)" else "AI Quick Actions",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AssistChip(
                onClick = { onAskAi("Fix syntax errors and NPE risks in active file", "bugfix") },
                label = { Text("🐛 Bugfix", fontSize = 11.sp) },
                modifier = Modifier.weight(1f).testTag("ai_bugfix_chip")
            )
            AssistChip(
                onClick = { onAskAi("Refactor active file for clean compose architecture", "refactor") },
                label = { Text("⚡ Refactor", fontSize = 11.sp) },
                modifier = Modifier.weight(1f).testTag("ai_refactor_chip")
            )
            AssistChip(
                onClick = { onAskAi("Explain active file code in detail", "explain") },
                label = { Text("📖 Explain", fontSize = 11.sp) },
                modifier = Modifier.weight(1f).testTag("ai_explain_chip")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI Response Output
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                if (isThinking) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "notrack.ai প্রাইভেট প্রসেসিং চলছে..." else "notrack.ai encrypting & processing...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else if (aiResponse.isBlank()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "আপনার প্রশ্ন বা কোড প্রম্পট লিখুন অথবা ওপরের কুইক চিপ এ চাপ দিন।" else "Type a code query or tap a quick chip above.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            item {
                                Text(
                                    text = aiResponse,
                                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, lineHeight = 20.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onApplyCode,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("apply_ai_code_btn")
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (language == AppLanguage.BENGALI) "কোড এডিটরে প্রয়োগ করুন" else "Apply Code to Active File")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Custom Prompt Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = { Text(if (language == AppLanguage.BENGALI) "notrack.ai এ প্রশ্ন করুন..." else "Ask notrack.ai...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_prompt_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (promptInput.isNotBlank()) {
                        onAskAi(promptInput.trim(), "general")
                        promptInput = ""
                    }
                },
                modifier = Modifier.testTag("send_ai_prompt_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send AI Prompt",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
