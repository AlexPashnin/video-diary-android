package com.videodiary.android.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.videodiary.android.BuildConfig
import com.videodiary.android.domain.model.User
import com.videodiary.android.domain.model.UserTier
import com.videodiary.android.domain.model.WatermarkPosition
import com.videodiary.android.presentation.common.InlineLoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val logoutState by viewModel.logoutState.collectAsStateWithLifecycle()
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkModeEnabled.collectAsStateWithLifecycle(initialValue = false)
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle(initialValue = true)
    val watermarkPositionName by viewModel.watermarkPosition.collectAsStateWithLifecycle(initialValue = "")

    val currentWatermark = remember(watermarkPositionName) {
        WatermarkPosition.entries.firstOrNull { it.name == watermarkPositionName }
            ?: WatermarkPosition.BOTTOM_RIGHT
    }

    var showLogoutConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(logoutState) {
        if (logoutState is LogoutState.Success) onLogout()
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Sign out?") },
            text = { Text("You'll need to sign in again to access your diary.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                    }
                ) { Text("Sign out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel") }
            },
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // Profile section
            SectionLabel("Profile")
            when (val ps = profileState) {
                is ProfileState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        InlineLoadingIndicator()
                    }
                }
                is ProfileState.Loaded -> ProfileCard(user = ps.user)
                is ProfileState.Error -> {
                    Text(
                        "Unable to load profile",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Preferences
            SectionLabel("Preferences")
            ListItem(
                headlineContent = { Text("Dark mode") },
                supportingContent = { Text("Override system theme") },
                trailingContent = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.setDarkMode(it) },
                    )
                },
            )
            ListItem(
                headlineContent = { Text("Push notifications") },
                supportingContent = { Text("Receive alerts for processing updates") },
                trailingContent = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Watermark position
            SectionLabel("Default watermark position")
            WatermarkPositionPicker(
                selected = currentWatermark,
                onSelect = { viewModel.setWatermarkPosition(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Account
            SectionLabel("Account")
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { showLogoutConfirm = true },
                enabled = logoutState !is LogoutState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                if (logoutState is LogoutState.Loading) {
                    InlineLoadingIndicator()
                } else {
                    Text("Sign out")
                }
            }

            if (logoutState is LogoutState.Error) {
                Text(
                    text = (logoutState as LogoutState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            // App version
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Video Diary v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun ProfileCard(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Avatar: initials circle
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = user.displayName.take(2).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            TierBadge(tier = user.tier)
        }
    }
}

@Composable
private fun TierBadge(tier: UserTier) {
    val (label, containerColor, contentColor) = when (tier) {
        UserTier.FREE -> Triple(
            "Free",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
        )
        UserTier.PREMIUM -> Triple(
            "Premium",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
        )
        UserTier.ENTERPRISE -> Triple(
            "Enterprise",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
        )
    }
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun WatermarkPositionPicker(
    selected: WatermarkPosition,
    onSelect: (WatermarkPosition) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Visual 3×2 grid mirroring the actual video corners/edges
    val rows = listOf(
        listOf(WatermarkPosition.TOP_LEFT, WatermarkPosition.CENTER_TOP, WatermarkPosition.TOP_RIGHT),
        listOf(WatermarkPosition.BOTTOM_LEFT, WatermarkPosition.CENTER_BOTTOM, WatermarkPosition.BOTTOM_RIGHT),
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { position ->
                    val isSelected = position == selected
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                       else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (isSelected) Modifier.border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.shapes.small,
                                ) else Modifier
                            )
                            .clickable { onSelect(position) },
                    ) {
                        Text(
                            text = position.label,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

private val WatermarkPosition.label: String
    get() = when (this) {
        WatermarkPosition.TOP_LEFT -> "Top\nLeft"
        WatermarkPosition.TOP_RIGHT -> "Top\nRight"
        WatermarkPosition.BOTTOM_LEFT -> "Bottom\nLeft"
        WatermarkPosition.BOTTOM_RIGHT -> "Bottom\nRight"
        WatermarkPosition.CENTER_TOP -> "Center\nTop"
        WatermarkPosition.CENTER_BOTTOM -> "Center\nBottom"
    }
