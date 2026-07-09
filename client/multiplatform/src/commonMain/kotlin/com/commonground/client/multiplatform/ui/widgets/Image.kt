package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

private typealias Key = String

@Composable
fun Image(
    modifier: Modifier = Modifier,
    url: String?,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center
) {
    val image = produceState<Result<ImageBitmap>>(Result.Loading, url) {
        value = if (url == null) Result.Error else Result.Success(Cache.get(url))
    }

    when (val img = image.value) {
        is Result.Error -> BrokenImage(modifier)
        is Result.Loading -> BrokenImage(modifier)
        is Result.Success<ImageBitmap> -> Image(
            modifier = modifier,
            bitmap = img.value,
            contentDescription = null,
            contentScale = contentScale,
            alignment = alignment
        )
    }
}

@Composable
fun Image(
    modifier: Modifier = Modifier,
    data: ByteArray?,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center
) {
    var loading by remember { mutableStateOf(true) }
    var image by remember { mutableStateOf<ImageBitmap?>(null) }

    if (data == null) {
        BrokenImage(modifier)
    } else {
        LaunchedEffect(data) {
            loading = true
            image = data.decodeToImageBitmap()
            loading = false
        }

        when (loading) {
            true -> BrokenImage(modifier)
            false -> {
                image?.let {
                    Image(
                        modifier = modifier,
                        bitmap = it,
                        contentDescription = null,
                        contentScale = contentScale,
                        alignment = alignment
                    )
                } ?: BrokenImage(modifier)
            }
        }
    }
}

@Composable
private fun BrokenImage(
    modifier: Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            imageVector = Icons.Default.BrokenImage,
            contentDescription = null
        )
    }
}

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    object Error : Result<Nothing>()
    class Success<T>(val value: T) : Result<T>()
}

private object Cache {
    private const val MAX_CACHE_SIZE = 100 * 1024 * 1024
    private val client by lazy { HttpClient() }

    private val cache = mutableMapOf<Key, Image>()
    private val mutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var cacheSize = 0

    suspend fun get(key: Key): ImageBitmap {
        val fromCache = cache[key]
        return if (fromCache == null) {
            mutex.withLock {
                val fromCache2 = cache[key]
                if (fromCache2 == null) {
                    val imageBitmap = client.get(key).bodyAsBytes().decodeToImageBitmap()
                    val image = Image(imageBitmap)
                    add(key to image)
                    while (cacheSize > MAX_CACHE_SIZE) {
                        val leastImportant = cache.minByOrNull { it.value.lastUsed }
                        if (leastImportant != null) {
                            remove(leastImportant.toPair())
                        }
                    }
                    image.bitmap
                } else {
                    fromCache2.updateLastUsed()
                    fromCache2.bitmap
                }
            }
        } else {
            fromCache.updateLastUsed()
            fromCache.bitmap
        }
    }

    private suspend fun add(entry: Pair<Key, Image>) {
        scope.launch {
            cache += entry
            cacheSize += entry.second.byteSize
        }.join()
    }

    private suspend fun remove(entry: Pair<Key, Image>) {
        scope.launch {
            cache.remove(entry.first)
            cacheSize -= entry.second.byteSize
        }.join()
    }

    private data class Image(val bitmap: ImageBitmap) {
        var lastUsed = Clock.System.now()
        val byteSize = bitmap.width * bitmap.height * 4

        fun updateLastUsed() { lastUsed = Clock.System.now() }
    }
}
