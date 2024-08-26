import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
    kotlin("jvm")
    alias(libs.plugins.goooler.shadow) // Use a different fork of the shadow plugin to support Java 21

    // These are required for the test servers (see below), can be removed when not required
    id("com.wolfyscript.devtools.docker.run") version "2.0-SNAPSHOT"
    id("com.wolfyscript.devtools.docker.minecraft_servers") version "2.0-SNAPSHOT"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://repo.papermc.io/repository/maven-public/") // Just the repo required for the paper api
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev") // scafall & viewportl will be available on this repo
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    // We need both the scafall & viewportl spigot implementation.
    implementation("com.wolfyscript.scafall.spigot:spigot-platform:${project.version}")
    implementation("com.wolfyscript.viewportl.spigot:spigot-platform:${project.version}") // While this already provides the scafall api, we need the spigot impl. to shade it (see below)
}

kotlin {
    // Both scafall & viewportl are compiled using Java 21, so this plugin will be too
    jvmToolchain(21)
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
     *  Shade Scafall & Viewportl and relocate both of them  *
     * ***************************************************** */
    named<ShadowJar>("shadowJar") {
        mustRunAfter("jar")

        archiveClassifier = "" // This replaces the non-shaded jar with this shaded one (default creates a separate "-all.jar")

        dependencies {
            include(dependency("com.wolfyscript.scafall.spigot:.*"))
            include(dependency("com.wolfyscript.viewportl.spigot:.*"))
        }

        // Always relocate both viewportl & scafall to prevent conflicts between plugins!
        relocate("com.wolfyscript", "org.example.viewportl.libs.com.wolfyscript")
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
    customEnv["JVM_OPTS"] = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${debugPort}" // Allows to attach the IntelliJ debugger to the MC server inside the container
    customEnv["FORCE_REDOWNLOAD"] = "false"
    env.set(customEnv)
    // Constrain the container to 2 cpus, to behave similar to servers in production (it is unlikely servers use 24 threads)
    // and allow for console interactivity with 'docker attach'
    arguments("--cpus", "2", "-it")
}

minecraftServers {
    serversDir.set(file("${System.getProperty("user.home")}${File.separator}minecraft${File.separator}test_servers_v5"))
    libName.set("${project.name}-${version}.jar") // Makes sure to copy the correct file (when using shaded classifier "-all.jar" this needs to be changed!)
    val debugPortMapping = "${debugPort}:${debugPort}"
    servers {
        register("spigot_1_21") {
            version.set("1.21.1")
            type.set("SPIGOT")
            imageVersion.set("java21")
            ports.set(setOf(debugPortMapping, "25568:25565"))
        }
        // Paper test servers
        register("paper_1_21") {
            version.set("1.21.1")
            type.set("PAPER")
            imageVersion.set("java21")
            ports.set(setOf(debugPortMapping, "25569:25565"))
        }
    }
}
