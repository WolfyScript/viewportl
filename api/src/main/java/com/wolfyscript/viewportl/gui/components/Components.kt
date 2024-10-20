package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.scafall.function.ReceiverBiFunction
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.interaction.ClickInfo
import com.wolfyscript.viewportl.gui.interaction.ClickType
import com.wolfyscript.viewportl.gui.reactivity.ReactiveSource
import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import net.kyori.adventure.sound.Sound
import java.util.function.Consumer
import kotlin.reflect.KProperty

/**
 * A setup function used to create a component with a new [ComponentScope].
 *
 * These components are not actually present in the graph, but instead aggregate [NativeComponents][NativeComponent] and
 * allow for better structuring of ui components and encapsulate signals, memos, effects, etc.
 *
 * In the end only the [NativeComponents][NativeComponent] are present in the component graph,
 * while the component functions setup all reactive nodes for e.g. updating native component properties.
 *
 */
fun component(componentScope: ComponentScope? = null, runtime: ViewRuntime<*,*>, fn: ComponentScope.() -> Unit) {
    runtime.viewportl.guiFactory.runComponentFunction(runtime, componentScope, fn)
}

/**
 * The ComponentScope is the scope of a component function in which signals, memos, effects, intervals and more can be created.
 */
interface ComponentScope : ReactiveSource {

    val runtime : ViewRuntime<*,*>

    val component: NativeComponent?

    val parent: ComponentScope?

    /**
     * Adds a task that is run periodically while the Window is open.
     *
     * @param runnable The task to run, may update signals
     * @param intervalInTicks The interval for the task in ticks
     * @return This builder for chaining
     */
    fun interval(intervalInTicks: Long, runnable: Runnable)

    fun group(styles: RenderProperties.() -> Unit = {}, content: ComponentScope.() -> Unit)

    /**
     * A simple button that can execute actions when clicked and change its icon.
     *
     * ###### Sound
     * By default, it uses the default Button sound from Minecraft, on interaction.
     * While the sound can be removed, it is highly recommended to play a sound on interaction to notify the player that an action has been recognised!
     *
     * ###### Native Component
     * Buttons are a native component meaning they have a platform specific implementation that handles both interactions and rendering.
     * Only native components will be present in the component graph, non-native components don't really exist, they just group native components together.
     */
    fun button(
        icon: ButtonIcon.() -> Unit,
        styles: RenderProperties.() -> Unit = {},
        sound: Sound? = Sound.sound(Key.parse("minecraft:ui.button.click").into(), Sound.Source.MASTER, 0.25f, 1f),
        onClick: (ClickInfo.() -> Unit)? = null
    )

    fun outlet()

    /**
     * A Router decides which child component it should show depending on the current path.
     *
     * ### Navigation
     * Navigation between paths is possible within the [RouterScope]. For that the [RouterScope] may be passed to child components.
     *
     */
    fun router(routes: RouterScope.() -> Unit)

    /**
     * The show component renders the content depending on the condition.
     *
     * When the [condition] evaluates to true then it renders the [content], otherwise renders the [fallback].
     * Both the [content] and [fallback] components are added as children to the show component.
     */
    fun show(
        condition: () -> Boolean,
        fallback: ComponentScope.() -> Unit = {},
        content: ComponentScope.() -> Unit
    )

    fun slot(
        value: () -> ItemStack?,
        styles: RenderProperties.() -> Unit,
        onValueChange: Consumer<ItemStack?>? = null,
        canPickUpStack: ReceiverBiFunction<ClickType, ItemStack, Boolean>? = null,
    )

}

class DynamicProperty<O : NativeComponent, T>(
    val runtime: ViewRuntime<*,*>,
    initial: T
) {
    private var value: T = initial

    operator fun getValue(thisRef: O, property: KProperty<*>) : T {
        return value
    }

    operator fun setValue(thisRef: O, property: KProperty<*>, value: T) {
        this.value = value
        runtime.model.updateNode(thisRef.nodeId)
    }

}
