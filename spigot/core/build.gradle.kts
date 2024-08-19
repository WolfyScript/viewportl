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
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(group = "de.iani.cubeside", name = "LWC", version = "5.0.16-SNAPSHOT")
    compileOnly(group = "com.sk89q.worldedit", name = "worldedit-bukkit", version = "7.3.0-SNAPSHOT")
    compileOnly(group = "com.sk89q.worldguard", name = "worldguard-bukkit", version = "7.1.0-SNAPSHOT")
    compileOnly(group = "com.plotsquared", name = "PlotSquared-Core", version = "6.4.0")
    compileOnly(group = "com.plotsquared", name = "PlotSquared-Bukkit", version = "6.4.0")
    compileOnly(group = "com.gmail.nossr50.mcMMO", name = "mcMMO", version = "2.1.139-SNAPSHOT")
//    compileOnly(libs.guice)
//    compileOnly(libs.reflections)
//    compileOnly(libs.javassist)
//    compileOnly(libs.adventure.api)
    compileOnly(libs.net.kyori.adventure.platform.bukkit)
    implementation(group="com.wolfyscript.scafall.spigot", name = "spigot-platform", version = "0.1-alpha-SNAPSHOT")
//    compileOnly(libs.adventure.minimessage)
    implementation(kotlin("stdlib-jdk8"))
}


tasks.named<ProcessResources>("processResources") {
    expand(project.properties)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

description = "core"
