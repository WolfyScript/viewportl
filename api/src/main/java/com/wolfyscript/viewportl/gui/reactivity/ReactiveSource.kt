/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.wolfyscript.viewportl.gui.reactivity

import com.wolfyscript.scafall.function.ReceiverFunction
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.ViewRuntime
import java.util.*

interface ReactiveSource {

    /**
     * Creates a trigger that can be tracked, and notify its subscribers.
     * It does not contain a value, so it is for cases where a simple update notification and no value is required.
     *
     * To track the trigger inside an Effect/Memo you need to call the [Trigger.track] method inside it.<br>
     * To notify subscribers call the [Trigger.updateIfNecessary] method.
     *
     */
    fun createTrigger(): Trigger

    /**
     * Creates a Signal that holds a value of the specified [valueType].
     * The [defaultValueProvider] needs to provide a Non-null default value.
     *
     * When [Signal] values are accessed inside a [Memo]/[Effect] it subscribes to that [Signal].
     * Then when the value of the [Signal] is updated the [Memo]/[Effect] is updated too.
     */
    fun <T : Any?> createSignal(
        valueType: Class<T>,
        defaultValueProvider: ReceiverFunction<ViewRuntime<*, *>, T>,
    ): Signal<T>

    /**
     * Creates an [Effect] that reruns when a [Signal]/[Memo] used inside it is updated.
     *
     */
    fun createEffect(effect: () -> Unit): Effect

    /**
     * Creates a Memo, that holds a value of the specified [valueType].
     *
     * It is a combination of an [Effect] and [Signal]. It keeps track of the value of the previous execution of [fn]
     * and other [Effects][Effect]/[Memos][Memo] can subscribe to it.
     *
     * It guarantees that subscribers are only updated when the value of [fn] is different from the previous value.
     */
    fun <T : Any?> createMemo(initialValue: T, valueType: Class<T>, fn: (T) -> T): Memo<T>

    /**
     * Creates a function that is run when the current owner is removed.
     */
    fun createCleanup(cleanup: Cleanup)

    /**
     * Fetches data asynchronously and returns the result once available.
     *
     * After creation, it runs the specified function async and fetches the data.
     * The signal gets updated when the data is available.
     *
     * When the value is requested before the data is available it returns an empty Optional.
     *
     * @param fetch The function to run async
     * @return A signal for an Optional wrapping a [Result]; otherwise [Optional.empty] when not yet fetched
     * @param <T> The type of the value
     */
    fun <I, T : Any> resourceAsync(
        initialValue: Result<T>?,
        inputType: Class<I?>,
        input: () -> I?,
        fetch: (I, Viewportl, ViewRuntime<*, *>) -> Result<T>,
    ): Pair<Signal<Result<T>?>, () -> Unit>

}

/**
 * Creates a Memo, that holds a value of the specified type [T].
 *
 * It is a combination of an [Effect] and [Signal]. It keeps track of the value of the previous execution of [fn]
 * and other [Effects][Effect]/[Memos][Memo] can subscribe to it.
 *
 * It guarantees that subscribers are only updated when the value of [fn] is different from the previous value.
 */
inline fun <reified T : Any?> ReactiveSource.createMemo(initialValue: T, noinline fn: (T) -> T): Memo<T> {
    return createMemo(initialValue, T::class.java, fn)
}

inline fun <reified T : Any?> ReactiveSource.createSignal(defaultValue: T): Signal<T> {
    return createSignal(T::class.java) { defaultValue }
}

inline fun <reified T : Any?> ReactiveSource.createSignal(defaultValueProvider: ReceiverFunction<ViewRuntime<*, *>, T>): Signal<T> {
    return createSignal(T::class.java, defaultValueProvider)
}
