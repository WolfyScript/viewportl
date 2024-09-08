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
    `java-library`
    `maven-publish`
    id("viewportl.kotlinmodule")
    alias(libs.plugins.goooler.shadow)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    api(project(":api"))
    compileOnly(project(":sponge"))
    compileOnly(project(":common"))
    compileOnly("com.wolfyscript.scafall.sponge:sponge:${project.version}")
    compileOnly("com.wolfyscript.scafall.sponge:sponge-platform:${project.version}")
    compileOnly(libs.spongepowered.api)
}

tasks {
    named<ProcessResources>("processResources") {
        expand(project.properties)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    named<ShadowJar>("shadowJar") {
        dependsOn(project(":sponge").tasks.named("shadowJar"))
        mustRunAfter("jar")
        configurations = listOf(
            project.configurations.runtimeClasspath.get(),
            project.configurations.compileClasspath.get() // Include compileOnly dependencies to hide implementation from
        )

        archiveClassifier = ""

        include("**")

        dependencies {
            include(project(":api"))
            include(project(":sponge"))
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
            groupId = "com.wolfyscript.viewportl.sponge"
            artifactId = "sponge-platform"
        }
    }
}
