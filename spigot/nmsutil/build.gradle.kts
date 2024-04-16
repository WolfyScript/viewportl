import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask

plugins {
    id("wolfyutils.spigot.conventions")
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

description = "nmsutil"

// Specify directory in home directory as buildtools source
ext.set("buildToolsDir", "${System.getProperty("user.home")}${File.separator}minecraft${File.separator}buildtools")
ext.set("buildToolsJar", "${System.getProperty("user.home")}${File.separator}minecraft${File.separator}buildtools${File.separator}BuildTools.jar")

dependencies {
    subprojects.forEach {
        api(it)
    }

    compileOnly("com.google.inject:guice:5.1.0")
    compileOnly("org.reflections:reflections:0.10.2")
    compileOnly("org.javassist:javassist:3.29.2-GA")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.1.2")
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
}

subprojects.forEach {
    // Do not publish all the version specific subprojects!
    it.tasks.withType<ArtifactoryTask> {
        skip = true
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        subprojects.forEach { project ->
            project.tasks.findByName("remap")?.let {
                dependsOn(it)
            }
        }

        archiveClassifier.set("")

        dependencies {
            subprojects.forEach {
                include(project(it.path))
            }
        }
    }
}