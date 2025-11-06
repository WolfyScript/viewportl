/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    kotlin("jvm")
    id("viewportl.spigot.conventions")

    alias(libs.plugins.shadow)
    id("build.docker.run")
    alias(libs.plugins.resource.factory.bukkit)
}

description = "viewportl-spigot"

dependencies {
    compileOnly(libs.adventure.platform.bukkit)
    implementation(libs.scafall.spigot)
    implementation(libs.scafall.loader)
    api(project(":common"))
    implementation(project(":spigotlike"))

    paperweight.paperDevBundle(libs.versions.papermc.get())
}

fun archiveName(): String {
    return "${rootProject.name}-${project.version}-${project.name}-${libs.versions.minecraft.get()}"
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

tasks {
    shadowJar {
        archiveFileName.set("${archiveName()}-mojmap.jar")

        finalizedBy(reobfJar)

        dependencies {
            include(project(":spigotlike"))
            include(project(":api"))
            include(project(":common"))

            include(dependency("androidx.annotation:.*"))
            include(dependency("androidx.collection:.*"))
            include(dependency("androidx.compose.runtime:.*"))


//            include {
//                if (it.moduleGroup.startsWith("androidx")) {
//                    println("Filter Shadow: ${it.moduleGroup} ${it.moduleName} ${it.moduleVersion}")
//                }
//                it.moduleGroup == "org.jetbrains.compose.runtime" || it.moduleGroup == "androidx.compose.runtime"
//            }
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL
    }
    reobfJar {
        dependsOn(shadowJar)
        finalizedBy(jar)
        outputJar.set(layout.buildDirectory.file("libs/${archiveName()}.jar"))
    }
}

artifacts {
    archives(tasks.reobfJar)
}

bukkitPluginYaml {
    name = "viewportl"
    version = project.version.toString()
    main = "com.wolfyscript.viewportl.spigot.SpigotLoaderPlugin"
    apiVersion = libs.versions.minecraft.get() // Only support the latest Minecraft version!
//    libraries.addAll(
//        "androidx.compose.runtime:runtime:1.9.0",
//        "androidx.compose.runtime:runtime-desktop:1.9.0",
//        "androidx.compose.runtime:runtime-annotation:1.9.0",
//        "androidx.compose.runtime:runtime-annotation-jvm:1.9.0",
//        "androidx.collection:collection:1.5.0",
//        "androidx.collection:collection-jvm:1.5.0",
//        "androidx.annotation:annotation:1.9.1",
//        "androidx.annotation:annotation-jvm:1.9.1",
//    )
    authors.add("WolfyScript")
    depend.add("scafall")
}

minecraftServers {
    libName.set("${archiveName()}.jar")
    servers {
        register("spigot") {
            destFileName.set("viewportl.jar")
            version.set(libs.versions.minecraft.get())
            type.set("SPIGOT")
            extraEnv.put("BUILD_FROM_SOURCE", "true")
            imageVersion.set("java21-graalvm") // graalvm contains the jdk required to build from source
            ports.add("25569:25565")
        }
    }
}
