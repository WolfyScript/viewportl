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
import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency.LoadOrder

plugins {
    kotlin("jvm")
    alias(libs.plugins.goooler.shadow) // Use a different fork of the shadow plugin to support Java 21
    alias(libs.plugins.spongepowered.gradle)

    // These are required for the test servers (see below), can be removed when not required
    id("com.wolfyscript.devtools.docker.run") version "a2.0.1.0"
    id("com.wolfyscript.devtools.docker.minecraft_servers") version "a2.0.1.0"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://repo.papermc.io/repository/maven-public/") // Just the repo required for the paper api
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev") // scafall & viewportl will be available on this repo
}

dependencies {
    implementation(libs.org.reflections.reflections)

    implementation(project(":test-plugins:single-platform:plugin-common"))
}

kotlin {
    // Both scafall & viewportl are compiled using Java 21, so this plugin will be too
    jvmToolchain(21)
}

sponge {
    apiVersion(libs.versions.sponge.api.get())
    license("GNU GPL 3.0")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("viewportl_example") {
        version("0.0.1.0")
        displayName("ViewportlExample")
        description("")
        entrypoint("org.example.viewportl.ViewportlExample")
        dependency("spongeapi") {
            loadOrder(LoadOrder.AFTER)
            optional(false)
        }
        dependency("scafall") {
            version("1000.0.1.0-SNAPSHOT")
            loadOrder(LoadOrder.AFTER)
            optional(false)
        }
        dependency("viewportl") {
            version("1000.0.1.0-SNAPSHOT")
            loadOrder(LoadOrder.AFTER)
            optional(false)
        }
    }
}

tasks {
    /* ******************************************************************************************** *
     *  Processes resources like the plugin.yml and replaces placeholders with project properties.  *
     *  e.g. version, dependencies                                                                  *
     * ******************************************************************************************** */
    named<ProcessResources>("processResources") {
        expand(project.properties)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    /* ***************************************************** *
     *  Shade the common module                              *
     * ***************************************************** */
    named<ShadowJar>("shadowJar") {
        mustRunAfter("jar")

        archiveClassifier =
            "" // This replaces the non-shaded jar with this shaded one (default creates a separate "-all.jar")

        include("**")

        dependencies {
            include(project(":test-plugins:single-platform:plugin-common"))
        }
    }
}

/* ********************************************************************************************* *
 *  Construct, Copy Plugin & Run Test Servers directly from gradle inside a docker container     *
 *                          !! Requires a local docker instance !!                               *
 * ********************************************************************************************* */
val debugPort: String = "5006" // This port will be used for the debugger

minecraftDockerRun {
    val customEnv = env.get().toMutableMap()
    customEnv["MEMORY"] = "2G"
    customEnv["JVM_OPTS"] =
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${debugPort}" // Allows to attach the IntelliJ debugger to the MC server inside the container
    customEnv["FORCE_REDOWNLOAD"] = "false"
    env.set(customEnv)
    clean = true // When enabled, removes the docker container once it is shutdown
    // Constrain the container to 2 cpus, to behave similar to servers in production (it is unlikely servers use 24 threads)
    // and allow for console interactivity with 'docker attach'
    arguments("--cpus", "2", "-it")
}

minecraftServers {
    serversDir.set(file("${System.getProperty("user.home")}${File.separator}minecraft${File.separator}test_servers_v5"))
    libName.set("${project.name}-${version}.jar") // Makes sure to copy the correct file (when using shaded classifier "-all.jar" this needs to be changed!)
    val debugPortMapping = "${debugPort}:${debugPort}"
    servers {
        register("spongevanilla_14") {
            destFileName.set("viewportl_example.jar")
            val spongeVersion = "1.21.4-14.0.0-RC2113"
            imageVersion.set("java21")
            type.set("CUSTOM")
            extraEnv.put("SPONGEVERSION", spongeVersion)
            extraEnv.put(
                "CUSTOM_SERVER",
                "https://repo.spongepowered.org/repository/maven-public/org/spongepowered/spongevanilla/${spongeVersion}/spongevanilla-${spongeVersion}-universal.jar"
            )
            ports.set(setOf(debugPortMapping, "25595:25565"))
        }
    }
}
