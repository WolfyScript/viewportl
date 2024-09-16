package com.wolfyscript.viewportl.common.gui.components

import com.wolfyscript.scafall.function.ReceiverBiFunction
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.components.ButtonIcon
import com.wolfyscript.viewportl.gui.components.ButtonProperties
import com.wolfyscript.viewportl.gui.components.ComponentScope
import com.wolfyscript.viewportl.gui.components.GroupProperties
import com.wolfyscript.viewportl.gui.components.NativeComponent
import com.wolfyscript.viewportl.gui.components.OutletProperties
import com.wolfyscript.viewportl.gui.components.RouterProperties
import com.wolfyscript.viewportl.gui.components.RouterScope
import com.wolfyscript.viewportl.gui.components.ShowProperties
import com.wolfyscript.viewportl.gui.components.SlotProperties
import com.wolfyscript.viewportl.gui.components.component
import com.wolfyscript.viewportl.gui.interaction.ClickTransaction
import com.wolfyscript.viewportl.gui.interaction.ClickType
import com.wolfyscript.viewportl.gui.interaction.DragTransaction
import com.wolfyscript.viewportl.gui.reactivity.*
import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import net.kyori.adventure.sound.Sound
import java.util.function.Consumer

class ComponentScopeImpl(override val runtime: ViewRuntimeImpl<*, *>, override val parent: ComponentScope? = null) : ComponentScope,
    ReactiveSource by runtime.reactiveSource {

    override var component: NativeComponent? = null

    fun setComponent(component: NativeComponent): Long {
        val id = runtime.model.addNode(component)
        this.component = component
        runtime.model.insertNodeAsChildOf(id, parent?.component?.nodeId ?: 0)
        return id
    }

    override fun interval(intervalInTicks: Long, runnable: Runnable) {
        val task = runtime.viewportl.scafall.scheduler.task(runtime.viewportl.scafall.corePlugin)
            .interval(intervalInTicks)
            .delay(1)
            .execute(Runnable{
                runnable.run()
                runtime.reactiveSource.runEffects()
            })
            .build()

        createCleanup {
            task.cancel()
        }
    }

    override fun group(
        styles: RenderProperties.() -> Unit,
        content: ComponentScope.() -> Unit
    ) = component(this, runtime) {
        runtime.viewportl.guiFactory.componentFactory.group(GroupProperties(this, styles, content))
    }

    override fun button(
        icon: ButtonIcon.() -> Unit,
        styles: RenderProperties.() -> Unit,
        sound: Sound?,
        onClick: (ClickTransaction.() -> Unit)?
    ) = component(this, runtime) {
        runtime.viewportl.guiFactory.componentFactory.button(
            ButtonProperties(
                this,
                icon,
                styles,
                sound,
                onClick
            )
        )
    }

    override fun outlet() = component(this, runtime) {
        runtime.viewportl.guiFactory.componentFactory.outlet(
            OutletProperties(
                this
            )
        )
    }

    override fun router(routes: RouterScope.() -> Unit) = component(this, runtime) {
        runtime.viewportl.guiFactory.componentFactory.router(
            RouterProperties(
                this,
                routes
            )
        )
    }

    override fun show(
        condition: () -> Boolean,
        fallback: ComponentScope.() -> Unit,
        content: ComponentScope.() -> Unit
    ) = component(this, runtime) {
        runtime.viewportl.guiFactory.componentFactory.show(
            ShowProperties(
                this, condition, fallback, content
            )
        )
    }

    override fun slot(
        value: () -> ItemStack?,
        styles: RenderProperties.() -> Unit,
        onValueChange: Consumer<ItemStack?>?,
        canPickUpStack: ReceiverBiFunction<ClickType, ItemStack, Boolean>?
    ) = component(this, runtime) {
        runtime.viewportl.guiFactory.componentFactory.slot(
            SlotProperties(
                this,
                value,
                styles,
                onValueChange,
                canPickUpStack
            )
        )
    }

}