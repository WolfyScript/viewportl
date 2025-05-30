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
    alias(libs.plugins.paperweight.userdev)
}

description = "viewportl-spigot"

dependencies {
    compileOnly(libs.adventure.platform.bukkit)
    implementation(libs.scafall.spigot.impl)
    implementation(libs.scafall.loader)
    api(project(":common"))

    paperweight.paperDevBundle(libs.versions.papermc.get())
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

tasks {
    shadowJar {
        archiveFileName = "viewportl-spigot.innerjar"

        dependencies {
            include(dependency("com.wolfyscript.viewportl:.*"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            groupId = "com.wolfyscript.viewportl.spigot"
            artifactId = "spigot"
        }
    }
}
