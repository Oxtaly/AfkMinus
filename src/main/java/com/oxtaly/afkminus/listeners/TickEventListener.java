package com.oxtaly.afkminus.listeners;

import com.oxtaly.afkminus.AfkMinus;
import com.oxtaly.afkminus.AfkPlayer;
import com.oxtaly.afkminus.config.ConfigData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;

public abstract class TickEventListener {
    private static int tick = -1;

    public static void register() {
        ServerTickEvents.START_WORLD_TICK.register(TickEventListener::execute);
    }

    private static void execute(ServerWorld serverWorld) {
        tick++;
        if(tick < 20) {
            return;
        }
        tick = 0;
        ConfigData config = AfkMinus.CONFIG_MANAGER.getData();
        for (var entry : AfkMinus.PLAYER_LIST.entrySet()) {
            AfkPlayer afkPlayer = entry.getValue();
            if(config.afkTag != null && !config.afkTag.isEmpty()) {
                if(config.addTagOnAfk) {
                    if(!afkPlayer.hasAfkTag() && afkPlayer.isAfk()) {
                        afkPlayer.getPlayer().addCommandTag(config.afkTag);
                        afkPlayer.setHasAfkTag(true);
                        AfkMinus.LOGGER.debug(String.format("Adding afk tag to player %s", afkPlayer.getPlayer().getName().getString()));
                    }
                }
                if(afkPlayer.hasAfkTag() && !afkPlayer.isAfk()) {
                    afkPlayer.getPlayer().removeCommandTag(config.afkTag);
                    afkPlayer.setHasAfkTag(false);
                    AfkMinus.LOGGER.debug(String.format("Removed afk tag from player %s", afkPlayer.getPlayer().getName().getString()));
                }
            }
        };
        return;
    }
}
