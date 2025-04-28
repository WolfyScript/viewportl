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
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.parchmentmc.org/")
        content { includeGroup("org.parchmentmc.data") }
    }
    maven {
        url = uri("https://maven.neoforged.net/releases")
        content { includeGroup("org.parchmentmc.data") }
    }
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev")
    maven(url = "https://libraries.minecraft.net/")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.maven.apache.org/maven2/")
    mavenLocal()
}

kotlin {
    jvmToolchain(21)
}

// Make sure all tasks which produce archives (jar, sources jar, javadoc jar, etc) produce more consistent output
tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }
}

val Project.libs
    get() = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

dependencies {
    api(libs.scafall.api)
    implementation(libs.bundles.jetbrains)

    compileOnly(libs.inject.guice)
    compileOnly(libs.org.reflections)
    compileOnlyApi(libs.commons.lang3)
    compileOnly(libs.guava)

    compileOnly(libs.bundles.minecraft.deps)
    compileOnlyApi(libs.bundles.jackson)
    compileOnlyApi(libs.bundles.adventure)

    testImplementation(libs.bundles.testing)
}
