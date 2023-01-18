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
import java.util.Objects;

public class ComponentStateDefault<D extends Data> implements ComponentState<D> {

        private final String key;
        private final InteractionCallback<D> interactionCallback;
        private final RenderPreCallback<D> renderPreCallback;
        private final RenderCallback<D> renderCallback;

        protected ComponentStateDefault(String key, InteractionCallback<D> interactionCallback, RenderPreCallback<D> renderPreCallback, RenderCallback<D> renderCallback) {
            this.key = key;
            this.interactionCallback = interactionCallback;
            this.renderPreCallback = renderPreCallback;
            this.renderCallback = renderCallback;
        }

        @Override
        public InteractionCallback<D> interactCallback() {
            return interactionCallback;
        }

        @Override
        public RenderCallback<D> renderCallback() {
            return renderCallback;
        }

        @Override
        public RenderPreCallback<D> renderPreCallback() {
            return renderPreCallback;
        }

        @Override
        public String key() {
            return key;
        }

        public static class Builder<D extends Data> implements ComponentState.Builder<D, ComponentState<D>> {

            private String key;
            private InteractionCallback<D> interactionCallback;
            private RenderPreCallback<D> renderPreCallback;
            private RenderCallback<D> renderCallback;

            protected Builder(String ownerID) {
                this.key = ownerID;
            }

            @Override
            public Builder<D> subKey(String subKey) {
                this.key += "." + subKey;
                return this;
            }

            @Override
            public Builder<D> key(String key) {
                this.key = key;
                return this;
            }

            @Override
            public Builder<D> interact(InteractionCallback<D> interactionCallback) {
                this.interactionCallback = interactionCallback;
                return this;
            }

            @Override
            public Builder<D> renderPre(RenderPreCallback<D> renderPreCallback) {
                this.renderPreCallback = renderPreCallback;
                return null;
            }

            @Override
            public Builder<D> render(RenderCallback<D> renderCallback) {
                this.renderCallback = renderCallback;
                return null;
            }

            @Override
            public ComponentStateDefault<D> create() {
                Preconditions.checkNotNull(renderCallback, "Cannot create Component without a RenderCallback!");
                final var interactCallback = Objects.requireNonNullElseGet(this.interactionCallback, () -> (holder, data, component, details) -> InteractionResult.def());
                final var renderPreCallback = Objects.requireNonNullElseGet(this.renderPreCallback, () -> (holder, data, component, context) -> {});
                final var renderCallback = Objects.requireNonNullElseGet(this.renderCallback, () -> (holder, data, component, context) -> {});

                return new ComponentStateDefault<>(key, interactCallback, renderPreCallback, renderCallback);
            }
        }
    }
