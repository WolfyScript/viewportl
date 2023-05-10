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

package com.wolfyscript.utilities.common.gui;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.common.gui.components.ButtonState;
import java.util.Objects;

public class ButtonStateDefault implements ButtonState {

        private final String key;
        private final InteractionCallback interactionCallback;
        private final RenderCallback renderCallback;

        protected ButtonStateDefault(String key, InteractionCallback interactionCallback, RenderCallback renderCallback) {
            this.key = key;
            this.interactionCallback = interactionCallback;
            this.renderCallback = renderCallback;
        }

        @Override
        public InteractionCallback interactCallback() {
            return interactionCallback;
        }

        @Override
        public RenderCallback renderCallback() {
            return renderCallback;
        }

        @Override
        public String key() {
            return key;
        }

        public static class Builder implements ButtonState.Builder<ButtonState> {

            private String key;
            private InteractionCallback interactionCallback;
            private RenderCallback renderCallback;

            protected Builder(String ownerID) {
                this.key = ownerID;
            }

            @Override
            public Builder subKey(String subKey) {
                this.key += "." + subKey;
                return this;
            }

            @Override
            public Builder key(String key) {
                this.key = key;
                return this;
            }

            @Override
            public Builder interact(InteractionCallback interactionCallback) {
                this.interactionCallback = interactionCallback;
                return this;
            }

            @Override
            public Builder render(RenderCallback renderCallback) {
                this.renderCallback = renderCallback;
                return null;
            }

            @Override
            public ButtonStateDefault create() {
                Preconditions.checkNotNull(renderCallback, "Cannot create Component without a RenderCallback!");
                final var interactCallback = Objects.requireNonNullElseGet(this.interactionCallback, () -> (holder, state, details) -> InteractionResult.def());
                final var renderCallback = Objects.requireNonNullElseGet(this.renderCallback, () -> (holder, state) -> {});

                return new ButtonStateDefault(key, interactCallback, renderCallback);
            }
        }
    }
