package com.wolfyscript.viewportl.common.gui.interaction

import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.viewportl.gui.interaction.ClickInfo

interface CommonClickInfoImpl {

    class Primary(override val slot: Int, override val cursorStack: ScafallItemStack?, override val currentStack: ScafallItemStack?) :
        ClickInfo.Primary

    class Secondary(override val slot: Int, override val cursorStack: ScafallItemStack?, override val currentStack: ScafallItemStack?) :
        ClickInfo.Secondary

    class Middle(override val slot: Int, override val cursorStack: ScafallItemStack?, override val currentStack: ScafallItemStack?) :
        ClickInfo.Middle

    class Double(override val slot: Int, override val cursorStack: ScafallItemStack?, override val currentStack: ScafallItemStack?) :
        ClickInfo.Double

    class NumberPress(
        override val slot: Int,
        override val number: Int,
        override val cursorStack: ScafallItemStack?,
        override val currentStack: ScafallItemStack?
    ) : ClickInfo.NumberPress

    interface Drop {

        interface Outside {

            class Primary(
                override val slot: Int,
                override val cursorStack: ScafallItemStack?,
                override val currentStack: ScafallItemStack?
            ) : ClickInfo.Drop.Outside.Primary

            class Secondary(
                override val slot: Int,
                override val cursorStack: ScafallItemStack?,
                override val currentStack: ScafallItemStack?
            ) : ClickInfo.Drop.Outside.Secondary

        }

        class Full(
            override val slot: Int,
            override val cursorStack: ScafallItemStack?,
            override val currentStack: ScafallItemStack?
        ) : ClickInfo.Drop.Full

        class Single(
            override val slot: Int,
            override val cursorStack: ScafallItemStack?,
            override val currentStack: ScafallItemStack?
        ) : ClickInfo.Drop.Single

    }

    interface Shift {

        class Primary(
            override val slot: Int,
            override val cursorStack: ScafallItemStack?,
            override val currentStack: ScafallItemStack?
        ) : ClickInfo.Shift.Primary

        class Secondary(
            override val slot: Int,
            override val cursorStack: ScafallItemStack?,
            override val currentStack: ScafallItemStack?
        ) : ClickInfo.Shift.Secondary

    }

    interface Drag {

        class Primary(
            override val slot: Int,
            override val cursorStack: ScafallItemStack?,
            override val currentStack: ScafallItemStack?
        ) : ClickInfo.Drag.Primary

        class Secondary(
            override val slot: Int,
            override val cursorStack: ScafallItemStack?,
            override val currentStack: ScafallItemStack?
        ) : ClickInfo.Drag.Secondary

        class Middle(
            override val slot: Int,
            override val cursorStack: ScafallItemStack?,
            override val currentStack: ScafallItemStack?
        ) : ClickInfo.Drag.Middle
    }
}
