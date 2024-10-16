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
    id("viewportl.spigot.conventions")
    id("viewportl.kotlinmodule")
    alias(libs.plugins.goooler.shadow)
}

dependencies {
    api(project(":api"))
    compileOnly("com.wolfyscript.scafall.spigot:spigot:${project.version}")
    compileOnly("com.wolfyscript.scafall.spigot:spigot-platform:${project.version}")
    compileOnly(project(":spigot"))
    compileOnly(project(":common"))
}

tasks {
    named<ProcessResources>("processResources") {
        expand(project.properties)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    named<ShadowJar>("shadowJar") {
        dependsOn(project(":spigot").tasks.named("shadowJar"))
        mustRunAfter("jar")
        configurations = listOf(
            project.configurations.runtimeClasspath.get(),
            project.configurations.compileClasspath.get() // Include compileOnly dependencies to hide implementation from
        )

        archiveClassifier = ""

        include("**")

        dependencies {
            include(project(":api"))
            include(project(":spigot"))
            include(project(":common"))
        }
    }
}

description = "core"

artifacts {
    archives(tasks.shadowJar)
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            groupId = "com.wolfyscript.viewportl.spigot"
            artifactId = "spigot-platform"
        }
    }
}
