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
    api("com.wolfyscript.viewportl.api:api:alpha0.0.1.0-SNAPSHOT")
}

kotlin {
    // Both scafall & viewportl are compiled using Java 21, so this plugin will be too
    jvmToolchain(21)
}
