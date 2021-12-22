<div align="center">
  <img src="docs/wu_banner.png" alt="WolfyUtilities Banner" />
</div>

## WolfyUtilities
![bstats_server](https://img.shields.io/bstats/servers/5114)
![spigot_down](https://img.shields.io/spiget/downloads/64124)
![spigot_stars](https://img.shields.io/spiget/stars/64124)
![github_commit](https://img.shields.io/github/last-commit/WolfyScript/WolfyUtilities)
![lines_code](https://img.shields.io/tokei/lines/github/WolfyScript/WolfyUtilities)

This API is the core that powers all of my plugins like CustomCrafting, ArmorStandTool, and possible future ones.

#### WolfyUtilities contains a lot of useful APIs, such as:

- **InventoryAPI** to create and manage in-game GUIs.
- **ConfigAPI** to easily manage _YAML_ and _JSON_ configs.
- **LanguageAPI** to load languages and support multiple languages for GUIs, messages, etc.!
- **ChatAPI** to send translatable messages, make clickable text execute code, and more.
- **NMS API** including a fully featured **NBTTag API**, custom **RecipeIterator**, and some block and Inventory Utils.
- **CustomItems** allow creating custom items with special settings.

#### and Utils:

- **Serialize/Deserialize ItemStacks** via Base64.
- Basic **Reflection** Utils.
- **Player Head** utils to set textures and more.
- Basic **MySQL** connection to run queries and updates.
- ItemBuilder to edit/create ItemStacks.
- Save player specific data.

The API is build with customization in mind, so that you can register a lot of your own settings, data, CustomItems into
the Registry and share it across plugins.  
It constantly receives updates to improve, fix issues, and make it as easy as possible to use.

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
        <artifactId>core</artifactId>
        <version>2.16.2.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

To start using it you need to create an API instance for your plugin.<br>
It's best to initiate it in your constructor, so you don't mistakenly change the instance of the api.
(And we are able to use some options of the API **onLoad()**)

```java
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.WolfyUtilCore;

public class YourPlugin extends JavaPlugin {
    
    private final WolfyUtilities api;

    public YourPlugin() {
        super();
        //Create the instance for your plugin. We don't want to initialize the events yet (so set it to false)!
        api = WolfyUtilCore.getInstance().getAPI(this, false);
        this.chat = api.getChat();
        //We should set our prefixes for the chat and console.
        this.chat.setInGamePrefix("§7[§3CC§7] ");
        this.chat.setConsolePrefix("§7[§3CC§7] ");
        
        //Optionally you can set a custom cache object to save custom data for your GUI. (More detail soon)
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