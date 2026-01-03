package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.model.*
import com.wolfyscript.viewportl.viewportl
import java.util.*

/**
 * Handles navigation between different [Composables][Composable] bound to a Key.
 *
 *
 *
 */
@Composable
fun NavigationRoot(
    backstack: SnapshotStateList<NavKey>,
    sharedNavigation: Boolean = true,
    paths: @DisallowComposableCalls NavPathBuilderScope.() -> Unit,
) {
    val scope = remember(sharedNavigation, paths) {
        ScafallProvider.get().logger.info("Recalculating NavigationRoot paths: $paths")
        NavPathBuilderScope().also { paths(it) }
    }
    val keysInBackstack = remember(sharedNavigation, backstack) {
        backstack.map { it }.toSet()
    }
    val lastKey = remember(sharedNavigation, backstack) { backstack.lastOrNull() } ?: return

    for (entry in scope.entries) {
        if (entry.tryRunContent(lastKey, sharedNavigation, keysInBackstack)) {
            break
        }
    }
}

/**
 * Identifies a Route in the Navigator.
 *
 * It can be extended by any arbitrary class and can contain data that can be used as a way to pass arguments to other paths
 *
 * **Any data this Key holds must be immutable! The Keys are added to the backstack and may persist for a long time.**
 * **Care needs to be taken when storing data in Keys, as they may prevent garbage collection! Do not store Minecraft objects, Bukkit objects, or similar objects!!!**
 */
interface NavKey

/**
 * Used to construct the navigation entries.
 */
class NavPathBuilderScope {

    val entries: MutableList<NavPathConfig<*>> = mutableListOf()

    /**
     * Defines a new path in the Navigation graph.
     *
     * The [NavKey] defined by [T] specifies the type of entry in the [backstack][NavigationRoot].
     * This [content] is shown whenever the latest entry in the backstack matches [T].
     *
     * @see NavKey
     */
    inline fun <reified T : NavKey> composable(noinline content: @Composable (T) -> Unit) {
        entries.add(NavPathConfig(T::class.java, content))
    }

}

val NavigationStoreMapKey = Key.viewportl("navigation")

/**
 * Holds the type of key and content that is rendered when the entry is active.
 */
data class NavPathConfig<T : NavKey>(
    val type: Class<T>,
    val content: @Composable (T) -> Unit,
    ) {

    /**
     * Runs the content lambda defined for this route based on the [key].
     *
     * @param key The current key for this route
     * @param keysInBackstack A set of **unique** [NavKeys][NavKey] that are currently in the backstack
     */
    @Composable
    fun tryRunContent(key: NavKey, sharedNavigation: Boolean, keysInBackstack: Set<NavKey>): Boolean {
        if (type.isInstance(key)) {
            val currentViewer = LocalView.current.viewer
            val currentStoreOwner = LocalStoreOwner.current
            val navEntryStoresMap = if (sharedNavigation) {
                sharedStore<NavEntryStoresMap>(NavigationStoreMapKey)
            } else {
                store<NavEntryStoresMap>(NavigationStoreMapKey)
            }
            val currentViewerEntryStoresMap = if (sharedNavigation) {
                store<NavEntryStoresMap>(NavigationStoreMapKey)
            } else {
                navEntryStoresMap
            }

            DisposableEffect(key1 = key) {
                onDispose {
                    if (!keysInBackstack.contains(key)) {
                        navEntryStoresMap.clearStoreFor(key)
                        onPop()
                    }
                }
            }

            val storeOwner = remember {
                object : StoreOwner {
                    override val sharedStore: DataStoreMap
                        get() {
                            return navEntryStoresMap.storeFor(key)
                        }

                    override fun getViewerStore(viewer: UUID): DataStoreMap {
                        if (viewer != currentViewer) {
                            // Requested a store for a viewer outside of this View!
                            // Need to be careful doing so, because this won't get cleared when it leaves the composition.
                            // TODO: Throw an exception instead to disallow accessing other viewers stores?!?
                            val store = currentStoreOwner.getViewerStore(viewer)
                            val existing = store[NavigationStoreMapKey]
                                ?: NavEntryStoresMap().also { store[NavigationStoreMapKey] = it }
                            return existing.storeFor(key)
                        }
                        return currentViewerEntryStoresMap.storeFor(key)
                    }
                }
            }

            CompositionLocalProvider(LocalStoreOwner provides storeOwner) {
                content(type.cast(key))
            }
            return true
        }
        return false
    }

    fun onPop() {
        // TODO: support for decorator/modifier system similar to navigation3
    }

}

internal class NavEntryStoresMap : Store() {

    private val stores = mutableMapOf<NavKey, DataStoreMap>()

    fun storeFor(key: NavKey): DataStoreMap {
        return stores.getOrPut(key) { ScafallProvider.get().viewportl.guiFactory.dataStoreFactory.createDataStoreMap() }
    }

    fun clearStoreFor(key: NavKey) {
        stores.remove(key)?.clear()
    }

    override fun onCleared() {
        stores.values.forEach { it.clear() }
    }

}
