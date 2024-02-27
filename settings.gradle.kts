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
    "common"
).forEach {
    include(":${it}")
    project(":${it}").projectDir = file(it)
}
