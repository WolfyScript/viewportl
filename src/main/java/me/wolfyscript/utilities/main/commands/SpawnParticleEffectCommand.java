package me.wolfyscript.utilities.main.commands;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class SpawnParticleEffectCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(WolfyUtilities.hasPermission(commandSender, "wolfyutilities.command.spawn_particle_effect")){
                if(args.length >= 1){
                    String effectName = args[0];
                    if(args.length == 2){
                        ParticleEffects.spawnEffectOnPlayer(effectName, EquipmentSlot.HAND, player);
                    }else{
                        Block block = player.getTargetBlockExact(10);
                        if(block != null){
                            ParticleEffects.spawnEffectOnBlock(effectName, block);
                        }
                    }
                }
            }
        }
        return false;
    }
}
