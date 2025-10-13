import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.named
import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency.LoadOrder
import utils.convertToEpochVer

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
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.spongepowered.gradle)
    id("build.docker.run")
}

description = "viewportl-sponge"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev") // scafall & viewportl will be available on this repo
}

dependencies {
    compileOnly(libs.spongepowered.api)
    implementation(libs.scafall.loader)
    implementation(libs.org.reflections)
    api(project(":common"))
    implementation(libs.scafall.loader)
}

kotlin {
    jvmToolchain(21)
}

fun archiveName() = "${project.rootProject.name}-${project.version}-spigot-${libs.versions.minecraft.get()}"

tasks {
    named<ProcessResources>("processResources") {
        expand(project.properties)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("${archiveName()}-mojmap.jar")

        dependencies {
            include(project(":common"))
        }
    }
}

sponge {
    apiVersion(libs.versions.sponge.api.get())
    license("GNU GPL 3.0")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("viewportl") {
        version(project.version.toString().convertToEpochVer())
        displayName("Viewportl")
        description("")
        entrypoint("com.wolfyscript.viewportl.loader.SpongeViewportlLoader")
        dependency("spongeapi") {
            loadOrder(LoadOrder.AFTER)
            optional(false)
        }
        dependency("scafall") {
            version(libs.versions.scafall.get().convertToEpochVer())
            loadOrder(LoadOrder.AFTER)
            optional(false)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            groupId = "com.wolfyscript.viewportl.sponge"
            artifactId = "sponge"
        }
    }
}

/* ********************************************************************************************* *
 *  Construct, Copy Plugin & Run Test Servers directly from gradle inside a docker container     *
 *                          !! Requires a local docker instance !!                               *
 * ********************************************************************************************* */
minecraftServers {
    libName.set("${archiveName()}.jar") // Makes sure to copy the correct file (when using shaded classifier "-all.jar" this needs to be changed!)
    servers {
        register("spongevanilla_14") {
            destFileName.set("viewportl.jar")
            val spongeVersion = "1.21.4-14.0.0-RC2113"
            imageVersion.set("java21")
            type.set("CUSTOM")
            extraEnv.put("SPONGEVERSION", spongeVersion)
            extraEnv.put(
                "CUSTOM_SERVER",
                "https://repo.spongepowered.org/repository/maven-public/org/spongepowered/spongevanilla/${spongeVersion}/spongevanilla-${spongeVersion}-universal.jar"
            )
            ports.add("25595:25565")
        }
    }
}
