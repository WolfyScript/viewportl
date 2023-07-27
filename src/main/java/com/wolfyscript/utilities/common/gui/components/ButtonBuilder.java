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

package com.wolfyscript.utilities.common.gui.components;

import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.functions.SerializableSupplier;
import com.wolfyscript.utilities.common.gui.signal.Signal;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import java.util.function.Consumer;

/**
 * Builder to create a {@link Button} instance.
 *
 */
public interface ButtonBuilder extends ComponentBuilder<Button, Component> {

    /**
     * Creates a new {@link IconBuilder} to create the icon of the button.
     *
     * @param icon The consumer that provides the {@link IconBuilder}
     * @return This builder instance for chaining.
     */
    ButtonBuilder icon(Consumer<IconBuilder> icon);

    ButtonBuilder interact(InteractionCallback interactionCallback);

    Button create(Component parent);

    /**
     * Provides methods to create an icon for Buttons.
     * Dynamic icons are recreated each time the component is re-rendered, while static icons are just created once and then reused.
     * By default, all icons are static to improve performance.
     */
    interface IconBuilder {

        IconBuilder stack(ItemStackConfig<?> stackConfig);

        IconBuilder stack(SerializableSupplier<ItemStackConfig<?>> stackConfigSupplier);

        IconBuilder updateOnSignals(Signal<?>... signals);

        ButtonIcon create();
    }

}
