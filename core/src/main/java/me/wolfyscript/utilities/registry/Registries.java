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

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.Meta;
import me.wolfyscript.utilities.api.inventory.tags.Tags;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.animators.Animator;
import me.wolfyscript.utilities.util.particles.timer.Timer;

import java.util.HashMap;
import java.util.Map;

/**
 * Includes all the Registries inside WolfyUtilities.<br>
 * <br>
 * To use the registries you need to get an instance of this class.<br>
 * You should always try to not use the static method, as it can make the code less maintainable.<br>
 * If it is possible to access the instance of your WU API, then that should be used instead!<br>
 * <br>
 *
 * <strong>Get an instance:</strong>
 * <ul>
 *     <li>(<b>Recommended</b>) via your API instance {@link WolfyUtilities#getRegistries()}</li>
 *     <li>via static method {@link WolfyUtilCore#getInstance()} & {@link WolfyUtilCore#getRegistries()} (This should only be used in cases where you have no access to your API instance!)</li>
 * </ul>
 */
public class Registries {

    private final WolfyUtilCore core;

    private final Map<Class<? extends Keyed>, IRegistry<?>> REGISTRIES_BY_TYPE = new HashMap<>();
    private final Map<NamespacedKey, IRegistry<?>> REGISTRIES_BY_KEY = new HashMap<>();

    void indexTypedRegistry(IRegistry<?> registry) {
        Preconditions.checkArgument(!REGISTRIES_BY_KEY.containsKey(registry.getKey()), "A registry with the key \"" + registry.getKey() + "\" already exists!");
        REGISTRIES_BY_KEY.put(registry.getKey(), registry);

        //Index them by type if available
        if(registry instanceof RegistrySimple<?> simpleRegistry && simpleRegistry.getType() != null) {
            Preconditions.checkArgument(!REGISTRIES_BY_TYPE.containsKey(simpleRegistry.getType()), "A registry with that type already exists!");
            REGISTRIES_BY_TYPE.put(simpleRegistry.getType(), simpleRegistry);
        }
    }

    /**
     * Gets a Registry by the type it contains.
     * The Registry has to be created with the class of the type (See: {@link RegistrySimple#RegistrySimple(NamespacedKey, Registries, Class)}).
     *
     * @param type The class of the type the registry contains.
     * @param <V> The type the registry contains.
     * @return The registry of the specific type; or null if not available.
     */
    @SuppressWarnings("unchecked")
    public <V extends Keyed> Registry<V> getByType(Class<V> type) {
        return (Registry<V>) REGISTRIES_BY_TYPE.get(type);
    }

    public IRegistry<?> getByKey(NamespacedKey key) {
        return REGISTRIES_BY_KEY.get(key);
    }

    public <V extends IRegistry<?>> V getByKeyOfType(NamespacedKey key, Class<V> registryType) {
        var registry = getByKey(key);
        return registryType.cast(registry);
    }

    /**
     * This Registry contains all the {@link CustomItem} instances.
     * If you install your own item make sure to use your plugins name as the namespace.
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
    public final Registry<CustomData.Provider<?>> CUSTOM_ITEM_DATA;
    public final RegistryParticleEffect PARTICLE_EFFECTS;
    public final RegistryParticleAnimation PARTICLE_ANIMATIONS;
    //Tags
    public final Tags<CustomItem> ITEM_TAGS;
    //Class Registries
    public final TypeRegistry<Animator> PARTICLE_ANIMATORS;
    public final TypeRegistry<Timer> PARTICLE_TIMER;
    public final TypeRegistry<Meta> CUSTOM_ITEM_NBT_CHECKS;

    public Registries(WolfyUtilCore core) {
        this.core = core;

        CUSTOM_ITEMS = new RegistryCustomItem(this);
        CUSTOM_ITEM_DATA = new RegistrySimple<>(new NamespacedKey(core, "custom_item_data"), this);
        PARTICLE_EFFECTS = new RegistryParticleEffect(this);
        PARTICLE_ANIMATIONS = new RegistryParticleAnimation(this);

        ITEM_TAGS = new Tags<>(this);

        PARTICLE_ANIMATORS = new TypeRegistrySimple<>(new NamespacedKey(core, "particle_animators"), this);
        PARTICLE_TIMER = new TypeRegistrySimple<>(new NamespacedKey(core, "particle_timers"), this);
        CUSTOM_ITEM_NBT_CHECKS = new TypeRegistrySimple<>(new NamespacedKey(core, "custom_item_nbt_checks"), this);
    }

    public WolfyUtilCore getCore() {
        return core;
    }
}
