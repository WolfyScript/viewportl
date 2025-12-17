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
    id("build.docker.run")

    alias(libs.plugins.shadow)
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

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

fun archiveName(): String {
    return "${rootProject.name}-${project.version}-${project.name}-${libs.versions.minecraft.get()}"
}

tasks {
    shadowJar {
        archiveFileName.set("${archiveName()}-mojmap.jar")
        dependencies {
            include(project(":spigotlike"))
        }
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL
    }
    assemble {
        dependsOn(shadowJar)
    }
}

artifacts {
    archives(tasks.shadowJar)
}

bukkitPluginYaml {
    name = "viewportl"
    version = project.version.toString()
    main = "com.wolfyscript.viewportl.paper.PaperLoaderPlugin"
    apiVersion = libs.versions.minecraft.get() // Only support the latest Minecraft version!
    authors.add("WolfyScript")
    depend.add("scafall")
}

minecraftServers {
    libName.set("${archiveName()}-mojmap.jar")
    servers {
        register("paper") {
            destFileName.set("viewportl.jar")
            version.set(libs.versions.minecraft.get())
            type.set("PAPER")
            imageVersion.set("java21")
            ports.add("25570:25565")
        }
    }
}
