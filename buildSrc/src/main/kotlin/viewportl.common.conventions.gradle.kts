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

val javaTarget = 21
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
    if (JavaVersion.current() < JavaVersion.toVersion(javaTarget)) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(javaTarget))
    }
}

tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8" // Consistent source file encoding
        release.set(javaTarget)
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

val Project.libs
    get() = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

dependencies {
    api(libs.scafall.api)
    api(libs.jackson.dataformat.hocon)
    implementation(libs.org.jetbrains.kotlin.stdlib)

    compileOnly(libs.com.google.inject.guice)
    compileOnly(libs.org.reflections.reflections)
    compileOnly(libs.it.unimi.dsi.fastutil)
    compileOnly(libs.org.jetbrains.annotations)
    compileOnly(libs.io.netty.netty.all)
    compileOnly(libs.com.mojang.authlib)
    compileOnlyApi(libs.org.apache.commons.commons.lang3)
    compileOnly(libs.com.google.guava.guava)

    compileOnly(libs.jackson.databind)
    compileOnlyApi(libs.net.kyori.adventure.api)
    compileOnlyApi(libs.net.kyori.adventure.text.minimessage)
    compileOnly(libs.net.kyori.adventure.platform.api)

    testImplementation(libs.org.junit.jupiter.junit.jupiter)
}
