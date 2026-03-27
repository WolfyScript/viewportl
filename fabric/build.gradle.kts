plugins {
    kotlin("jvm")
    `java-library`
    id(libs.plugins.devtools.docker.minecraft.get().pluginId)
    id("viewportl.common.conventions")
    id("build.docker.run")
    alias(libs.plugins.fabric.loom)
}

dependencies {
    api(project(":api"))
    implementation(libs.scafall.spigot)
    implementation(libs.scafall.loader)
    api(project(":common"))

    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
}

loom {
    serverOnlyMinecraftJar()

    mods {
        create("viewportl") {
            sourceSet(sourceSets.main.get())
        }
    }
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
    assemble {
        dependsOn(remapJar)
    }
}

artifacts {
    archives(tasks.remapJar)
}
