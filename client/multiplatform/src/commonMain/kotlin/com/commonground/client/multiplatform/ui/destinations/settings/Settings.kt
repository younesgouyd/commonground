package com.commonground.client.multiplatform.ui.destinations.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.ThemeMode
import com.commonground.client.multiplatform.ui.ThemeState

@Composable
fun Settings(onLogout: () -> Unit) {
    val themeMode = ThemeState.current.value

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Appearance",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            tonalElevation = 0.dp
        ) {
            Column {
                ThemeOption(
                    icon = Icons.Default.DarkMode,
                    label = "Dark",
                    selected = themeMode == ThemeMode.Dark,
                    onClick = { ThemeState.current.value = ThemeMode.Dark }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ThemeOption(
                    icon = Icons.Default.LightMode,
                    label = "Light",
                    selected = themeMode == ThemeMode.Light,
                    onClick = { ThemeState.current.value = ThemeMode.Light }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ThemeOption(
                    icon = Icons.Default.BrightnessAuto,
                    label = "System default",
                    selected = themeMode == ThemeMode.System,
                    onClick = { ThemeState.current.value = ThemeMode.System }
                )
            }
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

@Composable
private fun ThemeOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        if (selected) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
