package com.wolfyscript.utilities.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.InjectableValues;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper;
import com.wolfyscript.utilities.WolfyUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GuiAPIManagerImpl implements GuiAPIManager {

    private static final Pattern GUI_FILE_PATTERN = Pattern.compile(".*\\.(conf|json)");

    private final WolfyUtils wolfyUtils;
    private final BiMap<String, Router> clustersMap = HashBiMap.create();
    private final BiMap<String, Function<ViewRuntime, RouterBuilder>> entriesMap = HashBiMap.create();

    private File guiDataSubFolder;
    private String guiResourceDir;

    private final Long2ObjectMap<ViewRuntime> VIEW_RUNTIMES = new Long2ObjectOpenHashMap<>();
    private final Multimap<String, Long> CACHED_VIEW_RUNTIMES = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Multimap<UUID, Long> VIEW_RUNTIMES_PER_PLAYER = MultimapBuilder.hashKeys().hashSetValues().build();

    public GuiAPIManagerImpl(WolfyUtils wolfyUtils) {
        this.wolfyUtils = wolfyUtils;
        this.guiDataSubFolder = new File(wolfyUtils.getDataFolder(), "gui");
        this.guiResourceDir = "com/wolfyscript/utilities/gui/example"; // TODO: Customization
    }

    public void setGuiResourceDir(String path) {
        this.guiResourceDir = path;
    }

    public void setGuiDataDir(File dir) {
        this.guiDataSubFolder = dir;
    }

    @Override
    public Stream<ViewRuntime> getViewManagersFor(UUID uuid) {
        return VIEW_RUNTIMES_PER_PLAYER.get(uuid).stream().map(id -> VIEW_RUNTIMES.get(id));
    }

    @Override
    public Stream<ViewRuntime> getViewManagersFor(UUID uuid, String guiID) {
        Collection<Long> ids = CACHED_VIEW_RUNTIMES.get(guiID);
        return VIEW_RUNTIMES_PER_PLAYER.get(uuid).stream()
                .filter(ids::contains)
                .map(id -> VIEW_RUNTIMES.get(id));
    }

    @Override
    public void registerGui(String id, BiConsumer<ReactiveSource, RouterBuilder> consumer) {
        // TODO: maybe wrap in an extra object?
        registerGui(id, (viewManager) -> {
            BuildContext buildContext = new BuildContext(((ViewRuntimeImpl) viewManager).getReactiveSource(), wolfyUtils);

            RouterBuilder builder = new RouterBuilderImpl(id, wolfyUtils, buildContext);
            consumer.accept(buildContext.getReactiveSource(), builder);
            return builder;
        });
    }

    @Override
    public void createViewAndThen(String guiId, Consumer<ViewRuntime> callback, UUID... uuids) {
        getGui(guiId).ifPresent(constructor -> {
            Set<UUID> viewers = Set.of(uuids);
            Collection<Long> viewManagersForID = CACHED_VIEW_RUNTIMES.get(guiId);

            viewManagersForID.stream()
                    .map(id -> VIEW_RUNTIMES.get(id))
                    .filter(viewManager -> viewManager.getViewers().equals(viewers))
                    .findFirst()
                    .ifPresentOrElse(callback, () -> {
                        // Construct the new view manager async, so it doesn't affect the main thread!
                        wolfyUtils.getCore().platform().scheduler().asyncTask(wolfyUtils, () -> {
                            ViewRuntimeImpl viewManager = new ViewRuntimeImpl(wolfyUtils, constructor, viewers);

                            synchronized (VIEW_RUNTIMES) {
                                viewManagersForID.add(viewManager.getId());
                                VIEW_RUNTIMES.put(viewManager.getId(), viewManager);
                            }
                            synchronized (VIEW_RUNTIMES_PER_PLAYER) {
                                for (UUID viewer : viewers) {
                                    VIEW_RUNTIMES_PER_PLAYER.put(viewer, viewManager.getId());
                                }
                            }
                            callback.accept(viewManager);
                        });
                    });
        });
    }

    protected void registerGui(String id, Function<ViewRuntime, RouterBuilder> constructor) {
        Preconditions.checkArgument(!clustersMap.containsKey(id), "A cluster with the id '" + id + "' is already registered!");
        entriesMap.put(id, constructor);
    }

    @Override
    public void createViewAndOpen(String guiId, UUID... players) {
        createViewAndThen(guiId, ViewRuntime::open, players);
    }

    @Override
    public Optional<Function<ViewRuntime, RouterBuilder>> getGui(String id) {
        return Optional.ofNullable(entriesMap.get(id));
    }

    @Override
    public void registerGuiFromFiles(String id, BiConsumer<ReactiveSource, RouterBuilder> consumer) {
        HoconMapper mapper = wolfyUtils.getJacksonMapperUtil().getGlobalMapper(HoconMapper.class);
        wolfyUtils.exportResources(guiResourceDir + "/" + id, new File(guiDataSubFolder, "/includes/" + id), true, GUI_FILE_PATTERN);

        registerGui(id, viewManager -> {
            try {
                File file = new File(guiDataSubFolder, id + "/entry.conf"); // Look for user-override
                if (!file.exists()) {
                    file = new File(guiDataSubFolder, "includes/" + id + "/entry.conf"); // Fall back to includes version
                    if (!file.exists() || !file.isFile())
                        throw new IllegalArgumentException("Cannot find gui index file! Expected: " + file.getPath());
                }

                BuildContext context = new BuildContext(((ViewRuntimeImpl) viewManager).getReactiveSource(), wolfyUtils);
                var injectableValues = new InjectableValues.Std();
                injectableValues.addValue("parent", null);
                injectableValues.addValue(WolfyUtils.class, wolfyUtils);
                injectableValues.addValue("wolfyUtils", wolfyUtils);
                injectableValues.addValue("context", context);
                injectableValues.addValue(BuildContext.class, context);

                RouterBuilder builder = mapper.readerFor(new TypeReference<RouterBuilderImpl>() {
                }).with(injectableValues).readValue(file);
                consumer.accept(context.getReactiveSource(), builder);
                return builder;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
