## Viewportl

![github_commit](https://img.shields.io/github/last-commit/WolfyScript/WolfyUtilities)

Viewportl is a Minecraft Inventory GUI framework, designed to easily create reactive GUIs.  
Based on the well-known [Signal reactive system](https://www.solidjs.com/guides/reactivity#how-it-works), popularized by web-frameworks like SolidJS, leptos-rs, etc.

It aims to provide a multi-platform GUI framework to quickly and easily integrate GUIs into any plugin/mod.

### Still in WIP (Alpha)
This project is very much still in early alpha!   
**The API may receive breaking changes at anytime without notice!**

### Planned (TODO)
These are few of the things planned for the near future.  
The list is not complete, and things may be added/removed along the way.
* [ ] Move utils and platform compatibility to a separate project
  * [x] Move everything not UI related to [scafall](https://github.com/WolfyScript/scafall)
  * [x] Provide API Entrypoint to load the viewportl instance on top of scafall
  * [x] ~~Check if Java interop can be kept~~ **Java interop won't be a priority! Kotlin first**
  * [ ] Create sample projects for a tutorial and testing
    * [x] Spigot single-platform example
    * [x] Sponge single-platform example
    * [ ] Create multi-platform example (requires other platform implementations)
* [ ] Multi Platform Support
  * [ ] Spigot (High priority)
    * [x] platform integration
    * [x] Scheduler
    * [x] InvUI Renderer
    * [ ] InvUI Interaction Handler
      * [ ] Use native event system
      * [ ] Components
  * [ ] Sponge
    * [x] platform integration
    * [x] InvUI Renderer
      * [ ] Fix inventory title update
    * [x] InvUI Interaction Handler
      * [x] Components

### Current API Examples
Examples can be found in the `examples` directory of the `common` implementation.  
[**-Example Directory-**](https://github.com/WolfyScript/viewportl/tree/master/common/src/main/java/com/wolfyscript/viewportl/gui/example)

Both Kotlin and Java examples can be found there.  
**Note that Kotlin is recommended for the best Development Experience.**
