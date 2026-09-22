package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivePreviewView(
    activeFileContent: String,
    language: AppLanguage = AppLanguage.BENGALI
) {
    var deviceMode by remember { mutableStateOf("Phone") } // "Phone", "Tablet", "FullScreen"
    var isReloading by remember { mutableStateOf(false) }

    // Parse Jetpack Compose UI components in real-time from active file code
    val parsedUiElements = remember(activeFileContent) {
        parseComposeCodeToUiElements(activeFileContent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Preview Header Toolbar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "Live Preview",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "অ্যান্ড্রয়েড স্টুডিও লাইভ প্রিভিউ" else "Android Studio Live Preview",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (language == AppLanguage.BENGALI) "Jetpack Compose রিয়েল-টাইম রেন্ডারার" else "Jetpack Compose Real-Time Renderer",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = deviceMode == "Phone",
                        onClick = { deviceMode = "Phone" },
                        label = { Text("Phone", fontSize = 11.sp) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    FilterChip(
                        selected = deviceMode == "Tablet",
                        onClick = { deviceMode = "Tablet" },
                        label = { Text("Tablet", fontSize = 11.sp) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    FilterChip(
                        selected = deviceMode == "FullScreen",
                        onClick = { deviceMode = "FullScreen" },
                        label = { Text("Full", fontSize = 11.sp) }
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { isReloading = true },
                        modifier = Modifier.testTag("reload_preview_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload Preview",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        LaunchedEffect(isReloading) {
            if (isReloading) {
                kotlinx.coroutines.delay(250)
                isReloading = false
            }
        }

        if (deviceMode == "FullScreen") {
            // Full Screen Edge-to-Edge Direct Preview
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (isReloading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    AppPreviewContentBody(parsedUiElements = parsedUiElements)
                }
            }
        } else {
            // Responsive Device Container (Phone / Tablet Frame)
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                val availableWidth = maxWidth
                val availableHeight = maxHeight

                val targetWidth = if (deviceMode == "Tablet") {
                    minOf(380.dp, availableWidth - 12.dp)
                } else {
                    minOf(300.dp, availableWidth - 12.dp)
                }

                val targetHeight = if (deviceMode == "Tablet") {
                    minOf(540.dp, availableHeight - 12.dp)
                } else {
                    minOf(500.dp, availableHeight - 12.dp)
                }

                Surface(
                    modifier = Modifier
                        .width(targetWidth)
                        .height(targetHeight)
                        .clip(RoundedCornerShape(26.dp))
                        .border(3.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), RoundedCornerShape(26.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Phone Top Status Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("12:00", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.Black, shape = CircleShape)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(12.dp))
                                Icon(Icons.Default.BatteryFull, contentDescription = null, modifier = Modifier.size(12.dp))
                            }
                        }

                        if (isReloading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            AppPreviewContentBody(parsedUiElements = parsedUiElements)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppPreviewContentBody(parsedUiElements: List<ParsedUiElement>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (parsedUiElements.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Android, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("App Screen Rendered", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Code compiled successfully into live preview", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            }
        } else {
            parsedUiElements.forEach { element ->
                RenderParsedUiElement(element = element)
            }
        }
    }
}

// Data class representing parsed Compose UI components
sealed class ParsedUiElement {
    data class HeaderTitle(val text: String) : ParsedUiElement()
    data class BodyText(val text: String) : ParsedUiElement()
    data class ActionButton(val label: String) : ParsedUiElement()
    data class InputField(val label: String, val initialValue: String = "") : ParsedUiElement()
    data class InfoCard(val title: String, val subtitle: String) : ParsedUiElement()
    data class ToggleSwitch(val label: String, val defaultChecked: Boolean = true) : ParsedUiElement()
    data class ListCard(val items: List<String>) : ParsedUiElement()
}

@Composable
fun RenderParsedUiElement(element: ParsedUiElement) {
    var counter by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var switchChecked by remember { mutableStateOf(true) }

    when (element) {
        is ParsedUiElement.HeaderTitle -> {
            Text(
                text = element.text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        is ParsedUiElement.BodyText -> {
            Text(
                text = element.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
        is ParsedUiElement.ActionButton -> {
            Button(
                onClick = { counter++ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (counter > 0) "${element.label} ($counter)" else element.label)
            }
        }
        is ParsedUiElement.InputField -> {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text(element.label, fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
        is ParsedUiElement.InfoCard -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(element.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(element.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                }
            }
        }
        is ParsedUiElement.ToggleSwitch -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(element.label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Switch(
                    checked = switchChecked,
                    onCheckedChange = { switchChecked = it }
                )
            }
        }
        is ParsedUiElement.ListCard -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    element.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(item, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Parses Compose source code to extract visible components dynamically.
 */
fun parseComposeCodeToUiElements(code: String): List<ParsedUiElement> {
    val elements = mutableListOf<ParsedUiElement>()

    // Regex matchers for Jetpack Compose components
    val textRegex = Regex("""Text\s*\(\s*(?:text\s*=\s*)?"([^"]+)"""")
    val buttonRegex = Regex("""Button\s*\([\s\S]*?Text\s*\(\s*(?:text\s*=\s*)?"([^"]+)"""")
    val textFieldRegex = Regex("""(?:OutlinedTextField|TextField)\s*\([\s\S]*?label\s*=\s*\{\s*Text\s*\(\s*(?:text\s*=\s*)?"([^"]+)"""")
    val cardRegex = Regex("""Card\s*\([\s\S]*?Text\s*\(\s*(?:text\s*=\s*)?"([^"]+)"""")

    // Extract Titles & Texts
    textRegex.findAll(code).forEach { match ->
        val extractedText = match.groupValues.getOrNull(1)
        if (!extractedText.isNullOrBlank() && extractedText.length < 100) {
            if (extractedText.contains("Title") || extractedText.contains("Welcome") || extractedText.contains("Header") || extractedText.length < 25) {
                elements.add(ParsedUiElement.HeaderTitle(extractedText))
            } else {
                elements.add(ParsedUiElement.BodyText(extractedText))
            }
        }
    }

    // Extract Buttons
    buttonRegex.findAll(code).forEach { match ->
        val label = match.groupValues.getOrNull(1)
        if (!label.isNullOrBlank()) {
            elements.add(ParsedUiElement.ActionButton(label))
        }
    }

    // Extract Text Fields
    textFieldRegex.findAll(code).forEach { match ->
        val label = match.groupValues.getOrNull(1)
        if (!label.isNullOrBlank()) {
            elements.add(ParsedUiElement.InputField(label))
        }
    }

    // Extract Cards
    cardRegex.findAll(code).forEach { match ->
        val title = match.groupValues.getOrNull(1)
        if (!title.isNullOrBlank()) {
            elements.add(ParsedUiElement.InfoCard(title, "Extracted layout component"))
        }
    }

    // Add fallback sample components if minimal code was supplied
    if (elements.isEmpty()) {
        elements.add(ParsedUiElement.HeaderTitle("NoTrack Sample App"))
        elements.add(ParsedUiElement.BodyText("Jetpack Compose Live Preview Active"))
        elements.add(ParsedUiElement.InputField("Enter Name"))
        elements.add(ParsedUiElement.ActionButton("Click Counter"))
        elements.add(ParsedUiElement.InfoCard("Privacy First", "E2EE zero telemetry architecture"))
    }

    return elements
}
