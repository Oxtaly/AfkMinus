package com.oxtaly.afkminus.api;

import com.oxtaly.afkminus.AfkMinus;
import com.oxtaly.afkminus.AfkPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface AfkMinusAPI {
    static boolean isAFK(@NotNull ServerPlayerEntity player) {
        AfkPlayer afkPlayer = AfkMinus.PLAYER_LIST.get(player.getUuid());
        return afkPlayer != null && afkPlayer.isAfk();
    }
    static boolean isAFK(@NotNull UUID uuid) {
        AfkPlayer afkPlayer = AfkMinus.PLAYER_LIST.get(uuid);
        return afkPlayer != null && afkPlayer.isAfk();
    }
}
