plugins {
    kotlin("jvm")
    alias(libs.plugins.fabric.loom)
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev") // scafall & viewportl will be available on this repo
}

dependencies {
    api(libs.scafall.api)
    api(project(":api"))
    modCompileOnly(libs.adventure.platform.shared)
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
}

kotlin {
    // Both scafall & viewportl are compiled using Java 21, so this plugin will be too
    jvmToolchain(21)
}

tasks {
    // Disable remapping without having to disable the tasks
    // This will get shaded into other platforms that then use their specific remapper instead.
    // Additionally, this will be a public api, which should work across all platforms.
    remapJar {
        targetNamespace = "named"
    }
    remapSourcesJar {
        targetNamespace = "named"
    }
}
