package com.wolfyscript.viewportl.gui.model

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.viewportl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.EmptyCoroutineContext

val StoreCoroutineScopeKey = Key.viewportl("coroutine_scope")

/**
 * A storage for persistent UI data.
 */
abstract class Store {

    private val lock = Any()
    private val closeables: MutableMap<Key, AutoCloseable> = mutableMapOf()

    val storeCoroutineScope: CoroutineScope
        get() {
            return getClosable(StoreCoroutineScopeKey)
                ?: CoroutineScope(EmptyCoroutineContext + SupervisorJob()).also {
                    addClosable(StoreCoroutineScopeKey, it.asCloseable())
                }
        }

    fun addClosable(key: Key, closeable: AutoCloseable) {
        val old = synchronized(lock) {
            closeables.put(key, closeable)
        }
        old?.close()
    }

    fun <T: AutoCloseable> getClosable(key: Key): T? = synchronized(lock) {
        closeables[key] as T?
    }

    fun clear() {
        synchronized(lock) {
            for (closeable in closeables.values) {
                closeable.close()
            }
        }
        onCleared()
    }

    open fun onCleared() {}

}
