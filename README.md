## Viewportl
A Minecraft Inventory GUI framework to easily create **powerful, reactive, and efficient Minecraft GUIs**.

![github_commit](https://img.shields.io/github/last-commit/WolfyScript/WolfyUtilities)

### Compose
Viewportl uses the Compose Runtime library to make the creation of Minecraft UIs very easy.  

> [!note]
> Jetpack Compose, that you may know from the Android Ecosystem, is build on top of the Compose Runtime.  
> Compose Runtime is a standalone library separate from the Android stack and can be used outside of it.
> Its main goal is to make the creation of dynamic (UI) Trees easy and provides utilities for it.

### Work in Progress
This project is in its early stages and the API is not stable!  
**The API may receive breaking changes at anytime without notice!**

### Things I'm working / Gonna work on / Planned Features
With the move over to compose the todo-list was basically reset.  
Now the goal is to get to the same state as the project was in previously.  
* [ ] Routing
* [ ] Default route (initial route to select, useful for nested routing)
* [ ] Basic elements
  * [x] Column
    * [x] Basics, Vertical Item Arrangement
    * [x] Horizontal Alignment
  * [x] Row
    * [x] Basics, Horizontal Item Arrangement
    * [x] Vertical Alignment
  * [x] Box
  * [ ] Buttons
    * [x] on click
    * [x] multi-slot (larger than a single slot)
    * [ ] sound
  * [ ] Icon
    * [x] stack icon
    * [ ] direct item model support
  * [ ] Slot Input
* [x] Persistent Data (Stores)
* [ ] Data fetching (async/sync data fetching utils)
* [ ] Multi-player GUIs (multiple viewers, synchronisation)
* [ ] Multi Platform Support
  * [ ] Spigot
    * [ ] InvUI Renderer
      * [x] Rendering specified draw modifiers
      * [ ] Inventory Background texture
    * [x] InvUI Interaction Handler
  * [ ] Fabric
    * [ ] platform integration
    * [ ] InvUI Renderer
      * [ ] Inventory Background texture
    * [ ] InvUI Interaction Handler

## Resources
- https://github.com/JakeWharton/mosaic - Compose Runtime for Console UI
- https://www.youtube.com/watch?v=zMKMwh9gZuI - Explanation of how Compose constructs UIs
- https://www.youtube.com/watch?v=BjGX2RftXsU - How Modifiers work in Compose
- https://developer.android.com/develop/ui/compose/layouts/custom - How do Custom Layouts work?
- https://developer.android.com/develop/ui/compose/layering - Design of Compose (viewportl builds on the Runtime)
- https://developer.android.com/develop/ui/compose/phases - More about the Composition, Layout, and Draw Phases (+ all the other UI architecture docs)
- https://proandroiddev.com/understanding-jetpack-compose-internal-implementation-and-working-6db20733d4da - Deep Dive Especially the Gap Buffer
- https://arunkumar.dev/jetpack-compose-for-non-ui-tree-construction-and-code-generation/
- https://intelligiblebabble.com/compose-from-first-principles/
- https://github.com/JetBrains/compose-multiplatform-core
- https://github.com/JetBrains/compose-multiplatform
