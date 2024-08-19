rootProject.name = "viewportl"
pluginManagement {
    includeBuild("build-logic") // Include 'plugins build' to define convention plugins.
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://artifacts.wolfyscript.com/artifactory/gradle-dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

sequenceOf(
    "api",
    "common",
    "spigot"
).forEach {
    include(":${it}")
    project(":${it}").projectDir = file(it)
}

// Spigot
sequenceOf(
    "core",
    "plugin-compatibility",
).forEach {
    include(":spigot:${it}")
    project(":spigot:${it}").projectDir = file("spigot/${it.replace(":", "/")}")
}
