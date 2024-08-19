plugins {
    id("viewportl.spigot.conventions")
    id("viewportl.kotlinmodule")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(group = "de.iani.cubeside", name = "LWC", version = "5.0.16-SNAPSHOT")
    compileOnly(group = "com.sk89q.worldedit", name = "worldedit-bukkit", version = "7.3.0-SNAPSHOT")
    compileOnly(group = "com.sk89q.worldguard", name = "worldguard-bukkit", version = "7.1.0-SNAPSHOT")
    compileOnly(group = "com.plotsquared", name = "PlotSquared-Core", version = "6.4.0")
    compileOnly(group = "com.plotsquared", name = "PlotSquared-Bukkit", version = "6.4.0")
    compileOnly(group = "com.gmail.nossr50.mcMMO", name = "mcMMO", version = "2.1.139-SNAPSHOT")
//    compileOnly(libs.guice)
//    compileOnly(libs.reflections)
//    compileOnly(libs.javassist)
//    compileOnly(libs.adventure.api)
    compileOnly(libs.net.kyori.adventure.platform.bukkit)
    implementation(group="com.wolfyscript.scafall.spigot", name = "spigot-platform", version = "0.1-alpha-SNAPSHOT")
//    compileOnly(libs.adventure.minimessage)
    implementation(kotlin("stdlib-jdk8"))
}


tasks.named<ProcessResources>("processResources") {
    expand(project.properties)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

description = "core"
