plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev") // scafall & viewportl will be available on this repo
}

dependencies {
    api(libs.scafall.api)
    api(project(":api"))
}

kotlin {
    // Both scafall & viewportl are compiled using Java 21, so this plugin will be too
    jvmToolchain(21)
}
