package com.wolfyscript.viewportl.gui.model

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.viewportl
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.EmptyCoroutineContext

val StoreCoroutineScopeKey = Key.viewportl("coroutine_scope")

/**
 * A storage for persistent UI data.
 */
abstract class Store {

    private val lock = Any()
    private val closeables: MutableMap<Key, AutoCloseable> = mutableMapOf()

    // Without this, uncaught exceptions would be hidden and never printed to the logger
    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        ScafallProvider.get().logger.error("[Store@${this::class.simpleName}] An error occurred in coroutine ${context[CoroutineName] ?: ""}", throwable)
    }
    protected val storeCoroutineScope: CoroutineScope
        get() {
            return getClosable(StoreCoroutineScopeKey)
                ?: CoroutineScope(EmptyCoroutineContext + exceptionHandler + SupervisorJob()).also {
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
