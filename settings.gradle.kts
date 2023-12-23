rootProject.name = "wolfyutils-parent"
pluginManagement {
    includeBuild("build-logic") // Include 'plugins build' to define convention plugins.
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

include(":api")

project(":api").projectDir = file("api")
