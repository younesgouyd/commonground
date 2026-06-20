package com.commonground.client.multiplatform.ui.destinations.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.widgets.ProfileContent
import com.commonground.client.multiplatform.ui.widgets.ProfileNavActions
import com.commonground.client.multiplatform.ui.widgets.StatItem
import com.commonground.core.models.User
import kotlinx.coroutines.launch

@Composable
fun User(
    viewModel: UserViewModel,
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
    state: UserState,
    navActions: ProfileNavActions
) {
    when (state) {
        is UserState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UserState.Loaded -> Wide(state, navActions)
        is UserState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}


@Composable
private fun Wide(
    state: UserState.Loaded,
    navActions: ProfileNavActions
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Surface(
            modifier = Modifier.width(320.dp).fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 2.dp
        ) {
            ProfileSidebar(state)
        }
        Surface(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileContent(state.events, state.follows, navActions)
        }
    }
}

@Composable
private fun ProfileSidebar(state: UserState.Loaded) {
    val scrollState = rememberScrollState()
    val user by state.user.collectAsState()
    val followers by state.follows.followers.collectAsState()
    val following by state.follows.following.collectAsState()
    val followersCount by followers.totalCount.collectAsState()
    val followingCount by following.totalCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            onClick = { TODO() }
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
        ToggleFollowState(
            user = user,
            onUnfollowUserClick = state.follows.onUnfollowUserClick,
            onFollowUserClick = state.follows.onFollowUserClick
        )
    }
}

@Composable
private fun Compact(
    state: UserState,
    navActions: ProfileNavActions
) {
    when (state) {
        is UserState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UserState.Loaded -> Compact(state, navActions)
        is UserState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun Compact(
    state: UserState.Loaded,
    navActions: ProfileNavActions
) {
    val user by state.user.collectAsState()
    val followers by state.follows.followers.collectAsState()
    val following by state.follows.following.collectAsState()
    val followersCount by followers.totalCount.collectAsState()
    val followingCount by following.totalCount.collectAsState()

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
                Surface(
                    modifier = Modifier.size(140.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    onClick = { TODO() }
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
                ToggleFollowState(
                    user = user,
                    onUnfollowUserClick = state.follows.onUnfollowUserClick,
                    onFollowUserClick = state.follows.onFollowUserClick
                )
            }
        }
        ProfileContent(state.events, state.follows, navActions)
    }
}

@Composable
private fun ToggleFollowState(
    user: User,
    onUnfollowUserClick: suspend (userId: String) -> Unit,
    onFollowUserClick: suspend (userId: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isSubmitting by remember { mutableStateOf(false) }

    if (user.isFollowed != null) {
        if (user.isFollowed == true) {
            OutlinedButton(
                onClick = {
                    scope.launch {
                        isSubmitting = true
                        try {
                            onUnfollowUserClick(user.id)
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                enabled = !isSubmitting,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Text(
                        text = "Following",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        } else {
            Button(
                onClick = {
                    scope.launch {
                        isSubmitting = true
                        try {
                            onFollowUserClick(user.id)
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                enabled = !isSubmitting,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Follow",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
