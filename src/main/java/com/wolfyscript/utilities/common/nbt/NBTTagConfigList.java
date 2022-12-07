/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
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

package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;
import java.util.Optional;

public abstract class NBTTagConfigList<VAL extends NBTTagConfig> extends NBTTagConfig {

    @JsonIgnore
    private final Class<VAL> elementType;
    private final List<Element<VAL>> elements;

    @JsonCreator
    public NBTTagConfigList(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<VAL>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path, Class<VAL> elementClass) {
        super(wolfyUtils, key, path);
        this.elementType = elementClass;
        this.elements = elements;
    }

    protected NBTTagConfigList(NBTTagConfigList<VAL> other) {
        super(other.wolfyUtils, other.key, other.parentPath);
        this.elementType = other.elementType;
        this.elements = other.elements.stream().map(Element::copy).toList();
    }

    public List<Element<VAL>> getElements() {
        return elements;
    }

    public Class<VAL> getElementType() {
        return elementType;
    }

    public static class Element<VAL extends NBTTagConfig> {

        private Integer index;
        private VAL value;

        public Element() {
            this.index = null;
            this.value = null;
        }

        private Element(Element<VAL> other) {
            this.index = other.index;
            this.value = (VAL) other.getValue().copy();
        }

        private Optional<Integer> index() {
            return Optional.ofNullable(index);
        }

        private Optional<VAL> value() {
            return Optional.ofNullable(value);
        }

        @JsonSetter
        public void setIndex(int index) {
            this.index = index;
        }

        @JsonGetter
        private int getIndex() {
            return index;
        }

        @JsonSetter
        public void setValue(VAL value) {
            this.value = value;
        }

        @JsonGetter
        public VAL getValue() {
            return value;
        }

        public Element<VAL> copy() {
            return new Element<>(this);
        }
    }
}
