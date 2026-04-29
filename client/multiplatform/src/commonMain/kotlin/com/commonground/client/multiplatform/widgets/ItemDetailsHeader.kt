package com.commonground.client.multiplatform.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.commonground.core.ImageUrl

data class MainHeaderAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

sealed class HeaderAction {
    data class Button(
        val label: String,
        val icon: ImageVector,
        val onClick: () -> Unit
    ) : HeaderAction()

    data class DropDown(
        val label: String,
        val icon: ImageVector,
        val options: List<DropdownOption>,
        val selectedOption: Any,
        val onChange: (Any) -> Unit
    ) : HeaderAction()
}

sealed class Image {
    data class File(val value: java.io.File /* TODO */) : Image()
    data class Url(val value: ImageUrl) : Image()
    class ByteArray(val value: kotlin.ByteArray) : Image()
    data class ImageVector(val value: androidx.compose.ui.graphics.vector.ImageVector) : Image()
}

@Composable
fun ItemDetailsHeaderWide(
    modifier: Modifier = Modifier,
    title: String,
    image: Image?,
    itemAttributes: (@Composable ColumnScope.() -> Unit)? = null,
    mainAction: MainHeaderAction? = null,
    actions: List<HeaderAction> = emptyList()
) {
    Surface(modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.weight(1f).aspectRatio(1f, true),
                image = image
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
                Actions(mainAction, actions)
            }
        }
    }
}

@Composable
fun ItemDetailsHeaderCompact(
    modifier: Modifier = Modifier,
    title: String,
    image: Image?,
    itemAttributes: (@Composable ColumnScope.() -> Unit)? = null,
    mainAction: MainHeaderAction? = null,
    actions: List<HeaderAction> = emptyList()
) {
    Surface(modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                modifier = Modifier.weight(1f).aspectRatio(1f, true),
                image = image
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
            if (itemAttributes != null) { itemAttributes() }
            Actions(mainAction, actions)
        }
    }
}

@Composable
private fun Image(
    modifier: Modifier,
    image: Image?
) {
    when (image) {
        is Image.File -> Image(modifier = modifier, file = image.value)
        is Image.ImageVector -> Image(modifier = modifier, imageVector = image.value, contentDescription = null)
        is Image.ByteArray -> Image(modifier = modifier, data = image.value)
        is Image.Url -> Image(modifier = modifier, url = image.value)
        null -> Unit
    }
}

@Composable
private fun Actions(
    mainAction: MainHeaderAction?,
    actions: List<HeaderAction>
) {
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
        items(actions) { action ->
            when (action) {
                is HeaderAction.Button -> OutlinedButton(
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(action.icon, null)
                            Text(text = action.label, style = MaterialTheme.typography.labelMedium)
                        }
                    },
                    onClick = action.onClick
                )
                is HeaderAction.DropDown -> Dropdown(
                    options = action.options,
                    selectedOption = action.selectedOption,
                    onOptionClick = action.onChange
                )
            }
        }
    }
}