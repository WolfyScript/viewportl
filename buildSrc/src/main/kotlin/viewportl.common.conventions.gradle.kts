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
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
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

    google()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
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

val Project.sharedLibs
    get() = extensions.getByType(org.gradle.accessors.dm.LibrariesForSharedLibs::class.java)

dependencies {
    api(sharedLibs.scafall.api)
    implementation(sharedLibs.bundles.jetbrains)

    compileOnly(sharedLibs.inject.guice)
    compileOnly(sharedLibs.org.reflections)
    compileOnlyApi(sharedLibs.commons.lang3)
    compileOnly(sharedLibs.guava)

    compileOnly(sharedLibs.bundles.minecraft.deps)
    compileOnlyApi(sharedLibs.bundles.jackson)
    compileOnlyApi(sharedLibs.bundles.adventure)

    testImplementation(sharedLibs.bundles.testing)

    api(compose.runtime) {
        exclude("org.jetbrains.kotlin")
    }
}
