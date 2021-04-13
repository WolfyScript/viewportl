### WolfyUtilities
###### _by WolfyScript_
##
This API is the core that powers all of my plugins (right now only CustomCrafting).

##### WolfyUtilities contains a lot of useful APIs, such as:
- **InventoryAPI** to create in-game GUIs
- **ConfigAPI** to easily manage your configs needs. It supports _YAML_ and _JSON_
- **LanguageAPI** that allows you to connect your plugin to a language file and support multiple languages for GUIs,
  messages, etc.!

##### and Utils:

- Easily send **chat messages** to players which can _execute code_ on click.
- **Serialize/Deserialize ItemStacks** via Base64
- Basic **Reflection** Utils
- **Skull texture** utils
- Basic **MySQL** connection (WIP)
- ItemBuilder class (WIP)

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
        <version>1.6.3.22</version>
    </dependency>
</dependencies>
```

To start using it you need to create an API instance for your plugin.<br>
It's best to initiate it in your constructor, so you don't mistakenly change the instance of the api.
(And we are able to use some options of the API **onLoad()**)

```java
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;

public class YourPlugin extends JavaPlugin {
    
    private final WolfyUtilities api;

    public YourPlugin() {
        super();
        //Create the instance for your plugin. We don't want to initialize the events yet (so set it to false)!
        api = WolfyUtilities.get(this, false);
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

The most important part to start using GUIs and literally anything else is to set up a language to use.<br>
Language Files are loaded from the `YourPlugin/lang` directory.<br>
You can load and set two languages at once: one active language, and one fallback language.

```java
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;

public class YourPlugin extends JavaPlugin {
    
    public void loadLang(){
        //Save the language file resource to the plugin folder.
        saveResource("lang/en_US.json", true);
        //Get the LanguageAPI of the api
        LanguageAPI languageAPI = this.api.getLanguageAPI();
        //Create the language
        Language fallBackLanguage = new Language(customCrafting, "en_US");
        //Register the language. The first registered language will be automatically be used as the fallback and active language.
        languageAPI.registerLanguage(fallBackLanguage);
        
        
        //- Optionally load another active language. -
        saveResource("lang/de_DE.json", true);
        //Optionally you can register another active language.
        Language language = new Language(customCrafting, "de_DE");
        languageAPI.registerLanguage(language);
        //Just requires to set the active language manually.
        languageAPI.setActiveLanguage(language);
    }
    
}
```

More coming soon™

