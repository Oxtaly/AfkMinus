package com.oxtaly.afkminus.mixin;

import com.oxtaly.afkminus.AfkMinus;
import com.oxtaly.afkminus.AfkPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
            afkPlayer = AfkMinus.PLAYER_LIST.put(player.getUuid(), AfkPlayer.fromUUID(player.getUuid()));
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
            AfkMinus.LOGGER.warn(String.format("[AfkMinus] Player %s was already in player list when created?!", player.getName().getString()));
            AfkMinus.PLAYER_LIST.remove(player.getUuid());
        }
        AfkMinus.PLAYER_LIST.put(player.getUuid(), AfkPlayer.fromUUID(player.getUuid()));
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    public void onDisconnect(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if(player == null) {
            AfkMinus.LOGGER.error("[AfkMinus] Nonexistent player in ServerPlayerMixin#onDisconnect()V!");
            return;
        }
        AfkPlayer afkPlayer = AfkMinus.PLAYER_LIST.get(player.getUuid());
        if(afkPlayer != null)
            AfkMinus.PLAYER_LIST.remove(player.getUuid());
        else
            AfkMinus.LOGGER.warn(String.format("[AfkMinus] Player %s was missing from player list!", player.getName().getString()));
    }
}
