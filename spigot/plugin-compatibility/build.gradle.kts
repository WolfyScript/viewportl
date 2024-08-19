/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
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

plugins {
    id("viewportl.spigot.conventions")
    id("viewportl.kotlinmodule")
}

dependencies {
    compileOnly(group = "com.ssomar.score", name = "SCore", version = "4.24.4.15")
    compileOnly("com.ssomar.executableblocks:ExecutableBlocks:4.24.4.15")
    compileOnly("com.denizenscript:denizen:1.2.5-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.willfp:eco:6.13.0")
    compileOnly("com.github.LoneDev6:api-itemsadder:3.1.5")
    compileOnly("com.elmakers.mine.bukkit:MagicAPI:10.2")
    compileOnly("com.github.AlessioGr:FancyBags:2.7.0")
    compileOnly("com.github.oraxen:oraxen:1.152.0")
    compileOnly("io.lumine:MythicLib:1.1.5")
    compileOnly("net.Indyuce:MMOItems-API:6.9.2-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
    compileOnly("com.google.inject:guice:5.1.0")
    compileOnly(project(":spigot:core"))
}

description = "plugin-compatibility"
repositories {
    mavenCentral()
}