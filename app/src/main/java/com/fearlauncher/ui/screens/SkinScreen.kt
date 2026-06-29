package com.fearlauncher.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.fearlauncher.logic.skins.Skin
import com.fearlauncher.logic.skins.SkinManager
import com.fearlauncher.ui.theme.*

@Composable
fun SkinScreen() {
    val context = LocalContext.current
    var skins by remember { mutableStateOf(SkinManager.getSkins(context)) }
    var selectedSkin by remember { mutableStateOf<Skin?>(null) }
    var rotation by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val autoRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auto_rotate"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "SKIN LIBRARY",
            style = MaterialTheme.typography.headlineMedium,
            color = SilverAccent,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Customize your character appearance",
            color = SilverDark,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            // Left Side - Skin Preview
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BlackSurface.copy(alpha = 0.6f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // 3D-ish Preview (using graphicsLayer for rotation)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.graphicsLayer {
                                rotationY = if (selectedSkin != null) rotation else autoRotation
                            }
                        ) {
                            // Head
                            Box(
                                modifier = Modifier
                                    .size(width = 56.dp, height = 56.dp)
                                    .background(if (selectedSkin?.name == "Alex") Color(0xFFFFD1AA) else Color(0xFFC09060), RoundedCornerShape(2.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                // Face Details (Mouth/Eyes)
                                Box(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 16.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Box(modifier = Modifier.size(8.dp).background(Color.White))
                                        Box(modifier = Modifier.size(8.dp).background(Color.White))
                                    }
                                    Box(modifier = Modifier.size(width = 16.dp, height = 4.dp).background(Color.Red.copy(alpha = 0.5f)).align(Alignment.BottomCenter))
                                }
                            }
                            // Body
                            val bodyWidth = if (selectedSkin?.name == "Alex") 72.dp else 80.dp
                            Box(
                                modifier = Modifier
                                    .size(width = bodyWidth, height = 96.dp)
                                    .padding(top = 2.dp)
                                    .background(SilverPrimary.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                            )
                            // Legs & Arms
                            Row(modifier = Modifier.padding(top = 2.dp)) {
                                Box(modifier = Modifier.size(width = 36.dp, height = 96.dp).background(SilverPrimary.copy(alpha = 0.15f), RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(2.dp))
                                Box(modifier = Modifier.size(width = 36.dp, height = 96.dp).background(SilverPrimary.copy(alpha = 0.15f), RoundedCornerShape(2.dp)))
                            }
                        }

                        Slider(
                            value = rotation,
                            onValueChange = { rotation = it },
                            valueRange = 0f..360f,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                            colors = SliderDefaults.colors(thumbColor = SilverPrimary, activeTrackColor = SilverPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* Apply Skin */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SilverPrimary)
                ) {
                    Text("WEAR SKIN", color = BlackBg, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Right Side - Skin List
            val skinPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    SkinManager.importSkin(context, it, "Skin_${System.currentTimeMillis()}")
                    skins = SkinManager.getSkins(context)
                }
            }

            Column(
                modifier = Modifier
                    .width(350.dp)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Collection", color = SilverAccent, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = { skinPickerLauncher.launch("image/png") }) {
                        Icon(Icons.Default.Add, null, tint = SilverPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Default Steve/Alex
                    item {
                        SkinCard("Steve", isSelected = selectedSkin?.name == "Steve", onSelect = { selectedSkin = Skin("Steve", java.io.File("")) })
                    }
                    item {
                        SkinCard("Alex", isSelected = selectedSkin?.name == "Alex", onSelect = { selectedSkin = Skin("Alex", java.io.File("")) })
                    }

                    items(skins) { skin ->
                        SkinCard(skin.name, isSelected = selectedSkin?.name == skin.name, onSelect = { selectedSkin = skin })
                    }
                }
            }
        }
    }
}

@Composable
fun SkinCard(name: String, isSelected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SilverPrimary.copy(alpha = 0.2f) else BlackSurface.copy(alpha = 0.8f)
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, SilverPrimary) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(DeepBlack, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = if (isSelected) SilverPrimary else SilverDark)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, color = if (isSelected) SilverPrimary else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}
