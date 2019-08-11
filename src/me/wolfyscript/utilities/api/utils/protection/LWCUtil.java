package me.wolfyscript.utilities.api.utils.protection;

import com.griefcraft.lwc.LWC;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LWCUtil {

    //LWC Utils! Only tested for the LWC fork by Brokkonaut!

    private static LWC lwc = LWC.getInstance();

    public static boolean hasPermToInteract(Player player, Entity entity) {
        return lwc.canAccessProtection(player, entity.getLocation().getBlock());
    }

    public static boolean canAccessprotection(Player player, Block block) {
        return lwc.canAccessProtection(player, block);
    }

}
