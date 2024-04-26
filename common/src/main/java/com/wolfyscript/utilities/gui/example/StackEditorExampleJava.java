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

package com.wolfyscript.utilities.gui.example;

import com.wolfyscript.utilities.data.ItemStackDataKeys;
import com.wolfyscript.utilities.gui.GuiAPIManager;
import com.wolfyscript.utilities.gui.interaction.InteractionResult;
import com.wolfyscript.utilities.gui.components.ComponentGroup;
import com.wolfyscript.utilities.gui.components.StackInputSlot;
import com.wolfyscript.utilities.gui.reactivity.Signal;
import com.wolfyscript.utilities.platform.adapters.ItemStack;

public class StackEditorExampleJava {

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
        manager.registerGui("stack_editor", (window) -> {
                    window.setSize(9 * 6);

                    window.routes((router) -> {
                        router.route(path -> { /* empty = Root path */ }, view -> {
                            // This is only called upon creation of the state. So this is not called when the signal is updated!

                            Signal<StackEditorStore> stackToEdit = window.createSignal(StackEditorStore.class, viewRuntime -> new StackEditorStore());
                            Signal<Tab> selectedTab = window.createSignal(Tab.class, r -> Tab.NONE);

                            view.whenever(() -> {
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
                                                .group("lore_tab", loreTab -> {
                                                    loreTab.button("edit_lore", it -> {
                                                        it.setOnClick((holder, details) -> {
                                                            return InteractionResult.cancel(true);
                                                        });
                                                    });
                                                    loreTab.button("clear_lore", it -> {
                                                        it.setOnClick((holder, details) -> {
                                                            return InteractionResult.cancel(true);
                                                        });
                                                    });
                                                }));
                                    }));

                            view.component("stack_slot", StackInputSlot.class, inputSlot -> {
                                inputSlot.setOnClick((guiHolder, interactionDetails) -> InteractionResult.cancel(false));
                                inputSlot.setOnValueChange(itemStack -> stackToEdit.update(stackEditorStore -> {
                                    stackEditorStore.setStack(itemStack);
                                    return stackEditorStore;
                                }));
                                inputSlot.setValue(stackToEdit.get().getStack());
                            });
                            view.button("display_name_tab_selector", it -> {
                                it.setOnClick((holder, details) -> {
                                    selectedTab.set(Tab.DISPLAY_NAME);
                                    return InteractionResult.cancel(true);
                                });
                            });
                            view.button("lore_tab_selector", it -> {
                                it.setOnClick((holder, details) -> {
                                    selectedTab.set(Tab.LORE);
                                    return InteractionResult.cancel(true);
                                });
                            });

                        }, subRoutes -> { });
                    });
                }
        );
    }

    static void displayNameTab(ComponentGroup reactiveBuilder, Signal<StackEditorStore> stackToEdit) {
        reactiveBuilder.group("display_name_tab", displayNameClusterBuilder -> {
                    displayNameClusterBuilder.button("set_display_name", button -> {
                        button.setOnClick((runtime, details) -> {
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
                        });
                    });
                    displayNameClusterBuilder.button("reset_display_name", button -> {
                        button.setOnClick((holder, details) -> {
                            stackToEdit.update(store -> {
                                var stack = store.getStack();
                                if (stack != null) {
                                    stack.data().remove(ItemStackDataKeys.CUSTOM_NAME);
                                }
                                return store;
                            });
                            return InteractionResult.cancel(true);
                        });
                    });
                }
        );
    }

}
