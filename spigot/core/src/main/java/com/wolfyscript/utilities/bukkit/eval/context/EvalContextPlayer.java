package com.wolfyscript.utilities.bukkit.eval.context;

import com.wolfyscript.utilities.eval.context.EvalContext;
import org.bukkit.entity.Player;

public class EvalContextPlayer extends EvalContext {

    private final Player player;

    public EvalContextPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
