package com.wolfyscript.viewportl.common.gui.interaction

import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.gui.interaction.ClickInfo

interface CommonClickInfoImpl {

    class Primary(override val slot: Int, override val cursorStack: ItemStack?, override val currentStack: ItemStack?) :
        ClickInfo.Primary

    class Secondary(override val slot: Int, override val cursorStack: ItemStack?, override val currentStack: ItemStack?) :
        ClickInfo.Secondary

    class Middle(override val slot: Int, override val cursorStack: ItemStack?, override val currentStack: ItemStack?) :
        ClickInfo.Middle

    class Double(override val slot: Int, override val cursorStack: ItemStack?, override val currentStack: ItemStack?) :
        ClickInfo.Double

    class NumberPress(
        override val slot: Int,
        override val number: Int,
        override val cursorStack: ItemStack?,
        override val currentStack: ItemStack?
    ) : ClickInfo.NumberPress

    interface Drop {

        interface Outside {

            class Primary(
                override val slot: Int,
                override val cursorStack: ItemStack?,
                override val currentStack: ItemStack?
            ) : ClickInfo.Drop.Outside.Primary

            class Secondary(
                override val slot: Int,
                override val cursorStack: ItemStack?,
                override val currentStack: ItemStack?
            ) : ClickInfo.Drop.Outside.Secondary

        }

        class Full(
            override val slot: Int,
            override val cursorStack: ItemStack?,
            override val currentStack: ItemStack?
        ) : ClickInfo.Drop.Full

        class Single(
            override val slot: Int,
            override val cursorStack: ItemStack?,
            override val currentStack: ItemStack?
        ) : ClickInfo.Drop.Single

    }

    interface Shift {

        class Primary(
            override val slot: Int,
            override val cursorStack: ItemStack?,
            override val currentStack: ItemStack?
        ) : ClickInfo.Shift.Primary

        class Secondary(
            override val slot: Int,
            override val cursorStack: ItemStack?,
            override val currentStack: ItemStack?
        ) : ClickInfo.Shift.Secondary

    }

    interface Drag {

        class Primary(
            override val slot: Int,
            override val cursorStack: ItemStack?,
            override val currentStack: ItemStack?
        ) : ClickInfo.Drag.Primary

        class Secondary(
            override val slot: Int,
            override val cursorStack: ItemStack?,
            override val currentStack: ItemStack?
        ) : ClickInfo.Drag.Secondary

        class Middle(
            override val slot: Int,
            override val cursorStack: ItemStack?,
            override val currentStack: ItemStack?
        ) : ClickInfo.Drag.Middle
    }
}
