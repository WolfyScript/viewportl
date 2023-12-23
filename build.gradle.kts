plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://maven.wolfyscript.com/repository/private/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.com.wolfyscript.jackson.dataformat.hocon)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    compileOnly(libs.com.fasterxml.jackson.core.jackson.databind)
    compileOnly(libs.org.reflections.reflections)
    compileOnly(libs.com.google.inject.guice)
    compileOnly(libs.it.unimi.dsi.fastutil)
    compileOnly(libs.org.jetbrains.annotations)
    compileOnly(libs.com.mojang.authlib)
    compileOnly(libs.io.netty.netty.all)
    compileOnly(libs.org.apache.commons.commons.lang3)
    compileOnly(libs.net.kyori.adventure.api)
    compileOnly(libs.net.kyori.adventure.text.minimessage)
    compileOnly(libs.net.kyori.adventure.platform.api)
    compileOnly(libs.com.google.guava.guava)
}

group = "com.wolfyscript.wolfyutils"
version = "5.0-SNAPSHOT"
description = "wolfyutilities"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
