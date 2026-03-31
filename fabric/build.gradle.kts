import utils.archiveName

plugins {
    kotlin("jvm")
    `java-library`
    id(sharedLibs.plugins.devtools.docker.minecraft.get().pluginId)
    id("viewportl.common.conventions")
    id("build.docker.run")
    alias(sharedLibs.plugins.fabric.loom)
    alias(sharedLibs.plugins.shadow)
}

dependencies {
    api(project(":api"))
    implementation(sharedLibs.scafall.loader)
    api(project(":common"))

    minecraft(sharedLibs.minecraft)

    implementation(sharedLibs.fabric.loader)
    implementation(sharedLibs.fabric.api)
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
        archiveFileName.set("${archiveName("fabric", sharedLibs.versions.minecraft.get())}.jar")
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
    libName.set("${archiveName("fabric", sharedLibs.versions.minecraft.get())}.jar")
    servers {
        register("fabric") {
            destPath.set("mods")
            destFileName.set("viewportl.jar")
            version.set(sharedLibs.versions.minecraft.get())
            type.set("FABRIC")
            imageVersion.set("java25")
            ports.add("25569:25565")
            extraEnv.put("MODRINTH_PROJECTS", "fabric-api, fabric-language-kotlin")
            extraEnv.put("FABRIC_LOADER_VERSION", sharedLibs.versions.fabric.loader.get())
        }
    }
}
