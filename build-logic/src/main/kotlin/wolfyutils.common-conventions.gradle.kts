plugins {
    `java-library`
    `maven-publish`
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }
    maven {
        url = uri("https://maven.wolfyscript.com/repository/public/")
    }
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://repo.citizensnpcs.co")
    }
    maven {
        url = uri("https://nexus.phoenixdevt.fr/repository/maven-public/")
    }
}

dependencies {
    api(libs.com.wolfyscript.jackson.dataformat.hocon)
    implementation(libs.org.jetbrains.kotlin.stdlib)

    compileOnly(libs.com.fasterxml.jackson.core.jackson.databind)
    compileOnly(libs.org.reflections.reflections)
    compileOnly(libs.com.google.inject.guice)
    compileOnly(libs.it.unimi.dsi.fastutil)
    compileOnly(libs.org.jetbrains.annotations)
    compileOnly(libs.com.mojang.authlib)
    compileOnly(libs.io.netty.netty.all)
    compileOnlyApi(libs.org.apache.commons.commons.lang3)
    compileOnlyApi(libs.net.kyori.adventure.api)
    compileOnlyApi(libs.net.kyori.adventure.text.minimessage)
    compileOnly(libs.net.kyori.adventure.platform.api)
    compileOnly(libs.com.google.guava.guava)

    testImplementation(libs.org.junit.jupiter.junit.jupiter)
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}


