package com.wolfyscript.viewportl.gui.elements


data class MatchProperties<V>(
    val scope: ComponentScope,
    val input: () -> V,
    val cases: MatchScope<V>.() -> Unit
)

/**
 * The actual match element
 */
interface Match : Element

interface MatchScope<V> {

    fun case(condition: (V) -> Boolean, view: ComponentScope.() -> Unit)

}