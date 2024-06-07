package com.wolfyscript.utilities.bukkit.gui.example;

import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.viewportl.gui.GuiAPIManager;
import com.wolfyscript.viewportl.gui.example.CounterExampleKotlinKt;
import com.wolfyscript.viewportl.gui.example.StackEditorExampleKotlinKt;

public class TestGUI {

    private final WolfyCore core;

    public TestGUI(WolfyCore core) {
        this.core = core;
    }

    public void initWithConfig() {
        GuiAPIManager manager = core.getWolfyUtils().getGuiManager();
        CounterExampleKotlinKt.registerExampleCounter(manager);
        StackEditorExampleKotlinKt.registerStackEditor(manager);
    }


}
