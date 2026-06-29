package com.commonground.client.multiplatform.ui.destinations.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.toBackendUrl
import com.commonground.client.multiplatform.ui.widgets.*
import com.commonground.core.models.User
import kotlinx.coroutines.launch

@Composable
fun Profile(
    viewModel: ProfileViewModel,
    navActions: ProfileNavActions
) {
    val state by viewModel.state.collectAsState()

    AdaptiveUi(
        wide = { Wide(state, navActions) },
        compact = { Compact(state, navActions) }
    )
}

@Composable
private fun Wide(
    state: ProfileState,
    navActions: ProfileNavActions
) {
    when (state) {
        is ProfileState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProfileState.Loaded -> Wide(state, navActions)
        is ProfileState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun Wide(
    state: ProfileState.Loaded,
    navActions: ProfileNavActions
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        ProfileSidebar(state, navActions)
        ProfileContent(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            events = state.events,
            follows = state.follows,
            navActions = navActions
        )
    }
}

@Composable
private fun Compact(
    state: ProfileState,
    navActions: ProfileNavActions
) {
    when (state) {
        is ProfileState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProfileState.Loaded -> Compact(state, navActions)
        is ProfileState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun Compact(
    state: ProfileState.Loaded,
    navActions: ProfileNavActions
) {
    val user by state.user.collectAsState()
    val followers by state.follows.followers.collectAsState()
    val following by state.follows.following.collectAsState()
    val followersCount by followers.totalCount.collectAsState()
    val followingCount by following.totalCount.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showSystemFilePicker by remember { mutableStateOf(false) }
    var showExpandedImage by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditDialog = false },
            onSave = state.onUpdateProfile
        )
    }
    if (showSystemFilePicker) {
        SystemFilePicker(
            onFileChosen = {
                showSystemFilePicker = false
                state.onUpdateProfilePic(it)
            },
            dismiss = { showSystemFilePicker = false }
        )
    }

    user.profilePic?.let {
        if (showExpandedImage) {
            ImagePreviewDialog(
                imageUrl = it,
                onDismiss = { showExpandedImage = false }
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    user.profilePic?.let { profilePic ->
                        Surface(
                            modifier = Modifier.size(140.dp),
                            shape = CircleShape,
                            color = Color.Transparent,
                            onClick = { showExpandedImage = true }
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                url = profilePic.toBackendUrl(),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    } ?: run {
                        Surface(
                            modifier = Modifier.size(140.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    modifier = Modifier.size(80.dp),
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile picture",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    FilledIconButton(
                        onClick = { showSystemFilePicker = true },
                        modifier = Modifier.size(28.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile Picture", modifier = Modifier.size(14.dp))
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = user.displayName ?: user.username,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                user.bio.let { bio ->
                    if (!bio.isNullOrBlank()) {
                        Text(
                            text = bio,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    followersCount?.let {
                        StatItem(count = it, label = "Followers")
                    }
                    followingCount?.let {
                        StatItem(count = it, label = "Following")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { showEditDialog = true }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Edit Profile")
                        }
                    }
                    OutlinedButton(navActions::toCreateEvent) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Create Event")
                        }
                    }
                }
            }
        }
        ProfileContent(
            events = state.events,
            follows = state.follows,
            navActions = navActions
        )
    }
}

@Composable
private fun ProfileSidebar(
    state: ProfileState.Loaded,
    navActions: ProfileNavActions
) {
    val scrollState = rememberScrollState()
    val user by state.user.collectAsState()
    val followers by state.follows.followers.collectAsState()
    val following by state.follows.following.collectAsState()
    val followersCount by followers.totalCount.collectAsState()
    val followingCount by following.totalCount.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showSystemFilePicker by remember { mutableStateOf(false) }
    var showExpandedImage by remember { mutableStateOf(false) }

    if (showEditDialog) {
        val user by state.user.collectAsState()
        EditProfileDialog(
            user = user,
            onDismiss = { showEditDialog = false },
            onSave = state.onUpdateProfile
        )
    }

    if (showSystemFilePicker) {
        SystemFilePicker(
            onFileChosen = {
                showSystemFilePicker = false
                state.onUpdateProfilePic(it)
            },
            dismiss = { showSystemFilePicker = false }
        )
    }

    user.profilePic?.let {
        if (showExpandedImage) {
            ImagePreviewDialog(
                imageUrl = it,
                onDismiss = { showExpandedImage = false }
            )
        }
    }

    Surface(
        modifier = Modifier.width(320.dp).fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.BottomCenter) {
                user.profilePic?.let { profilePic ->
                    Surface(
                        modifier = Modifier.size(140.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        onClick = { showExpandedImage = true }
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            url = profilePic.toBackendUrl(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                } ?: run {
                    Surface(
                        modifier = Modifier.size(140.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                modifier = Modifier.size(80.dp),
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile picture",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.offset(y = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = { showSystemFilePicker = true },
                        modifier = Modifier.size(28.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile Picture", modifier = Modifier.size(14.dp))
                    }
                    FilledIconButton(
                        onClick = state.onClearProfilePic,
                        modifier = Modifier.size(28.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Profile Picture", modifier = Modifier.size(14.dp))
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = user.displayName ?: user.username,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            user.bio.let { bio ->
                if (!bio.isNullOrBlank()) {
                    Text(
                        text = bio,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                followersCount?.let {
                    StatItem(count = it, label = "Followers")
                }
                followingCount?.let {
                    StatItem(count = it, label = "Following")
                }
            }
            HorizontalDivider()
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showEditDialog = true }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Edit Profile")
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = navActions::toCreateEvent
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Create Event")
                }
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: suspend (username: String, displayName: String?, bio: String?) -> Unit
) {
    var username by remember { mutableStateOf(user.username) }
    var displayName by remember { mutableStateOf(user.displayName ?: "") }
    var bio by remember { mutableStateOf(user.bio ?: "") }

    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Edit Profile") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = username.isBlank(),
                    enabled = !isSaving
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                )
            }
        },
        confirmButton = {
            Button(
                enabled = username.isNotBlank() && !isSaving,
                onClick = {
                    scope.launch {
                        isSaving = true
                        try {
                            onSave(
                                username.trim(),
                                displayName.trim().takeIf { it.isNotBlank() },
                                bio.trim().takeIf { it.isNotBlank() }
                            )
                            onDismiss()
                        } finally {
                            isSaving = false
                        }
                    }
                }
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isSaving,
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}