package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.commonground.client.multiplatform.ui.formatted
import com.commonground.client.multiplatform.ui.toBackendUrl
import com.commonground.core.models.Event
import com.commonground.core.models.User

@Composable
fun WideEventCard(
    modifier: Modifier = Modifier,
    event: Event,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier.width(250.dp).height(300.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            EventImage(
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                image = event.image
            )
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (event.locationName.isNotBlank()) {
                        Location(
                            modifier = Modifier.fillMaxWidth(),
                            locationName = event.locationName
                        )
                    }
                    val desc = event.description
                    if (!desc.isNullOrBlank()) {
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (event.isPrivate) { Badge("Followers only", Icons.Default.Lock) }
                    if (event.isPrivatePlace) { Badge("Indoor", Icons.Default.Home) }
                    if (event.isPaid) { Badge("Paid", Icons.Default.Paid) }
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Creator(event.creator)
                        Text(
                            modifier = Modifier,
                            text = event.startDate.formatted(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Location(
    modifier: Modifier = Modifier,
    locationName: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = locationName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Creator(user: User){
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        user.profilePic?.let { img ->
            Image(
                modifier = Modifier.size(24.dp).clip(CircleShape),
                url = img.toBackendUrl(),
                contentScale = ContentScale.Crop
            )
        } ?: Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.Default.Person,
            contentDescription = null
        )
        Text(
            text = user.displayName ?: user.username,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Creator(
    user: User,
    onClick: () -> Unit
) {
    TextButton(onClick) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            user.profilePic?.let { img ->
                Image(
                    modifier = Modifier.size(24.dp).clip(CircleShape),
                    url = img.toBackendUrl(),
                    contentScale = ContentScale.Crop
                )
            } ?: Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Person,
                contentDescription = null
            )
            Text(
                text = user.displayName ?: user.username,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
