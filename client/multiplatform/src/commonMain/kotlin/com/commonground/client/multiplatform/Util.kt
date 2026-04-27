package com.commonground.client.multiplatform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

// https://kotlinlang.org/docs/multiplatform/whats-new-compose-170.html#new-common-modules:~:text=The%20calculateWindowSizeClass()%20function%20is%20not%20available%20in%20common%20code%20yet
@Composable
expect fun getWindowSizeClass(): WindowWidthSizeClass

@Composable
fun AdaptiveUi(
    wide: @Composable () -> Unit,
    compact: @Composable () -> Unit
) {
    val windowSizeClass = getWindowSizeClass()

    when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> compact()
        else -> wide()
    }
}

fun Duration?.formatted(): String {
    if (this == null) {
        return "??:??"
    }
    return this.toComponents { hours: Long, minutes: Int, seconds: Int, _: Int ->
        StringBuilder()
            .append(hours.toString().padStart(2, '0'))
            .append(":")
            .append(minutes.toString().padStart(2, '0'))
            .append(":")
            .append(seconds.toString().padStart(2, '0'))
            .toString()
    }
}

data class HeaderAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun ItemDetailsHeaderWide(
    modifier: Modifier = Modifier,
    title: String,
    image: File?,
    itemAttributes: (@Composable ColumnScope.() -> Unit)? = null,
    mainAction: HeaderAction? = null,
    actions: List<HeaderAction> = emptyList()
) {
    Surface(modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.weight(1f).aspectRatio(1f, true),
                file = image
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center
                )
                if (itemAttributes != null) { itemAttributes() }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (mainAction != null) {
                        item {
                            Button(mainAction.onClick) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(mainAction.icon, null)
                                    Text(text = mainAction.label, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                    items(actions) { (label, icon, onClick) ->
                        OutlinedButton(
                            content = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(icon, null)
                                    Text(text = label, style = MaterialTheme.typography.labelMedium)
                                }
                            },
                            onClick = onClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemDetailsHeaderCompact(
    modifier: Modifier = Modifier,
    title: String,
    image: File?,
    itemAttributes: (@Composable ColumnScope.() -> Unit)? = null,
    mainAction: HeaderAction? = null,
    actions: List<HeaderAction> = emptyList()
) {
    Surface(modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                file = image
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
            if (itemAttributes != null) { itemAttributes() }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (mainAction != null) {
                    item {
                        IconButton(
                            onClick = mainAction.onClick
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                imageVector = mainAction.icon,
                                contentDescription = mainAction.label,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                items(actions) { (label, icon, onClick) ->
                    OutlinedButton(
                        content = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(icon, null)
                                Text(text = label, style = MaterialTheme.typography.labelMedium)
                            }
                        },
                        onClick = onClick
                    )
                }
            }
        }
    }
}