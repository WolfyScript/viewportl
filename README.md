<div align="center"><img src="https://user-images.githubusercontent.com/41468455/158254076-d856f0db-12a0-4cd8-a186-568a656dd96f.png" alt="WolfyUtilities Banner" /></div>

## WolfyUtilities
![bstats_server](https://img.shields.io/bstats/servers/5114?label=Servers)  
![spigot_down](https://img.shields.io/spiget/downloads/64124?label=Spigot+Downloads)
![spigot_stars](https://img.shields.io/spiget/stars/64124?label=Spigot+Rating)  
![github_commit](https://img.shields.io/github/last-commit/WolfyScript/WolfyUtilities)

Core API that provides an API and Utils for plugins based on Spigot.

**In the recent versions WolfyUtilities was redesigned.  
This repository only contains the platform independent API and Utils**  
That is in preparation to support other platforms like Sponge.
- Spigot Implementation – [WolfyScript/WolfyUtils-Spigot](https://github.com/WolfyScript/WolfyUtils-Spigot)

## Updates
Updates are planned to be released in their dedicated repositories in the future.  
For now, they might still be published here as a mirror.

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

# Implementations
Usually you would use the platform dependent implementation to make use of the API and utils in your plugin.

## Spigot
The latest Spigot specific implementation (4.16-SNAPSHOT) is nearly completely backwards compatible with previous WolfyUtilities versions, except some internal changes.  
More info in the new repo: [WolfyScript/WolfyUtils-Spigot](https://github.com/WolfyScript/WolfyUtils-Spigot).

## Sponge
This implementation is still being planned and developed.  
No ETA yet!

More info about the API can be found in the [Wiki](https://github.com/WolfyScript/WolfyUtilities/wiki).
<br>