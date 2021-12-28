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

package me.wolfyscript.utilities.extensions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PackMetaFile {

    private final PackInfo pack;

    @JsonCreator
    PackMetaFile(@JsonProperty("pack") PackInfo pack) {
        this.pack = pack;
    }

    public PackInfo getPack() {
        return pack;
    }

    public static class PackInfo {

        private final int version;
        private final String namespace;
        private final List<String> authors;
        private final String description;

        @JsonCreator
        PackInfo(@JsonProperty("version") int version, @JsonProperty("namespace") String namespace, @JsonProperty("authors") List<String> authors, @JsonProperty("description") String description) {
            this.version = version;
            this.namespace = namespace;
            this.authors = List.copyOf(authors);
            this.description = description;
        }

        public int getVersion() {
            return version;
        }

        public String getNamespace() {
            return namespace;
        }

        public List<String> getAuthors() {
            return authors;
        }

        public String getDescription() {
            return description;
        }
    }

}
