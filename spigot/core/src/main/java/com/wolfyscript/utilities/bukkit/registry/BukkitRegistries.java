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

package com.wolfyscript.utilities.bukkit.registry;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.nbt.QueryNode;
import com.wolfyscript.utilities.bukkit.persistent.player.CustomPlayerData;
import com.wolfyscript.utilities.bukkit.persistent.world.CustomBlockData;
import com.wolfyscript.utilities.bukkit.tags.Tags;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.bukkit.world.items.CustomItemData;
import com.wolfyscript.utilities.bukkit.world.items.actions.Action;
import com.wolfyscript.utilities.bukkit.world.items.actions.Event;
import com.wolfyscript.utilities.bukkit.world.items.meta.Meta;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleAnimation;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleEffect;
import com.wolfyscript.utilities.bukkit.world.particles.animators.Animator;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.Shape;
import com.wolfyscript.utilities.bukkit.world.particles.timer.Timer;
import com.wolfyscript.viewportl.gui.components.Component;
import com.wolfyscript.utilities.registry.Registries;
import com.wolfyscript.utilities.registry.Registry;
import com.wolfyscript.utilities.registry.RegistryGUIComponentTypes;
import com.wolfyscript.utilities.registry.RegistrySimple;
import com.wolfyscript.utilities.registry.TypeRegistry;
import com.wolfyscript.utilities.registry.UniqueTypeRegistrySimple;
import org.jetbrains.annotations.NotNull;

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
 *     <li>(<b>Recommended</b>) via your API instance {@link WolfyUtilsBukkit#getRegistries()}</li>
 * </ul>
 */
public class BukkitRegistries extends Registries {

    public static final BukkitNamespacedKey ITEM_ACTION_VALUES = BukkitNamespacedKey.wolfyutilties("custom_item/actions/values");
    public static final BukkitNamespacedKey ITEM_ACTION_TYPES = BukkitNamespacedKey.wolfyutilties("custom_item/actions/types");
    public static final BukkitNamespacedKey ITEM_EVENT_VALUES = BukkitNamespacedKey.wolfyutilties("custom_item/events/values");
    public static final BukkitNamespacedKey ITEM_EVENT_TYPES = BukkitNamespacedKey.wolfyutilties("custom_item/events/types");

    public static final BukkitNamespacedKey GUI_COMPONENTS = BukkitNamespacedKey.wolfyutilties("gui/components");
    public static final BukkitNamespacedKey GUI_COMPONENT_BUILDERS = BukkitNamespacedKey.wolfyutilties("gui/component_builders");

    public static final BukkitNamespacedKey ITEM_CUSTOM_DATA = BukkitNamespacedKey.wolfyutilties("custom_item/data");
    public static final BukkitNamespacedKey ITEM_NBT_CHECKS = BukkitNamespacedKey.wolfyutilties("custom_item/nbt_checks");

    //Value registries
    private final RegistryCustomItem customItems;
    private final RegistryParticleEffect particleEffects;
    private final RegistryParticleAnimation particleAnimations;
    private final Registry<Action<?>> customItemActionValues;
    private final Registry<Event<?>> customItemEventValues;
    private final RegistryStackIdentifierParsers stackIdentifierParsers;
    //Tags
    private final Tags<CustomItem> itemTags;
    //Class Registries
    private final TypeRegistry<Animator> particleAnimators;
    private final TypeRegistry<Shape> particleShapes;
    private final TypeRegistry<Timer> particleTimer;
    private final TypeRegistry<Meta> customItemNbtChecks;
    private final TypeRegistry<Action<?>> customItemActions;
    private final TypeRegistry<Event<?>> customItemEvents;

    private final TypeRegistry<Component> guiComponents;
    private final RegistryGUIComponentTypes guiComponentBuilders;

    private final TypeRegistry<CustomBlockData> customBlockData;
    private final TypeRegistry<CustomPlayerData> customPlayerData;
    private final TypeRegistry<CustomItemData> customItemDataTypeRegistry;

    private final TypeRegistry<QueryNode<?>> nbtQueryNodes;

    public BukkitRegistries(WolfyCoreCommon core) {
        super(core);

        customItems = new RegistryCustomItem(this);
        particleEffects = new RegistryParticleEffect(this);
        particleAnimations = new RegistryParticleAnimation(this);
        customItemActionValues = new RegistrySimple<>(ITEM_ACTION_VALUES, this, (Class<Action<?>>)(Object) Action.class);
        customItemEventValues = new RegistrySimple<>(ITEM_EVENT_VALUES, this, (Class<Event<?>>)(Object) Event.class);
        stackIdentifierParsers = new RegistryStackIdentifierParsers(this);

        itemTags = new Tags<>(this);

        particleAnimators = new UniqueTypeRegistrySimple<>(new BukkitNamespacedKey(core.getWolfyUtils(), "particles/animators"), this);
        particleShapes = new UniqueTypeRegistrySimple<>(new BukkitNamespacedKey(core.getWolfyUtils(), "particles/shapes"), this);
        particleTimer = new UniqueTypeRegistrySimple<>(new BukkitNamespacedKey(core.getWolfyUtils(), "particles/timers"), this);
        customItemNbtChecks = new UniqueTypeRegistrySimple<>(ITEM_NBT_CHECKS, this);
        customItemDataTypeRegistry = new UniqueTypeRegistrySimple<>(ITEM_CUSTOM_DATA, this);
        customItemActions = new UniqueTypeRegistrySimple<>(ITEM_ACTION_TYPES, this);
        customItemEvents = new UniqueTypeRegistrySimple<>(ITEM_EVENT_TYPES, this);

        customPlayerData = new UniqueTypeRegistrySimple<>(new BukkitNamespacedKey(core.getWolfyUtils(), "persistent/player"), this);
        customBlockData = new UniqueTypeRegistrySimple<>(new BukkitNamespacedKey(core.getWolfyUtils(), "persistent/block"), this);

        this.nbtQueryNodes = new UniqueTypeRegistrySimple<>(new BukkitNamespacedKey(core.getWolfyUtils(), "nbt/query/nodes"), this);

        guiComponents = new RegistryGUIComponent(GUI_COMPONENTS, this);
        guiComponentBuilders = new RegistryGUIComponentBuilder(GUI_COMPONENT_BUILDERS, this);
    }

    @Override
    protected void indexTypedRegistry(@NotNull com.wolfyscript.utilities.registry.Registry<?> registry) {
        super.indexTypedRegistry(registry);
    }

    /**
     * This Registry contains all the {@link CustomItem} instances.
     * If you install your own item make sure to use your plugins name as the namespace.
     */
    public RegistryCustomItem getCustomItems() {
        return customItems;
    }

    /**
     * Gets the registry containing all the available {@link ParticleAnimation}s.
     *
     * @return The type registry of {@link ParticleAnimation}s
     */
    public RegistryParticleAnimation getParticleAnimations() {
        return particleAnimations;
    }

    /**
     * Gets the registry containing all the available {@link ParticleEffect}s.
     *
     * @return The type registry of {@link ParticleEffect}s
     */
    public RegistryParticleEffect getParticleEffects() {
        return particleEffects;
    }

    public Tags<CustomItem> getItemTags() {
        return itemTags;
    }

    public Registry<Action<?>> getCustomItemActionValues() {
        return customItemActionValues;
    }

    public Registry<Event<?>> getCustomItemEventValues() {
        return customItemEventValues;
    }

    public TypeRegistry<Shape> getParticleShapes() {
        return particleShapes;
    }

    /**
     * Gets the registry containing all the available NBTChecks for CustomItems.
     *
     * @return The type registry of {@link Meta}
     */
    public TypeRegistry<Meta> getCustomItemNbtChecks() {
        return customItemNbtChecks;
    }

    public TypeRegistry<CustomItemData> getCustomItemDataTypeRegistry() {
        return customItemDataTypeRegistry;
    }

    /**
     * Gets the registry containing all the available Animators, that can be used in {@link ParticleAnimation}s.
     *
     * @return The type registry of {@link Animator}
     */
    public TypeRegistry<Animator> getParticleAnimators() {
        return particleAnimators;
    }

    /**
     * Gets the registry containing all the available Timers, that can be used in {@link ParticleAnimation}s.
     *
     * @return The type registry of {@link Timer}
     */
    public TypeRegistry<Timer> getParticleTimer() {
        return particleTimer;
    }

    public TypeRegistry<Action<?>> getCustomItemActions() {
        return customItemActions;
    }

    public TypeRegistry<Event<?>> getCustomItemEvents() {
        return customItemEvents;
    }

    public TypeRegistry<CustomPlayerData> getCustomPlayerData() {
        return customPlayerData;
    }

    public TypeRegistry<CustomBlockData> getCustomBlockData() {
        return customBlockData;
    }

    public TypeRegistry<QueryNode<?>> getNbtQueryNodes() {
        return nbtQueryNodes;
    }

    @Override
    public RegistryGUIComponentTypes getGuiComponentTypes() {
        return guiComponentBuilders;
    }

    public RegistryStackIdentifierParsers getStackIdentifierParsers() {
        return stackIdentifierParsers;
    }
}
