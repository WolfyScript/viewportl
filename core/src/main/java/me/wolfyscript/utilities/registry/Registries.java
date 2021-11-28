/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.registry;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.Meta;
import me.wolfyscript.utilities.api.inventory.tags.Tags;
import me.wolfyscript.utilities.util.ClassRegistry;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.particles.animators.Animator;
import me.wolfyscript.utilities.util.particles.timer.Timer;

import java.util.HashMap;
import java.util.Map;

public class Registries {

    private final Map<Class<? extends Keyed>, IRegistry<?>> REGISTRIES_BY_TYPE = new HashMap<>();

    void indexTypedRegistry(IRegistry<?> registry) {
        if(registry instanceof RegistrySimple<?> simpleRegistry && simpleRegistry.getType() != null) {
            REGISTRIES_BY_TYPE.put(simpleRegistry.getType(), simpleRegistry);
        }
    }

    /**
     * Gets a Registry by the type it contains.
     * The Registry has to be created with the class of the type (See: {@link RegistrySimple (Class)}).
     *
     * @param type The class of the type the registry contains.
     * @param <V> The type the registry contains.
     * @return The registry of the specific type; or null if not available.
     */
    @SuppressWarnings("unchecked")
    public <V extends Keyed> IRegistry<V> getByType(Class<V> type) {
        return (IRegistry<V>) REGISTRIES_BY_TYPE.get(type);
    }

    /**
     * The Registry for all of the {@link CustomItem} instances.
     */
    public final RegistryCustomItem CUSTOM_ITEMS;

    /**
     * Contains {@link CustomData.Provider} that can be used in any Custom Item from the point of registration.
     * <br>
     * You can register any CustomData you might want to add to your CustomItems and then save and load it from config too.
     * <br>
     * It allows you to save and load custom data into a CustomItem and makes things a lot easier if you have some items that perform specific actions with the data etc.
     * <br>
     * For example CustomCrafting registers its own CustomData, that isn't in this core API, for its Elite Workbenches that open up custom GUIs dependent on their CustomData.
     * And also the Recipe Book uses a CustomData object to store some data.
     */
    public final IRegistry<CustomData.Provider<?>> CUSTOM_ITEM_DATA;
    public final RegistryParticleEffect PARTICLE_EFFECTS;
    public final RegistryParticleAnimation PARTICLE_ANIMATIONS;

    //Tags
    public final Tags<CustomItem> ITEM_TAGS = new Tags<>();

    //Class Registries
    public final ClassRegistry.SimpleClassRegistry<Animator> PARTICLE_ANIMATORS = new ClassRegistry.SimpleClassRegistry<>();
    public final ClassRegistry.SimpleClassRegistry<Timer> PARTICLE_TIMER = new ClassRegistry.SimpleClassRegistry<>();
    public final ClassRegistry.SimpleClassRegistry<Meta> CUSTOM_ITEM_NBT_CHECKS = new ClassRegistry.SimpleClassRegistry<>();

    {
        CUSTOM_ITEMS = new RegistryCustomItem();
        CUSTOM_ITEM_DATA = new RegistrySimple<>();
        PARTICLE_EFFECTS = new RegistryParticleEffect(this);
        PARTICLE_ANIMATIONS = new RegistryParticleAnimation(this);
    }

}
