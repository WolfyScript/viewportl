<div align="center"><img src="https://user-images.githubusercontent.com/41468455/158254076-d856f0db-12a0-4cd8-a186-568a656dd96f.png" alt="WolfyUtilities Banner" /></div>

## WolfyUtilities
![bstats_server](https://img.shields.io/bstats/servers/5114)
![spigot_down](https://img.shields.io/spiget/downloads/64124)
![spigot_stars](https://img.shields.io/spiget/stars/64124)
![github_commit](https://img.shields.io/github/last-commit/WolfyScript/WolfyUtilities)
![lines_code](https://img.shields.io/tokei/lines/github/WolfyScript/WolfyUtilities)

Core API that provides an API and Utils for plugins based on Spigot.

### APIs & Utils
- **API** is plugin dependent, which means there is one instance of the API per plugin.
- **Utils** are plugin independent. They can be used everywhere.
- **Registry** is bound to the core of WolfyUtilities, but can be accessed from anywhere. 

#### APIs
- **Inventory** - Functional API to create in-game GUIs.
- **Language** - Load JSON based language files and support multiple languages for GUIs, messages, etc.
- **Chat** - Send translatable messages, text click event callbacks, and more.
  - (**3.16.1+**) [KyoriPowered/adventure](https://github.com/KyoriPowered/adventure) implementation.
- **Config** - Simple config utilities.
- **NMS** - Fully featured **NBTTag API**, custom **RecipeIterator**, and some block and Inventory Utils.
- **CustomItems** - Create flexible custom items with settings like custom fuel, durability, actions, etc.

#### Registry
The Registry is the base of all custom content in WolfyUtilities and the plugins that build on it.
It allows you to register types & objects under unique namespaced keys. 
That not only allows the plugin to register things like CustomItems, etc., but it can be extended by other plugins too.

#### Utils:
- **NamespacedKey** - Unique key for all registrable content.
- **JSON** - Various Jackson utils that simplify de-/serialization
  - Custom de-/serializer for Bukkit objects
  - Easy de-/serialization from Registry values
    - Object  (See @OptionalKeyReference)
    - Type to object (See @KeyedTypeIdResolver & @KeyedTypeResolver)
- **Particles** - Configure custom particle effects & animations using JSON.
- **RandomCollection** - Weight based random collection.
- **Reflection** - Basic Reflection Utils.
- **Player Head** utils to set textures and more.
- Basic **MySQL** connection to run queries and updates.
- ItemBuilder to edit/create ItemStacks.
- Save player specific data.

### Plugins using WolfyUtilities

#### CustomCrafting
CustomCrafting is heavily based on these APIs and Utils, and is more of an extension than standalone plugin.  
CustomCrafting especially makes use of the InventoryAPI to create and manage the in-game RecipeCreators.
The JSON utils are used to load/save recipes & items from/to JSON, and to allow for custom settings inside the json files.

# Getting started

You can get the API from the public maven repo:

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
        <groupId>com.wolfyscript.wolfyutilities</groupId>
        <artifactId>wolfyutilities</artifactId>
        <version>3.16.1.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

To start using it you need to create an API instance for your plugin.<br>
It's best to initiate it in your constructor, so you don't mistakenly change the instance of the api.
(And we are able to use some options of the API **onLoad()**)

```java

import me.wolfyscript.lib.net.kyori.adventure.text.Component;

public class YourPlugin extends JavaPlugin {

  private final WolfyUtilities api;

  public YourPlugin() {
    super();
    //Create the instance for your plugin. We don't want to initialize the events yet (so set it to false)!
    api = WolfyUtilCore.getInstance().getAPI(this, false);
    this.chat = api.getChat();
    //We should set our prefix for the chat
    this.chat.setChatPrefix(Component.text("[", NamedTextColor.GRAY).append(Component.text("CC", NamedTextColor.AQUA))
            .append(Component.text("] ", NamedTextColor.DARK_GRAY)));
    //Or using the MiniMessage api
    this.chat.setChatPrefix(chat.getMiniMessage().parse("<gray>[<gradient:dark_aqua:aqua>CC</gradient><gray>]"));

    //Optionally you can set a custom cache object to cache data for your GUI.
    api.setInventoryAPI(new InventoryAPI<>(api.getPlugin(), api, CCCache.class));
  }

  @Override
  public void onEnable() {
    //Once the plugin is enabled we can initialize the events!
    this.api.initialize();
  }

}

```

More info about the API can be found in the [Wiki](https://github.com/WolfyScript/WolfyUtilities/wiki).
<br>