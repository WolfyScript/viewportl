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

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("viewportl.spigot.conventions")

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.wolfyscript.devtools.docker.run") version "2.0-SNAPSHOT"
    id("com.wolfyscript.devtools.docker.minecraft_servers") version "2.0-SNAPSHOT"
    kotlin("jvm")
}

description = "viewportl-spigot"

dependencies {
    api(project(":api"))
    implementation(project(":common"))
    implementation(project(":spigot:core"))
    implementation(project(":spigot:plugin-compatibility"))

    api(libs.org.bstats.bukkit)
    api(libs.de.tr7zw.item.nbt.api)

    testImplementation(project(":spigot:core"))
    implementation(kotlin("stdlib-jdk8"))
}

tasks.named<ProcessResources>("processResources") {
    expand(project.properties)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val debugPort: String = "5006"

minecraftDockerRun {
    val customEnv = env.get().toMutableMap()
    customEnv["MEMORY"] = "2G"
    customEnv["JVM_OPTS"] = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${debugPort}"
    customEnv["FORCE_REDOWNLOAD"] = "false"
    env.set(customEnv)
    arguments("--cpus", "2", "-it") // Constrain to only use 2 cpus, and allow for console interactivity with 'docker attach'
}

minecraftServers {
    serversDir.set(file("${System.getProperty("user.home")}${File.separator}minecraft${File.separator}test_servers_v5"))
    libName.set("${project.name}-${version}.jar")
    val debugPortMapping = "${debugPort}:${debugPort}"
    servers {
        register("spigot_1_17") {
            version.set("1.17.1")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25565:25565"))
        }
        register("spigot_1_18") {
            version.set("1.18.2")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25566:25565"))
        }
        register("spigot_1_19") {
            version.set("1.19.4")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25567:25565"))
        }
        register("spigot_1_20") {
            version.set("1.20.6")
            type.set("SPIGOT")
            imageVersion.set("java21")
            ports.set(setOf(debugPortMapping, "25568:25565"))
        }
        // Paper test servers
        register("paper_1_20") {
            version.set("1.20.6")
            type.set("PAPER")
            imageVersion.set("java21")
            ports.set(setOf(debugPortMapping, "25569:25565"))
        }
        register("paper_1_19") {
            version.set("1.19.4")
            type.set("PAPER")
            ports.set(setOf(debugPortMapping, "25570:25565"))
        }
    }
}

tasks.named<ShadowJar>("shadowJar") {
    dependsOn(project(":spigot:core").tasks.named("shadowJar"))
    mustRunAfter("jar")

    archiveClassifier.set("")

    include("**")

    dependencies {
        include(project(":api"))
        include(project(":common"))
        include(dependency(libs.com.wolfyscript.jackson.dataformat.hocon.get().toString()))
        include(dependency("${libs.org.bstats.bukkit.get().group}:.*"))
        include(dependency(libs.de.tr7zw.item.nbt.api.get().toString()))
        include(project(":spigot:core"))
        include(project(":spigot:plugin-compatibility"))
    }

    // Always required to be shaded and relocated!
    relocate("org.bstats", "com.wolfyscript.utilities.bukkit.metrics")
    relocate("de.tr7zw.changeme.nbtapi", "com.wolfyscript.lib.de.tr7zw.nbtapi")
}

tasks.named("test") {
    dependsOn.add(tasks.named("shadowJar"))
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(17)
}