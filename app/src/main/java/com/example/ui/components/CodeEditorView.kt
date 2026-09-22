package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.Breakpoint

enum class EditorViewMode {
    CODE_ONLY,
    SPLIT,
    PREVIEW_ONLY
}

@Composable
fun CodeEditorView(
    filePath: String,
    content: String,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    breakpoints: List<Breakpoint>,
    onToggleBreakpoint: (String, Int) -> Unit,
    language: AppLanguage,
    onQuickAiPrompt: (String) -> Unit
) {
    val lines = remember(content) { content.split("\n") }
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    var viewMode by remember { mutableStateOf(EditorViewMode.SPLIT) } // Default to SPLIT so preview is ALWAYS visible!
    var showPasteDialog by remember { mutableStateOf(false) }
    var pastedCodeInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Editor Header Bar with View Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // View Mode Chips (Code / Split / Preview)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilterChip(
                    selected = viewMode == EditorViewMode.CODE_ONLY,
                    onClick = { viewMode = EditorViewMode.CODE_ONLY },
                    label = { Text(if (language == AppLanguage.BENGALI) "কোড" else "Code", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(12.dp)) },
                    modifier = Modifier.testTag("mode_code_chip")
                )

                FilterChip(
                    selected = viewMode == EditorViewMode.SPLIT,
                    onClick = { viewMode = EditorViewMode.SPLIT },
                    label = { Text(if (language == AppLanguage.BENGALI) "স্প্লিট" else "Split", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.VerticalSplit, contentDescription = null, modifier = Modifier.size(12.dp)) },
                    modifier = Modifier.testTag("mode_split_chip")
                )

                FilterChip(
                    selected = viewMode == EditorViewMode.PREVIEW_ONLY,
                    onClick = { viewMode = EditorViewMode.PREVIEW_ONLY },
                    label = { Text(if (language == AppLanguage.BENGALI) "প্রিভিউ" else "Preview", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp)) },
                    modifier = Modifier.testTag("mode_preview_chip")
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Paste Code Action Button
                Button(
                    onClick = { showPasteDialog = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .testTag("paste_code_header_btn")
                ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == AppLanguage.BENGALI) "কোড পেস্ট" else "Paste Code", fontSize = 11.sp)
                }

                // Save Action
                IconButton(
                    onClick = onSave,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("save_file_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save File",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

        // Main Editor / Preview Body Layout
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (viewMode) {
                EditorViewMode.CODE_ONLY -> {
                    // Code Editor Canvas Only
                    Box(modifier = Modifier.fillMaxSize()) {
                        CodeCanvas(
                            lines = lines,
                            content = content,
                            onContentChange = onContentChange,
                            breakpoints = breakpoints,
                            filePath = filePath,
                            onToggleBreakpoint = onToggleBreakpoint,
                            verticalScrollState = verticalScrollState,
                            horizontalScrollState = horizontalScrollState
                        )
                    }
                }
                EditorViewMode.SPLIT -> {
                    // Split Layout: Code on Top, Live Phone Preview on Bottom
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            CodeCanvas(
                                lines = lines,
                                content = content,
                                onContentChange = onContentChange,
                                breakpoints = breakpoints,
                                filePath = filePath,
                                onToggleBreakpoint = onToggleBreakpoint,
                                verticalScrollState = verticalScrollState,
                                horizontalScrollState = horizontalScrollState
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.primary, thickness = 2.dp)

                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            LivePreviewView(
                                activeFileContent = content,
                                language = language
                            )
                        }
                    }
                }
                EditorViewMode.PREVIEW_ONLY -> {
                    // Live Phone Preview Only
                    Box(modifier = Modifier.fillMaxSize()) {
                        LivePreviewView(
                            activeFileContent = content,
                            language = language
                        )
                    }
                }
            }
        }

        // Auto-Completion & Snippets
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AssistChip(
                        onClick = {
                            onQuickAiPrompt("Fix syntax and optimize compose state in active file")
                        },
                        label = {
                            Text(
                                if (language == AppLanguage.BENGALI) "🤖 notrack.ai ফিক্স" else "🤖 notrack.ai Fix",
                                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    val quickSnippets = listOf("fun ", "val ", "remember { }", "mutableStateOf()", "@Composable", "Column", "Row", "Text", "Button")
                    quickSnippets.forEach { snippet ->
                        SuggestionChip(
                            onClick = {
                                onContentChange("$content $snippet")
                            },
                            label = {
                                Text(snippet, style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp))
                            }
                        )
                    }
                }
            }
        }
    }

    // Code Paste Dialog
    if (showPasteDialog) {
        AlertDialog(
            onDismissRequest = { showPasteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (language == AppLanguage.BENGALI) "কোড পেস্ট করে লোড করুন" else "Paste Code to Editor",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column {
                    Text(
                        if (language == AppLanguage.BENGALI) "যেকোনো Kotlin / Compose কোড পেস্ট করুন। এটি স্বয়ংক্রিয়ভাবে লাইভ প্রিভিউতে রেন্ডার হবে।" else "Paste your Kotlin / Compose code below. It will render directly in live preview.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pastedCodeInput,
                        onValueChange = { pastedCodeInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("pasted_code_text_field"),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                        placeholder = { Text("fun main() {\n   Text(\"Hello World\")\n}") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pastedCodeInput.isNotBlank()) {
                            onContentChange(pastedCodeInput.trim())
                            onSave()
                            pastedCodeInput = ""
                            showPasteDialog = false
                            viewMode = EditorViewMode.SPLIT
                        }
                    },
                    modifier = Modifier.testTag("confirm_paste_code_btn")
                ) {
                    Text(if (language == AppLanguage.BENGALI) "প্রয়োগ ও লাইভ প্রিভিউ দেখুন" else "Apply & View Live Preview")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasteDialog = false }) {
                    Text(if (language == AppLanguage.BENGALI) "বাতিল" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun CodeCanvas(
    lines: List<String>,
    content: String,
    onContentChange: (String) -> Unit,
    breakpoints: List<Breakpoint>,
    filePath: String,
    onToggleBreakpoint: (String, Int) -> Unit,
    verticalScrollState: androidx.compose.foundation.ScrollState,
    horizontalScrollState: androidx.compose.foundation.ScrollState
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScrollState)
    ) {
        // Line Numbers
        Column(
            modifier = Modifier
                .width(42.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            lines.indices.forEach { idx ->
                val lineNum = idx + 1
                val hasBreakpoint = breakpoints.any { it.filePath == filePath && it.lineNumber == lineNum }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clickable { onToggleBreakpoint(filePath, lineNum) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (hasBreakpoint) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                        )
                    } else {
                        Text(
                            text = "$lineNum",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        }

        // Main Code Input Field
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(6.dp)
                .horizontalScroll(horizontalScrollState)
        ) {
            BasicTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("code_text_field"),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 20.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
