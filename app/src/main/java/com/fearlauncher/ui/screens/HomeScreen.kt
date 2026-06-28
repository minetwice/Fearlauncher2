package com.fearlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fearlauncher.ui.components.VersionCard
import com.fearlauncher.ui.theme.*

@Composable
fun HomeScreen(username: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "FEAR LAUNCHER",
            style = MaterialTheme.typography.headlineMedium,
            color = SilverAccent,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BlackSurface.copy(alpha = 0.8f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(20.dp)
            ) {
                Column {
                    Text("Welcome back,", color = SilverDark, fontSize = 14.sp)
                    Text("$username!", color = SilverPrimary, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ready to explore?", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f))
                }
                Button(
                    onClick = { },
                    modifier = Modifier.align(Alignment.BottomEnd).width(140.dp).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SilverPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, "Play", tint = androidx.compose.ui.graphics.Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("PLAY NOW", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Skin Section
        Text("Your Character", color = SilverAccent, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BlackSurface.copy(alpha = 0.8f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Detailed Skin Preview (Front/Back)
                Row(
                    modifier = Modifier
                        .width(120.dp)
                        .height(160.dp)
                        .background(DeepBlack, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Front Preview
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(SilverDark.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, "Skin Front", tint = SilverPrimary, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Back Preview
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(SilverDark.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, "Skin Back", tint = SilverDark, modifier = Modifier.size(40.dp))
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Current Skin", color = SilverPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("Default Alex", color = SilverDark, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = SilverPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, "Upload", tint = BlackBg, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload", color = BlackBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SilverPrimary),
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(SilverDark, SilverPrimary))),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Reset", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Recent Installations", color = SilverAccent, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(listOf("1.20.4 Vanilla", "1.19.2 Modded", "1.20.1 SMP")) { version ->
                VersionCard(versionName = version)
            }
        }
    }
}
