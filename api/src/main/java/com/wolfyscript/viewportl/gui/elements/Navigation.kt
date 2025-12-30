package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Handles navigation between different [Composables][Composable] bound to a Key.
 *
 *
 *
 */
@Composable
fun NavHost(
    backstack: SnapshotStateList<NavKey>,
    onBack: () -> Unit,
    graph: @DisallowComposableCalls NavHostScope.() -> Unit
) {
    val scope = remember { NavHostScope().also { graph(it) } }

    val lastKey = backstack.lastOrNull() ?: return

    for (entry in scope.entries) {
        if (entry.tryRunContent(lastKey)) {
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
class NavHostScope {

    val entries: MutableList<NavEntry<*>> = mutableListOf()

    /**
     * Defines a new path in the Navigation graph.
     *
     * The [NavKey] defined by [T] specifies the type of entry in the [backstack][NavHost].
     * This [content] is shown whenever the latest entry in the backstack matches [T].
     *
     * @see NavKey
     */
    inline fun <reified T: NavKey> composable(noinline content: @Composable (T) -> Unit) {
        entries.add(NavEntry(T::class.java, content))
    }

}

data class NavEntry<T: NavKey>(val type: Class<T>, val content: @Composable (T) -> Unit) {

    @Composable
    fun tryRunContent(key: NavKey): Boolean {
        if (type.isInstance(key)) {
            content(type.cast(key))
            return true
        }
        return false
    }

}
