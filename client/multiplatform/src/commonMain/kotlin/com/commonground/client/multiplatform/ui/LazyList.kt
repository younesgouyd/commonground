package com.commonground.client.multiplatform.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class LazyList<T>(
    private val coroutineScope: CoroutineScope,
    private val load: suspend (Int) -> Chunk<T>
) {
    private val _items: MutableStateFlow<List<T>> = MutableStateFlow(emptyList())
    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var next: Int? = 0
    private val mutex = Mutex()

    val items: StateFlow<List<T>> get() = _items.asStateFlow()
    val loading: StateFlow<Boolean> get() = _loading.asStateFlow()

    init {
        loadMore()
    }

    fun loadMore() {
        if (!_loading.value) {
            next?.let { nextOffsetNotNull ->
                coroutineScope.launch {
                    mutex.withLock {
                        _loading.value = true
                        val result = load(nextOffsetNotNull)
                        next = result.next
                        _items.update {
                            it + result.items
                        }
                        _loading.value = false
                    }
                }
            }
        }
    }

    data class Chunk<Item>(
        val items: List<Item>,
        val next: Int?
    )
}