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

    alias(sharedLibs.plugins.shadow)
}

description = "viewportl-spigot"

dependencies {
    compileOnly(sharedLibs.adventure.platform.bukkit)
    implementation(sharedLibs.scafall.spigot)
    api(project(":common"))

    paperweight.paperDevBundle(sharedLibs.versions.papermc.get())
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

fun archiveName(): String {
    return "${rootProject.name}-${project.version}"
}

tasks {
    shadowJar {
        dependencies {
            include(project(":api"))
            include(project(":common"))
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL
    }
    reobfJar {
        enabled = false
    }
    build {
        dependsOn(shadowJar)
    }
}

artifacts {
    archives(tasks.reobfJar)
}
