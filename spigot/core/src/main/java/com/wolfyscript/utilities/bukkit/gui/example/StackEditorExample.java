package com.wolfyscript.utilities.bukkit.gui.example;

import com.wolfyscript.utilities.data.ItemStackDataKeys;
import com.wolfyscript.utilities.gui.GuiAPIManager;
import com.wolfyscript.utilities.gui.InteractionResult;
import com.wolfyscript.utilities.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.gui.components.ComponentGroupBuilder;
import com.wolfyscript.utilities.gui.components.StackInputSlotBuilder;
import com.wolfyscript.utilities.gui.reactivity.Signal;
import com.wolfyscript.utilities.platform.adapters.ItemStack;

public class StackEditorExample {

    private static class StackEditorStore {

        private ItemStack stack;

        public ItemStack getStack() {
            return stack;
        }

        public void setStack(ItemStack stack) {
            this.stack = stack;
        }
    }

    private enum Tab {
        NONE,
        DISPLAY_NAME,
        LORE,
    }

    static void register(GuiAPIManager manager) {
        manager.registerGuiFromFiles("stack_editor", (builder) -> {
                    builder.window((mainMenu) -> {
                        mainMenu.size(9 * 6);
                        // This is only called upon creation of the state. So this is not called when the signal is updated!

                        // Persistent data stores
                        Signal<StackEditorStore> stackToEdit = mainMenu.createSignal(StackEditorStore.class, viewRuntime -> new StackEditorStore());

                        // Weak data signals
                        Signal<Tab> selectedTab = mainMenu.createSignal(Tab.class, r -> Tab.NONE);

                        mainMenu.whenever(() -> {
                            StackEditorStore store = stackToEdit.get();
                            ItemStack itemStack = store == null ? null : store.getStack();
                            return itemStack == null || itemStack.getItem() == null || itemStack.getItem().getKey().equals("air");
                        }).then(group -> {
                            // Empty component! Perhaps add a note that the item is missing!
                        }).orElse(tabGroup ->
                                // Whenever the stack is available we can show the selected tab
                                tabGroup.match(Tab.class, selectedTab::get, cases -> {
                                    cases.select(value -> value.equals(Tab.DISPLAY_NAME), group -> displayNameTab(group, stackToEdit));
                                    cases.select(value -> value.equals(Tab.LORE), group -> group
                                            .component("lore_tab", ComponentGroupBuilder.class, loreTab -> loreTab
                                                    .component("edit_lore", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                            .interact((holder, details) -> {

                                                                return InteractionResult.cancel(true);
                                                            }))
                                                    .component("clear_lore", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                            .interact((holder, details) -> {

                                                                return InteractionResult.cancel(true);
                                                            }))));
                                }));
                        // The state of a component is only reconstructed if the slot it is positioned at changes.
                        // Here the slot will always have the same type of component, so the state is created only once.
                        mainMenu.component("stack_slot", StackInputSlotBuilder.class, inputSlotBuilder -> inputSlotBuilder
                                        .interact((guiHolder, interactionDetails) -> InteractionResult.cancel(false))
                                        .onValueChange(itemStack -> stackToEdit.update(stackEditorStore -> {
                                            stackEditorStore.setStack(itemStack);
                                            return stackEditorStore;
                                        }))
                                        .value(() -> stackToEdit.get().getStack()))
                                .component("display_name_tab_selector", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                        .interact((holder, details) -> {
                                            selectedTab.set(Tab.DISPLAY_NAME);
                                            return InteractionResult.cancel(true);
                                        }))
                                .component("lore_tab_selector", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                        .interact((holder, details) -> {
                                            selectedTab.set(Tab.LORE);
                                            return InteractionResult.cancel(true);
                                        }));
                    });
                }
        );
    }

    static void displayNameTab(ComponentGroupBuilder reactiveBuilder, Signal<StackEditorStore> stackToEdit) {
        reactiveBuilder.component("display_name_tab", ComponentGroupBuilder.class, displayNameClusterBuilder -> displayNameClusterBuilder
                .component("set_display_name", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                        .interact((runtime, details) -> {
                            runtime.setTextInputCallback((p, rn, s, strings) -> {
                                stackToEdit.update(store -> {
                                    var stack = store.getStack();
                                    if (stack != null) {
                                        stack.data().set(ItemStackDataKeys.CUSTOM_NAME, runtime.getWolfyUtils().getChat().getMiniMessage().deserialize(s));
                                    }
                                    return store;
                                });
                                return true;
                            });
                            return InteractionResult.cancel(true);
                        }))
                .component("reset_display_name", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                        .interact((holder, details) -> {
                            stackToEdit.update(store -> {
                                var stack = store.getStack();
                                if (stack != null) {
                                    stack.data().remove(ItemStackDataKeys.CUSTOM_NAME);
                                }
                                return store;
                            });
                            return InteractionResult.cancel(true);
                        }))
        );
    }

}
