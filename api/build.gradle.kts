
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
    `java-library`
    `maven-publish`
    id("viewportl.common.conventions")
    alias(libs.plugins.shadow)
    alias(libs.plugins.fabric.loom)
}

dependencies {
    implementation(libs.scafall.loader)

    minecraft("com.mojang:minecraft:${libs.versions.minecraft.get()}")

    mappings(
        loom.layered {
            officialMojangMappings()
//            parchment("org.parchmentmc.data:parchment-${libs.versions.minecraft.get()}:${libs.versions.parchment.get()}@zip")
        }
    )
}

tasks {
    shadowJar {
        archiveBaseName = "scafall-api"

        // Mappings are in the runtime classpath. Not sure why they are included even though we use include for dependencies...
        // So to be sure nothing else slips in, just accept dependencies from the shadow configuration.
        configurations = listOf(project.configurations.shadow.get())
        finalizedBy(remapJar)

        dependencies {
            include(dependency("com.wolfyscript.scafall:.*"))
        }
    }
    // Disable remapping without having to disable the tasks
    // This will get shaded into other platforms that then use their specific remapper instead.
    // Additionally, this will be a public api, which should work across all platforms.
    remapJar {
        dependsOn(shadowJar)
        targetNamespace = "named"
        inputFile.set(shadowJar.get().archiveFile)
    }
    remapSourcesJar {
        targetNamespace = "named"
    }
}

artifacts {
    archives(tasks.remapJar)
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            groupId = "com.wolfyscript.viewportl.api"
            artifactId = "api"
        }
    }
}
