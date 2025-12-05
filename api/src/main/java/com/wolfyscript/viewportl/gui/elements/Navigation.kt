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
    // TODO: Cache graph calculation
    val scope = NavHostScope()
    graph(scope)

    val lastKey = backstack.lastOrNull() ?: return

    for (entry in scope.entries) {
        if (entry.tryRunContent(lastKey)) {
            break
        }
    }
}

class NavHostScope {

    val entries: MutableList<NavEntry<*>> = mutableListOf()

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

/**
 * Identifies a Route in the Navigator.
 *
 * It can be extended by any arbitrary class and can contain data that can be used as a way to pass arguments to other paths
 *
 * **Any data this Key holds must be immutable! The Keys are added to the backstack and may persist for a long time.**
 * **Care needs to be taken when storing data in Keys, as they may prevent garbage collection! Do not store Minecraft objects, Bukkit objects, or similar objects!!!**
 */
interface NavKey