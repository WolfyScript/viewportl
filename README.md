<div align="center"><img src="https://github.com/WolfyScript/WolfyUtilities/assets/41468455/f6c49bf5-313d-4a1f-856a-e0a212570c04" alt="WolfyUtilities Banner" /></div>

## WolfyUtilities
![bstats_server](https://img.shields.io/bstats/servers/5114?label=Servers)  
![spigot_down](https://img.shields.io/spiget/downloads/64124?label=Spigot+Downloads)
![spigot_stars](https://img.shields.io/spiget/stars/64124?label=Spigot+Rating)  
![github_commit](https://img.shields.io/github/last-commit/WolfyScript/WolfyUtilities)

Core API that provides an API and Utils for plugins based on Spigot.

This repository contains the platform independent API and Implementation.  
For the platform specific internal implementations see:
- [**Spigot Implementation**](https://github.com/WolfyScript/WolfyUtils-Spigot)
- [**Sponge Implementation**](https://github.com/WolfyScript/WolfyUtils-Sponge)

## Common API
The common API is available via Maven.  
It is currently very spare and does not work on its own!  
<details>
<summary>Maven</summary>

```xml
<repositories>
    <repository>
        <id>wolfyscript-public</id>
        <url>https://maven.wolfyscript.com/repository/public/</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>com.wolfyscript.wolfyutils</groupId>
        <artifactId>wolfyutilities</artifactId>
        <version>4.16-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```
</details>

<details>
<summary>Gradle</summary>

#### Kotlin DSL

```kotlin
repositories {
    maven("https://maven.wolfyscript.com/repository/public/")
}
```

```kotlin

dependencies {
    implementation("com.wolfyscript.wolfyutils", "wolfyutilities","4.16-SNAPSHOT")
}
```

#### Groovy
```groovy
repositories {
    maven { 
        url "https://maven.wolfyscript.com/repository/public/" 
    }
}
```
```groovy
dependencies {
    implementation "com.wolfyscript.wolfyutils:wolfyutilities:4.16-SNAPSHOT"
}
```

</details>

More info about the API can be found in the [Wiki](https://github.com/WolfyScript/WolfyUtilities/wiki).
<br>
