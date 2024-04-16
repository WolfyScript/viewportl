rootProject.name = "wolfyutils-parent"
pluginManagement {
    includeBuild("build-logic") // Include 'plugins build' to define convention plugins.
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
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
    "nmsutil",
    "nmsutil:v1_17_R1",
    "nmsutil:v1_17_R1_P1",
    "nmsutil:v1_18_R1",
    "nmsutil:v1_18_R1_P1",
    "nmsutil:v1_18_R2",
    "nmsutil:v1_19_R3",
    "nmsutil:v1_19_R1",
    "nmsutil:v1_19_R2",
    "nmsutil:v1_20_R1",
    "nmsutil:v1_20_R2",
    "nmsutil:v1_20_R3",
).forEach {
    include(":spigot:${it}")
    project(":spigot:${it}").projectDir = file("spigot/${it.replace(":", "/")}")
}
