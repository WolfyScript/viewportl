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
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev")

    maven(url = "https://libraries.minecraft.net/")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.maven.apache.org/maven2/")
}

dependencies {
    api(libs.jackson.dataformat.hocon)
    api("com.wolfyscript.scafall:api:0.1-alpha-SNAPSHOT")
    implementation(libs.org.jetbrains.kotlin.stdlib)

    compileOnly(libs.jackson.databind)
    compileOnly(libs.org.reflections.reflections)
    compileOnly(libs.com.google.inject.guice)
    compileOnly(libs.it.unimi.dsi.fastutil)
    compileOnly(libs.org.jetbrains.annotations)
    compileOnly(libs.com.mojang.authlib)
    compileOnly(libs.io.netty.netty.all)
    compileOnlyApi(libs.org.apache.commons.commons.lang3)
    compileOnlyApi(libs.net.kyori.adventure.api)
    compileOnlyApi(libs.net.kyori.adventure.text.minimessage)
    compileOnly(libs.net.kyori.adventure.platform.api)
    compileOnly(libs.com.google.guava.guava)

    testImplementation(libs.org.junit.jupiter.junit.jupiter)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

java.sourceCompatibility = JavaVersion.VERSION_21
