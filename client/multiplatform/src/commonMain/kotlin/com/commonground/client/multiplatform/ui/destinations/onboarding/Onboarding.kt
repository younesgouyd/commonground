package com.commonground.client.multiplatform.ui.destinations.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.core.CategoryId
import com.commonground.core.EventCategory

interface OnboardingNavActions {
}

@Composable
fun Onboarding(
    viewModel: OnboardingViewModel,
    @Suppress("UNUSED_PARAMETER") navActions: OnboardingNavActions
) {
    val state by viewModel.state.collectAsState()
    AdaptiveUi(
        wide = { Wide(state, viewModel) },
        compact = { Compact(state, viewModel) }
    )
}

@Composable
private fun Wide(state: OnboardingState, vm: OnboardingViewModel) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxHeight().padding(32.dp)) {
                Body(state, vm, gridColumns = GridCells.Adaptive(180.dp))
            }
        }
    }
}

@Composable
private fun Compact(state: OnboardingState, vm: OnboardingViewModel) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Body(state, vm, gridColumns = GridCells.Fixed(2))
        }
    }
}

@Composable
private fun Body(
    state: OnboardingState,
    vm: OnboardingViewModel,
    gridColumns: GridCells
) {
    when (state) {
        is OnboardingState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is OnboardingState.Error -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Something went wrong",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleLarge
            )
        }

        is OnboardingState.Loaded -> when (state.step) {
            OnboardingState.Step.Welcome -> WelcomeStep(state, vm)
            OnboardingState.Step.Categories -> CategoriesStep(state, vm, gridColumns)
        }
    }
}

@Composable
private fun WelcomeStep(state: OnboardingState.Loaded, vm: OnboardingViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Celebration,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(Modifier.height(32.dp))
        Text(
            text = "Welcome to CommonGround",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Let's set up your interests so we can show you events you'll love.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 480.dp)
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = vm::next,
            modifier = Modifier.widthIn(min = 220.dp).height(52.dp),
            enabled = !state.isSubmitting
        ) {
            Text("Get started", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = vm::skip, enabled = !state.isSubmitting) {
            Text("Skip for now")
        }
    }
}

@Composable
private fun CategoriesStep(
    state: OnboardingState.Loaded,
    vm: OnboardingViewModel,
    gridColumns: GridCells
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = vm::back, enabled = !state.isSubmitting) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "What are you into?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Pick at least ${OnboardingState.MIN_SELECTED} — you can change these anytime.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AssistChip(
                onClick = {},
                enabled = false,
                label = {
                    Text("${state.selected.size}/${OnboardingState.MIN_SELECTED}+")
                },
                leadingIcon = { Icon(Icons.Default.Tune, null, Modifier.size(18.dp)) }
            )
        }

        // Grid
        LazyVerticalGrid(
            modifier = Modifier.weight(1f),
            columns = gridColumns,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.categories, key = { it.id.value }) { category ->
                CategoryCard(
                    category = category,
                    selected = category.id in state.selected,
                    enabled = !state.isSubmitting,
                    onClick = { vm.toggleCategory(category.id) }
                )
            }
        }

        // Footer
        if (state.error != null) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    text = state.error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = vm::skip,
                enabled = !state.isSubmitting
            ) {
                Text("Skip")
            }
            Button(
                modifier = Modifier.weight(2f).height(52.dp),
                onClick = vm::next,
                enabled = state.canContinue && !state.isSubmitting
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Continue", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: EventCategory,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val container = if (selected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant
    val onContainer = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    val borderWidth = if (selected) 2.dp else 1.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = container
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = iconFor(category.iconKey),
                    contentDescription = null,
                    tint = onContainer,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = onContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                category.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainer,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


private fun iconFor(key: String?): ImageVector = when (key) {
    "music" -> Icons.Default.MusicNote
    "sports" -> Icons.Default.SportsSoccer
    "tech" -> Icons.Default.Code
    "food" -> Icons.Default.Restaurant
    "art" -> Icons.Default.Palette
    "education" -> Icons.Default.School
    "books" -> Icons.Default.MenuBook
    "theater" -> Icons.Default.TheaterComedy
    "fitness" -> Icons.Default.DirectionsRun
    "cafe" -> Icons.Default.LocalCafe
    "design" -> Icons.Default.Brush
    else -> Icons.Default.Favorite
}