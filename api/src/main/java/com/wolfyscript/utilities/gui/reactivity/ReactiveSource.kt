/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
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
package com.wolfyscript.utilities.gui.reactivity

import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer
import com.wolfyscript.utilities.gui.functions.ReceiverFunction
import com.wolfyscript.utilities.gui.functions.SerializableRunnable
import com.wolfyscript.utilities.gui.functions.SignalableReceiverFunction
import com.wolfyscript.utilities.platform.Platform
import org.apache.commons.lang3.function.TriFunction
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function

interface ReactiveSource {

    /**
     * Creates a trigger that can be tracked, and notify its subscribers.
     * It does not contain a value, so it is for cases where a simple update notification and no value is required.
     *
     * To track the trigger inside an Effect/Memo you need to call the [Trigger.track] method inside it.<br>
     * To notify subscribers call the [Trigger.update] method.
     *
     */
    fun createTrigger() : Trigger

    /**
     * Creates a Signal that holds a value of the specified [valueType].
     * The [defaultValueProvider] needs to provide a Non-null default value.
     *
     * When [Signal] values are accessed inside a [Memo]/[Effect] it subscribes to that [Signal].
     * Then when the value of the [Signal] is updated the [Memo]/[Effect] is updated too.
     */
    fun <T : Any> createSignal(valueType: Class<T>, defaultValueProvider: ReceiverFunction<ViewRuntime, T>): Signal<T>

    /**
     * Creates an [Effect] that reruns when a [Signal]/[Memo] used inside it is updated.
     *
     */
    fun createEffect(effect: Runnable): Effect {
        return createEffect<Unit> {
            effect.run()
        }
    }

    /**
     * Creates an [Effect] that reruns when a [Signal]/[Memo] used inside it is updated.
     *
     * This type of [Effect] allows to use the value of the previous execution, and return the new value.<br>
     * When no value is required use the type [Unit]
     */
    fun <T> createEffect(effect: ReceiverFunction<T?, T>): Effect

    /**
     * Creates a Memo, that holds a value of the specified [valueType].
     *
     * It is a combination of an [Effect] and [Signal]. It keeps track of the value of the previous execution of [fn]
     * and other [Effects][Effect]/[Memos][Memo] can subscribe to it.
     *
     * It guarantees that subscribers are only updated when the value of [fn] is different from the previous value.
     */
    fun <T : Any> createMemo(valueType: Class<T>, fn: Function<T?, T?>) : Memo<T>

    /**
     * Must be used to fetch data from the main Minecraft thread (i.e. Entities, World, etc.).
     * This is because the GUI is run async on a different thread!
     *
     *
     * After creation, it runs the specified function on the main thread and fetches the data.
     * The signal gets updated when the data is available.
     * When the signal value is requested before the data is available it returns an empty Optional.
     *
     *
     * @param fetch The function to run on the main thread.
     * @return A signal that contains an Optional wrapping the fetched data; empty by default; non-empty when data has been fetched
     * @param <T> The type of the value
    </T> */
    fun <T> resourceSync(fetch: BiFunction<Platform, ViewRuntime, T>): Signal<Optional<T>>

    fun <I, T> resourceSync(
        input: Signal<I>,
        fetch: TriFunction<Platform, ViewRuntime, I, T>
    ): Signal<Optional<T>>

    /**
     * May be used to fetch data async.
     *
     *
     *
     * After creation, it runs the specified function async and fetches the data.
     * The signal gets updated when the data is available.
     * When the signal value is requested before the data is available it returns an empty Optional.
     *
     *
     * @param fetch The function to run async
     * @return A signal that contains an Optional wrapping the fetched data; empty by default; non-empty when data has been fetched
     * @param <T> The type of the value
    </T> */
    fun <T> resourceAsync(fetch: BiFunction<Platform, ViewRuntime, T>): Signal<Optional<T>>

}

/**
 * Creates a Memo, that holds a value of the specified type [T].
 *
 * It is a combination of an [Effect] and [Signal]. It keeps track of the value of the previous execution of [fn]
 * and other [Effects][Effect]/[Memos][Memo] can subscribe to it.
 *
 * It guarantees that subscribers are only updated when the value of [fn] is different from the previous value.
 */
inline fun <reified T: Any> ReactiveSource.createMemo(fn: Function<T?, T?>) : Memo<T> {
    return createMemo(T::class.java, fn)
}

inline fun <reified T : Any> ReactiveSource.createSignal(defaultValue: T): Signal<T> {
    return createSignal(T::class.java) { defaultValue }
}

inline fun <reified T : Any> ReactiveSource.createSignal(defaultValueProvider: ReceiverFunction<ViewRuntime, T>): Signal<T> {
    return createSignal(T::class.java, defaultValueProvider)
}
