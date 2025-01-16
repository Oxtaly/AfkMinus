package com.oxtaly.afkminus;

import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AfkPlayer {
    private long lastInputTime = Util.getEpochTimeMs();
    private final UUID uuid;
    private boolean forcedAfk = false;
    private Text forcedAfkSource = null;
    private long forcedAfkTime = 0;

    public UUID getUuid() {
        return uuid;
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


    public boolean isAfk() {
        long timeUntilAfk = (long) AfkMinus.CONFIG_MANAGER.getData().timeUntilAfk;
        // Disabled
        if(timeUntilAfk == -1)
            return false;
        boolean naturallyAfk = Util.getEpochTimeMs() - getLastInputTime() > timeUntilAfk*1000;
        return naturallyAfk || this.isForcedAfk();
    }

    public AfkPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Contract("_ -> new")
    public static @NotNull AfkPlayer fromUUID(UUID uuid) {
        return new AfkPlayer(uuid);
    }
}
