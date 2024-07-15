## Viewportl
> Previously **WolfyUtils**

![github_commit](https://img.shields.io/github/last-commit/WolfyScript/WolfyUtilities)

Viewportl is a Minecraft Inventory GUI framework, designed to easily create reactive GUIs.  
Based on the well-known [Signal reactive system](https://www.solidjs.com/guides/reactivity#how-it-works), popularized by web-frameworks like SolidJS, leptos-rs, etc.

It aims to provide a multi-platform GUI framework to quickly and easily integrate GUIs into any plugin/mod.

This repo was previously known as WolfyUtilities, but the GUI API got so advanced, that it deserves a separate library.   
[**Current WolfyUtils Spigot Implementation**](https://github.com/WolfyScript/WolfyUtils-Spigot)

### Still in WIP (Alpha)
This project is very much still in early alpha!   
**The API may receive breaking changes at anytime without notice!**

### Planned (TODO)
These are few of the things planned for the near future.  
The list is not complete, and things may be added/removed along the way.
* [ ] Move utils and platform compatibility to a separate project
  * Makes this library optional for other plugins, that don't require GUIs
* [ ] Complete name change
  * [ ] Rename packages
  * [ ] Rename strings & other appearances of WolfyUtils
* [ ] Multi Platform Support
  * [ ] Spigot
  * [ ] Sponge
  * [ ] possibly more in the future

### Current API Examples
Examples can be found in the `examples` directory of the `common` implementation.  
[**-Example Directory-**](https://github.com/WolfyScript/viewportl/tree/master/common/src/main/java/com/wolfyscript/viewportl/gui/example)

Both Kotlin and Java examples can be found there.  
**Note that Kotlin is the recommended for the best Development Experience.**
