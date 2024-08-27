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
    includeBuild("build-logic") // Include 'plugins build' to define convention plugins.
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://artifacts.wolfyscript.com/artifactory/gradle-dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

sequenceOf(
    "api",
    "common",
    "spigot"
).forEach {
    include(":${it}")
    project(":${it}").projectDir = file(it)
}

/* ********************* *
 * Example/Test Projects *
 * ********************* */
val examplesDir: String = "examples"

fun samplePlugin(root: String, vararg modules: String) {
    include(":$examplesDir:$root")
    project(":$examplesDir:$root").projectDir = file("$examplesDir/$root")

    modules.forEach {
        // platform loader project
        include(":$examplesDir:$root:$it")
        project(":$examplesDir:$root:$it").projectDir = file("$examplesDir/$root/${it.replace(":", "/")}")
    }
}

samplePlugin("single-platform", "spigot-plugin")

/* ********************* *
 * Spigot implementation *
 * ********************* */
sequenceOf(
    "platform",
).forEach {
    include(":spigot:${it}")
    project(":spigot:${it}").projectDir = file("spigot/${it.replace(":", "/")}")
}
