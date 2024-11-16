package com.wolfyscript.viewportl.common.gui.elements

import com.wolfyscript.scafall.function.ReceiverBiFunction
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.elements.ButtonIcon
import com.wolfyscript.viewportl.gui.elements.ButtonProperties
import com.wolfyscript.viewportl.gui.elements.ComponentScope
import com.wolfyscript.viewportl.gui.elements.GroupProperties
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.elements.RouterProperties
import com.wolfyscript.viewportl.gui.elements.RouterScope
import com.wolfyscript.viewportl.gui.elements.ShowProperties
import com.wolfyscript.viewportl.gui.elements.SlotProperties
import com.wolfyscript.viewportl.gui.elements.component
import com.wolfyscript.viewportl.gui.interaction.ClickInfo
import com.wolfyscript.viewportl.gui.interaction.ClickType
import com.wolfyscript.viewportl.gui.reactivity.*
import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import net.kyori.adventure.sound.Sound
import java.util.function.Consumer

class ComponentScopeImpl(override val runtime: ViewRuntimeImpl<*, *>, override val parent: ComponentScope? = null) : ComponentScope,
    ReactiveSource by runtime.reactiveSource {

    override var component: Element? = null

    fun setComponent(component: Element): Long {
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
        runtime.viewportl.guiFactory.elementFactory.group(GroupProperties(this, styles, content))
    }

    override fun button(
        icon: ButtonIcon.() -> Unit,
        styles: RenderProperties.() -> Unit,
        sound: Sound?,
        onClick: (ClickInfo.() -> Unit)?
    ) = component(this, runtime) {
        runtime.viewportl.guiFactory.elementFactory.button(
            ButtonProperties(
                this,
                icon,
                styles,
                sound,
                onClick
            )
        )
    }

    override fun router(routes: RouterScope.() -> Unit) = component(this, runtime) {
        runtime.viewportl.guiFactory.elementFactory.router(
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
        runtime.viewportl.guiFactory.elementFactory.show(
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
        runtime.viewportl.guiFactory.elementFactory.slot(
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