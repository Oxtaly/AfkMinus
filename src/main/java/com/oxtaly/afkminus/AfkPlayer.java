package com.oxtaly.afkminus;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AfkPlayer {
    private long lastInputTime = Util.getEpochTimeMs();
    private final ServerPlayerEntity player;
    private boolean forcedAfk = false;
    private Text forcedAfkSource = null;
    private long forcedAfkTime = 0;
    private boolean hasAfkTag = false;

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public long getLastInputTime() {
        return lastInputTime;
    }

    public void setLastInputTime(long lastInputTime) {
        this.lastInputTime = lastInputTime;
    }

    public boolean isForcedAfk() {
        return forcedAfk;
    }

    public long getForcedAfkTime() {
        return forcedAfkTime;
    }

    public Text getForcedAfkSource() {
        return forcedAfkSource;
    }

    public void setForcedAfk(boolean forcedAfk) {
        this.setForcedAfk(forcedAfk, null);
    }

    public void setForcedAfk(boolean forcedAfk, @Nullable Text source) {
        this.forcedAfk = forcedAfk;
        this.forcedAfkSource = source;
        this.forcedAfkTime = Util.getEpochTimeMs();
    }

    public boolean hasAfkTag() {
        return hasAfkTag;
    }

    public void setHasAfkTag(boolean hasAfkTag) {
        this.hasAfkTag = hasAfkTag;
    }

    public boolean isAfk() {
        long timeUntilAfk = (long) AfkMinus.CONFIG_MANAGER.getData().timeUntilAfk;
        // Disabled
        if(timeUntilAfk == -1)
            return false;
        boolean naturallyAfk = Util.getEpochTimeMs() - getLastInputTime() > timeUntilAfk*1000;
        return naturallyAfk || this.isForcedAfk();
    }

    private AfkPlayer(ServerPlayerEntity player) {
        this.player = player;
    }

    @Contract("_ -> new")
    public static @NotNull AfkPlayer fromPlayer(ServerPlayerEntity player) {
        return new AfkPlayer(player);
    }
}
