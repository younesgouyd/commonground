package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.commonground.client.multiplatform.data.LocationManager
import com.commonground.client.multiplatform.ui.*
import com.commonground.core.models.Coordinates
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraMoveReason
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun CoordinatesForm(
    modifier: Modifier = Modifier,
    value: Coordinates?,
    error: String?,
    onValueChange: (Coordinates?) -> Unit,
) {
    CoordinatesFormContainer(
        modifier = modifier
    ) {
        Content(
            value = value,
            error = error,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun CoordinatesFormDialog(
    value: Coordinates?,
    error: String?,
    onValueChange: (Coordinates?) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        CoordinatesFormContainer(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Content(
                    value = value,
                    error = error,
                    onValueChange = onValueChange
                )
                Button(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    onClick = onDismissRequest,
                    shape = MaterialTheme.shapes.medium,
                    content = { Text("Done") }
                )
            }
        }
    }
}

@Composable
private fun CoordinatesFormContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        content()
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun Content(
    value: Coordinates?,
    error: String?,
    onValueChange: (Coordinates?) -> Unit
) {
    val scope = rememberCoroutineScope()

    var mapLoadFinished by remember { mutableStateOf(false) }
    val cameraState = rememberCameraState()

    // note: cameraState.isCameraMoving doesn't work in jvm when the user is dragging the map
    val isDragging = cameraState.isCameraMoving && cameraState.moveReason == CameraMoveReason.GESTURE

    val pinLiftOffset by animateDpAsState(
        targetValue = if (isDragging) (-14).dp else 0.dp,
        animationSpec = tween(durationMillis = 180)
    )
    val shadowScale by animateFloatAsState(
        targetValue = if (isDragging) 0.5f else 1.0f,
        animationSpec = tween(durationMillis = 180)
    )
    val shadowAlpha by animateFloatAsState(
        targetValue = if (isDragging) 0.15f else 0.4f,
        animationSpec = tween(durationMillis = 180)
    )

    suspend fun animateToLocation(location: Coordinates) {
        cameraState.animateTo(
            finalPosition = CameraPosition(
                target = Position(longitude = location.longitude, latitude = location.latitude),
                zoom = 13.0
            ),
            duration = 2.seconds
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(22.dp),
                imageVector = Icons.Default.Map,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                modifier = Modifier.weight(1f),
                text = "Coordinates",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            IconButton(
                content = { Icon(Icons.Default.MyLocation, "Set my current location") },
                onClick = { scope.launch { onValueChange(LocationManager.getCurrentLocation()) } }
            )
        }
        AnimatedVisibility(error != null) {
            if (error != null) {
                Surface(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = error,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().then(
                other = if (getWindowSizeClass() == WindowWidthSizeClass.Compact) {
                    Modifier.aspectRatio(1f)
                } else {
                    Modifier.height(300.dp)
                }
            ),
            contentAlignment = Alignment.Center
        ) {
            val appTheme = ThemeState.current.value
            val mapTheme = if (appTheme == ThemeMode.Dark || appTheme == ThemeMode.System && isSystemInDarkTheme()) "fiord" else "liberty"
            MaplibreMap(
                modifier = Modifier.fillMaxSize(),
                baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/$mapTheme"),
                cameraState = cameraState,
                options = MapOptions(
                    ornamentOptions = OrnamentOptions.AllEnabled
                ),
                onMapLoadFinished = {
                    if (value == null) {
                        scope.launch {
                            val currentLocation = LocationManager.getCurrentLocation()
                            if (currentLocation != null) {
                                onValueChange(currentLocation)
                            }
                        }
                    }
                    mapLoadFinished = true
                }
            )
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 3.dp)
                    .align(Alignment.Center)
                    .scale(shadowScale)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = shadowAlpha),
                        shape = CircleShape
                    )
            )
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Selection Marker",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center)
                    // -18.dp places the bottom tip exactly on center coordinates.
                    // pinLiftOffset applies the runtime responsive UX lift behavior.
                    .offset(y = (-18).dp + pinLiftOffset)
            )
        }
        TextFields(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = value,
            onValueChange = onValueChange
        )
        Spacer(Modifier.height(8.dp))
    }

    var valueSetByUserGesture by remember { mutableStateOf<Coordinates?>(null) }

    LaunchedEffect(value, mapLoadFinished) {
        if (mapLoadFinished && value != null && valueSetByUserGesture != value) {
            animateToLocation(value)
        }
    }
    LaunchedEffect(cameraState) {
        cameraState.awaitProjection()
        snapshotFlow { Pair(cameraState.moveReason, cameraState.position) }
            .filter { it.first == CameraMoveReason.GESTURE }
            .distinctUntilChanged()
            // Wait for 200ms of no camera position changes to confirm it finished moving (handles drags + inertia)
            .debounce(200.milliseconds)
            .mapNotNull { cameraState.projection?.queryEventViewport(cameraState.position.target) }
            .distinctUntilChanged()
            .collect {
                val newValue = Coordinates(latitude = it.latitude, longitude = it.longitude)
                valueSetByUserGesture = newValue
                onValueChange(newValue)
            }
    }
}

@Composable
private fun TextFields(
    modifier: Modifier = Modifier,
    value: Coordinates?,
    onValueChange: (Coordinates?) -> Unit
) {
    var latitude by remember { mutableStateOf(value?.latitude) }
    var longitude by remember { mutableStateOf(value?.longitude) }

    fun onLatChange(value: Double?) {
        latitude = value
        if (value != null && longitude != null) {
            onValueChange(Coordinates(latitude = value, longitude = longitude!!))
        } else {
            onValueChange(null)
        }
    }
    fun onLongChange(value: Double?) {
        longitude = value
        if (value != null && latitude != null) {
            onValueChange(Coordinates(latitude = latitude!!, longitude = value))
        } else {
            onValueChange(null)
        }
    }

    LaunchedEffect(value) {
        if (value != null) {
            latitude = value.latitude
            longitude = value.longitude
        }
    }

    AdaptiveUi(
        wide = {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LatitudeField(Modifier.weight(1f), value?.latitude, ::onLatChange)
                LongitudeField(Modifier.weight(1f), value?.longitude, ::onLongChange)
            }
        },
        compact = {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LatitudeField(Modifier.fillMaxWidth(), value?.latitude, ::onLatChange)
                LongitudeField(Modifier.fillMaxWidth(), value?.longitude, ::onLongChange)
            }
        }
    )
}

@Composable
private fun LatitudeField(
    modifier: Modifier = Modifier,
    value: Double?,
    onValueChange: (Double?) -> Unit
) {
    var valueStr by remember { mutableStateOf(value?.toString() ?: "") }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (value != null) {
            valueStr = value.toString()
            error = false
        }
    }

    OutlinedTextField(
        modifier = modifier,
        value = valueStr,
        onValueChange = {
            valueStr = it
            val parsed = it.toDoubleOrNull()
            if (parsed != null && parsed in -90.0..90.0) {
                onValueChange(parsed)
                error = false
            } else {
                error = true
                onValueChange(null)
            }
        },
        label = { Text("Latitude") },
        placeholder = { Text("e.g. 40.7128") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        minLines = 1,
        maxLines = 1,
        isError = error,
        supportingText = if (error) {
            @Composable { Text("Valid values are a decimal in the range: [-90.0, 90.0]") }
        } else null
    )
}

@Composable
private fun LongitudeField(
    modifier: Modifier = Modifier,
    value: Double?,
    onValueChange: (Double?) -> Unit
) {
    var valueStr by remember { mutableStateOf(value?.toString() ?: "") }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (value != null) {
            valueStr = value.toString()
            error = false
        }
    }

    OutlinedTextField(
        modifier = modifier,
        value = valueStr,
        onValueChange = {
            valueStr = it
            val parsed = it.toDoubleOrNull()
            if (parsed != null && parsed in -180.0..180.0) {
                onValueChange(parsed)
                error = false
            } else {
                error = true
                onValueChange(null)
            }
        },
        label = { Text("Longitude") },
        placeholder = { Text("e.g. -74.0060") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        minLines = 1,
        maxLines = 1,
        isError = error,
        supportingText = if (error) {
            @Composable { Text("Valid values are a decimal in the range: [-180.0, 180.0]") }
        } else null
    )
}
