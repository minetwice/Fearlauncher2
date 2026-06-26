package com.fearlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fearlauncher.ui.theme.*

@Composable
fun LoginScreen(
    onMicrosoftLogin: () -> Unit,
    onLocalLogin: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(GradientStart, GradientEnd)))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Title Section
        Text(
            "FEAR LAUNCHER",
            style = MaterialTheme.typography.headlineLarge,
            color = SilverAccent,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 36.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Minecraft Java Launcher",
            color = SilverDark,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(48.dp))

        // Login Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 300.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BlackSurface.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Sign In",
                    style = MaterialTheme.typography.titleLarge,
                    color = SilverPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Username Field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username", color = SilverDark) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, "Person", tint = SilverPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White.copy(alpha = 0.8f),
                        focusedBorderColor = SilverPrimary,
                        unfocusedBorderColor = SilverDark
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Local Login Button
                Button(
                    onClick = {
                        if (username.isNotBlank()) {
                            isLoading = true
                            onLocalLogin(username)
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading && username.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SilverPrimary,
                        disabledContainerColor = SilverDark
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = BlackBg,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "LOCAL LOGIN",
                            color = BlackBg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "or",
                    color = SilverDark,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Microsoft Login Button
                OutlinedButton(
                    onClick = onMicrosoftLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SilverPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(SilverPrimary, SilverAccent))
                    )
                ) {
                    Icon(
                        Icons.Default.Lock,
                        "Microsoft",
                        tint = SilverPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Sign in with Microsoft",
                        color = SilverPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "By continuing, you agree to our Terms of Service",
            color = SilverDark.copy(alpha = 0.6f),
            fontSize = 10.sp
        )
    }
}
