package com.fearlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fearlauncher.ui.theme.*

@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()

    // Settings State
    var javaPath by remember { mutableStateOf("Internal (JRE 21)") }
    var jvmArgs by remember { mutableStateOf("-Xmx4G -XX:+UseG1GC") }
    var memoryAlloc by remember { mutableFloatStateOf(4f) }
    var resolution by remember { mutableStateOf("1920x1080") }
    var gameDir by remember { mutableStateOf("/sdcard/FearLauncher/.minecraft") }
    var renderer by remember { mutableStateOf("Holy Renderer") }
    var guiScale by remember { mutableFloatStateOf(1f) }
    var keepOpen by remember { mutableStateOf(true) }
    var enableGloss by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Text(
            "SETTINGS",
            style = MaterialTheme.typography.headlineMedium,
            color = SilverAccent,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Global launcher configurations",
            color = SilverDark,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        SettingsSection(title = "Java Runtime") {
            SettingsTextField(label = "Java Path", value = javaPath, onValueChange = { javaPath = it })
            SettingsTextField(label = "JVM Arguments", value = jvmArgs, onValueChange = { jvmArgs = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "Game Settings") {
            SettingsSlider(label = "Global Memory Allocation", value = memoryAlloc, range = 2f..16f, onValueChange = { memoryAlloc = it })
            SettingsTextField(label = "Resolution", value = resolution, onValueChange = { resolution = it })
            SettingsTextField(label = "Game Directory", value = gameDir, onValueChange = { gameDir = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "Video Settings") {
            SettingsDropdown(
                label = "Renderer",
                options = listOf("Holy Renderer", "GL4ES 1.1.4", "Angle (Experimental)"),
                selected = renderer,
                onSelect = { renderer = it }
            )
            SettingsSlider(label = "GUI Scale", value = guiScale, range = 0.5f..2f, onValueChange = { guiScale = it }, isInteger = false)
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "Launcher") {
            SettingsToggle(label = "Keep launcher open", enabled = keepOpen, onToggle = { keepOpen = it })
            SettingsToggle(label = "Enable dark mode gloss", enabled = enableGloss, onToggle = { enableGloss = it })
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            title,
            color = SilverPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlackSurface.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, color = SilverDark, fontSize = 12.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SilverPrimary,
                unfocusedBorderColor = SilverDark.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun SettingsSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    isInteger: Boolean = true
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = SilverDark, fontSize = 12.sp)
            Text(
                if (isInteger) "${value.toInt()} GB" else String.format("%.1f", value),
                color = SilverPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = SilverPrimary,
                activeTrackColor = SilverPrimary
            )
        )
    }
}

@Composable
fun SettingsDropdown(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, color = SilverDark, fontSize = 12.sp)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(SilverDark, SilverPrimary)))
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(selected)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(BlackSurface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = SilverPrimary) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsToggle(label: String, enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = SilverDark, fontSize = 14.sp)
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SilverPrimary,
                checkedTrackColor = SilverPrimary.copy(alpha = 0.5f)
            )
        )
    }
}
