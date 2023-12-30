rootProject.name = "wolfyutils-parent"
pluginManagement {
    includeBuild("build-logic") // Include 'plugins build' to define convention plugins.
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

sequenceOf(
    "api",
    "common"
).forEach {
    include(":${it}")
    project(":${it}").projectDir = file(it)
}
