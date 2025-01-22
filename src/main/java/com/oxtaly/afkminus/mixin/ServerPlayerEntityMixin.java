package com.oxtaly.afkminus.mixin;

import com.oxtaly.afkminus.AfkMinus;
import com.oxtaly.afkminus.AfkPlayer;
import com.oxtaly.afkminus.config.ConfigData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public class  ServerPlayerEntityMixin {

    @Inject(method = "setPlayerInput", at = @At("HEAD"))
    public void onPlayerInput(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if(player == null) {
            AfkMinus.LOGGER.error("[AfkMinus] Nonexistent player in ServerPlayerMixin#onPlayerInput()V!");
            return;
        }
        AfkPlayer afkPlayer = AfkMinus.PLAYER_LIST.get(player.getUuid());
        if(afkPlayer == null) {
            AfkMinus.LOGGER.warn(String.format("[AfkMinus] Player %s was not in player list until input!", player.getName().getString()));
            afkPlayer = AfkMinus.PLAYER_LIST.put(player.getUuid(), AfkPlayer.fromPlayer(player));
        }
        afkPlayer.setLastInputTime(Util.getEpochTimeMs());
        afkPlayer.setForcedAfk(false);
    }

    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/world/ServerWorld;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/packet/c2s/common/SyncedClientOptions;)V", at = @At("RETURN"))
    public void onCreate(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if(player == null) {
            AfkMinus.LOGGER.error("[AfkMinus] Nonexistent player in ServerPlayerMixin#onCreate()V!");
            return;
        }
        AfkPlayer afkPlayer = AfkMinus.PLAYER_LIST.get(player.getUuid());
        if(afkPlayer != null) {
            // Happens when player respawns due to the ServerPlayerEntity being re-created or with carpet shadowed players
            AfkMinus.LOGGER.warn(String.format("[AfkMinus] Player %s was already in player list when created?!", player.getName().getString()));
            AfkMinus.PLAYER_LIST.remove(player.getUuid());
        }
        AfkMinus.PLAYER_LIST.put(player.getUuid(), AfkPlayer.fromPlayer(player));
        ConfigData config = AfkMinus.CONFIG_MANAGER.getData();
        if(config.afkTag != null && !config.afkTag.isEmpty()) {
            Set<String> playerTags = player.getCommandTags();
            if(playerTags.contains(config.afkTag)){
                player.removeCommandTag(config.afkTag);
                AfkMinus.LOGGER.debug(String.format("[AfkMinus] onCreate: Removed afk tag from %s", player.getName().getString()));
            }
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    public void onDisconnect(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if(player == null) {
            AfkMinus.LOGGER.error("[AfkMinus] Nonexistent player in ServerPlayerMixin#onDisconnect()V!");
            return;
        }
        ConfigData config = AfkMinus.CONFIG_MANAGER.getData();
        AfkPlayer afkPlayer = AfkMinus.PLAYER_LIST.get(player.getUuid());
        if(afkPlayer != null) {
            if(afkPlayer.hasAfkTag() && config.afkTag != null && !config.afkTag.isEmpty()) {
                Set<String> playerTags = player.getCommandTags();
                if(playerTags.contains(config.afkTag)) {
                    player.removeCommandTag(config.afkTag);
                    AfkMinus.LOGGER.debug(String.format("[AfkMinus] onDisconnect: Removed afk tag from %s", player.getName().getString()));
                }
            }
            AfkMinus.PLAYER_LIST.remove(player.getUuid());
        }
        else
            AfkMinus.LOGGER.warn(String.format("[AfkMinus] Player %s was missing from player list!", player.getName().getString()));
    }
}
