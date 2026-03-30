import utils.archiveName

plugins {
    kotlin("jvm")
    `java-library`
    id(libs.plugins.devtools.docker.minecraft.get().pluginId)
    id("viewportl.common.conventions")
    id("build.docker.run")
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.shadow)
}

dependencies {
    api(project(":api"))
    implementation(libs.scafall.loader)
    api(project(":common"))

    minecraft(libs.minecraft)

    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)
}

loom {
    mods {
        create("viewportl") {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
    shadowJar {
        archiveFileName.set("${archiveName("fabric", libs.versions.minecraft.get())}.jar")
        dependencies {
            include(project(":api"))
            include(project(":common"))
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL
        finalizedBy("fabric_copy")
    }
}

artifacts {
    archives(tasks.shadowJar)
}

minecraftServers {
    libName.set("${archiveName("fabric", libs.versions.minecraft.get())}.jar")
    servers {
        register("fabric") {
            destPath.set("mods")
            destFileName.set("viewportl.jar")
            version.set(libs.versions.minecraft.get())
            type.set("FABRIC")
            imageVersion.set("java25")
            ports.add("25569:25565")
            extraEnv.put("MODRINTH_PROJECTS", "fabric-api, fabric-language-kotlin")
            extraEnv.put("FABRIC_LOADER_VERSION", libs.versions.fabric.loader.get())
        }
    }
}
