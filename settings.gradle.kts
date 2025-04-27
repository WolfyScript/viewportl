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

rootProject.name = "viewportl"
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net")
            content {
                includeGroup("net.fabricmc")
                includeGroup("fabric-loom")
            }
        }
        maven {
            name = "Sponge"
            url = uri("https://repo.spongepowered.org/repository/maven-public")
            content {
                includeGroupAndSubgroups("org.spongepowered")
            }
        }
        maven {
            name = "Forge"
            url = uri("https://maven.minecraftforge.net")
            content {
                includeGroupAndSubgroups("net.minecraftforge")
            }
        }
        maven("https://artifacts.wolfyscript.com/artifactory/gradle-dev")
        maven("https://maven.neoforged.net/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

sequenceOf(
    "api",
    "common",
    "spigot",
    "sponge"
).forEach {
    include(":${it}")
    project(":${it}").projectDir = file(it)
}

/* ********************* *
 * Example/Test Projects *
 * ********************* */
val examplesDir: String = "test-plugins"

fun samplePlugin(root: String, vararg modules: String) {
    include(":$examplesDir:$root")
    project(":$examplesDir:$root").projectDir = file("$examplesDir/$root")

    modules.forEach {
        // platform loader project
        include(":$examplesDir:$root:$it")
        project(":$examplesDir:$root:$it").projectDir = file("$examplesDir/$root/${it.replace(":", "/")}")
    }
}

samplePlugin("single-platform", "plugin-common", "spigot-plugin", "sponge-plugin")

/* ********************* *
 * Spigot implementation *
 * ********************* */
sequenceOf(
    "platform",
    "loader"
).forEach {
    include(":spigot:${it}")
    project(":spigot:${it}").projectDir = file("spigot/${it.replace(":", "/")}")
}


/* ********************* *
 * Sponge implementation *
 * ********************* */
sequenceOf(
    "platform",
    "loader"
).forEach {
    include(":sponge:${it}")
    project(":sponge:${it}").projectDir = file("sponge/${it.replace(":", "/")}")
}
