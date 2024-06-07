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

package com.wolfyscript.utilities.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.wolfyscript.utilities.WolfyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class NBTTagConfigList<VAL extends NBTTagConfig> extends NBTTagConfig {

    @JsonIgnore
    private final Class<VAL> elementType;
    @JsonIgnore
    private final List<Element<VAL>> elements;
    private List<VAL> values;

    @JsonCreator
    NBTTagConfigList(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("values") List<VAL> values, Class<VAL> elementClass) {
        super(wolfyUtils);
        this.elementType = elementClass;
        setValues(values);
        this.elements = new ArrayList<>();
    }

    public NBTTagConfigList(WolfyUtils wolfyUtils, NBTTagConfig parent, Class<VAL> elementType, List<VAL> values) {
        super(wolfyUtils, parent);
        this.elementType = elementType;
        setValues(values);
        this.elements = new ArrayList<>();
    }

    protected NBTTagConfigList(NBTTagConfigList<VAL> other) {
        super(other.wolfyUtils);
        this.elementType = other.elementType;
        this.values = other.values.stream().map(val -> {
            VAL copy = (VAL) val.copy();
            copy.setParent(this);
            return copy;
        }).toList();
        this.elements = other.elements.stream().map(element -> {
            Element<VAL> copyElem = element.copy();
            copyElem.getValue().setParent(this);
            return copyElem;
        }).toList();
    }

    public List<Element<VAL>> getElements() {
        return elements;
    }

    public void setValues(List<VAL> values) {
        this.values = values.stream().peek(val -> val.setParent(this)).toList();
    }

    public List<VAL> getValues() {
        return values;
    }

    public Class<VAL> getElementType() {
        return elementType;
    }

    public static class Element<VAL extends NBTTagConfig> {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer index;
        @JsonInclude(JsonInclude.Include.NON_NULL)
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
        private Integer getIndex() {
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
