package com.wolfyscript.viewportl.common.gui.elements

import com.wolfyscript.scafall.wrappers.world.items.ItemStackLike
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.interaction.ClickInfo
import com.wolfyscript.viewportl.gui.interaction.ClickType
import com.wolfyscript.viewportl.gui.reactivity.ReactiveSource
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
        val task = runtime.viewportl.scafall.scheduler.task(runtime.viewportl.scafall.modInfo)
            .interval(intervalInTicks)
            .delay(1)
            .execute(Runnable{
                runnable.run()
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
        content: ComponentScope.() -> Unit,
        fallback: ComponentScope.() -> Unit
    ) = component(this, runtime) {
        runtime.viewportl.guiFactory.elementFactory.show(
            ShowProperties(
                this, condition, fallback, content
            )
        )
    }

    override fun slot(
        value: () -> ItemStackLike?,
        styles: RenderProperties.() -> Unit,
        onValueChange: ((ScafallItemStack) -> Unit)?,
        canPickUpStack: (ClickType.(ScafallItemStack) -> Boolean)?
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