package com.oxtaly.afkminus.commands;

import com.google.gson.JsonIOException;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.oxtaly.afkminus.AfkPlayer;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import com.oxtaly.afkminus.AfkMinus;
import com.oxtaly.afkminus.config.ConfigData;
import com.oxtaly.afkminus.utils.Utils;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;


public final class ModCommands {
    private ModCommands() {}

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(
                CommandManager.literal("afkminus")
                    .requires(source -> Permissions.check(source, "afkminus.command.afkminus.base", 2))
                    .then(CommandManager.literal("reload")
                            .requires(source -> Permissions.check(source, "afkminus.command.afkminus.reload", 4))
                            .executes(ctx -> reloadConfig(ctx.getSource()))
                    )
                    .then(CommandManager.literal("force")
                            .requires(source -> Permissions.check(source, "afkminus.command.afkminus.force", 2))
                            .executes(ctx -> {
                                ServerPlayerEntity player = ctx.getSource().getPlayer();
                                if(player == null) {
                                    ctx.getSource().sendError(Utils.minecraftLogBuilder.error("You cannot execute this command as a non player without providing players!"));
                                    return 0;
                                }
                                return forceAfk(ctx.getSource(), List.of(player));
                            })
                            .then(CommandManager.argument("players", EntityArgumentType.players())
                                    .executes(ctx -> forceAfk(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "players")))
                            )
                    )
                    .then(CommandManager.literal("reset")
                            .requires(source -> Permissions.check(source, "afkminus.command.afkminus.reset", 2))
                            .executes(ctx -> {
                                ServerPlayerEntity player = ctx.getSource().getPlayer();
                                if(player == null) {
                                    ctx.getSource().sendError(Utils.minecraftLogBuilder.error("You cannot execute this command as a non player without providing players!"));
                                    return 0;
                                }
                                return resetAfk(ctx.getSource(), List.of(player));
                            })
                            .then(CommandManager.argument("players", EntityArgumentType.players())
                                    .executes(ctx -> resetAfk(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "players")))
                            )
                    )
                    .then(CommandManager.literal("getstatus")
                            .requires(source -> Permissions.check(source, "afkminus.command.afkminus.getstatus", 2))
                            .executes(ctx -> {
                                ServerPlayerEntity player = ctx.getSource().getPlayer();
                                if(player == null) {
                                    ctx.getSource().sendError(Utils.minecraftLogBuilder.error("You cannot execute this command as a non player without providing a player!"));
                                    return 0;
                                }
                                return getAfkStatus(ctx.getSource(), player);
                            })
                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                    .executes(ctx -> getAfkStatus(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player")))
                            )
                    )
                    .then(CommandManager.literal("set")
                            .requires(source -> Permissions.check(source, "afkminus.command.afkminus.set", 4))
                            .then(CommandManager.literal("time_until_afk")
                                    .requires(source -> Permissions.check(source, "afkminus.command.afkminus.set.time_until_afk", 4))
                                    .then(CommandManager.argument("value", IntegerArgumentType.integer(-1, 2147483647))
                                            .suggests((ctx, builder) -> {
                                                builder.suggest(-1);
                                                builder.suggest(ConfigData.DEFAULT.timeUntilAfk);
                                                builder.suggest(AfkMinus.CONFIG_MANAGER.getData().timeUntilAfk);
                                                return builder.buildFuture();
                                            })
                                            .executes(ctx -> setTimeUntilAfk(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "value")))
                                    )
                            )
                            .then(CommandManager.literal("afk_placeholder")
                                    .requires(source -> Permissions.check(source, "afkminus.command.afkminus.set.afk_placeholder", 4))
                                    .then(CommandManager.argument("value", StringArgumentType.string())
                                            .suggests((ctx, builder) -> {
                                                builder.suggest("\"" + ConfigData.DEFAULT.afkPlaceholder + "\"");
                                                builder.suggest("\"" + AfkMinus.CONFIG_MANAGER.getData().afkPlaceholder + "\"");
                                                return builder.buildFuture();
                                            })
                                            .executes(ctx -> setAfkPlaceholder(ctx.getSource(), StringArgumentType.getString(ctx, "value")))
                                    )
                            )
                    )
            );
        });
    }
    public static int reloadConfig(ServerCommandSource source) {
        try {
            ConfigData oldConfigData = AfkMinus.CONFIG_MANAGER.getData().clone();
            int returnCode = Utils.innitConfig();
            if(returnCode > 0) {
                AfkMinus.CONFIG_MANAGER.setData(oldConfigData);
                source.sendError(Utils.minecraftLogBuilder.error("An error happened trying reload config!"));
                return 0;
            }
            if(returnCode == -1) {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.warn("Missing config, created config successfully!"), true);
                AfkMinus.LOGGER.warn("[AfkMinus] Missing config, created config successfully!");
                return 1;
            }
            source.sendFeedback(() -> Utils.minecraftLogBuilder.log("Reloaded config successfully!"), true);
            AfkMinus.LOGGER.info("[AfkMinus] Reloaded config successfully!");
            return 1;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened trying reload config!"));
            AfkMinus.LOGGER.error("[AfkMinus] An error happened trying reload config!", e);
            return 0;
        }
    }

    public static int setAfkPlaceholder(ServerCommandSource source, @NotNull String value) {
        try {
            AfkMinus.CONFIG_MANAGER.getData().afkPlaceholder = value;
            Utils.saveConfig();
            source.sendFeedback(() -> Utils.minecraftLogBuilder.log("Set new afk_placeholder to ").append(value), true);
            return 1;
        } catch (JsonIOException e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened converting config to json! Check the console for more details."));
            AfkMinus.LOGGER.error("An error converting config to json in setAfkPlaceholder command!", e);
            return 0;
        } catch (IOException e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened saving the config file! Check the console for more details."));
            AfkMinus.LOGGER.error("An error saving config file in setAfkPlaceholder command!", e);
            return 0;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened running the command! Check the console for more details."));
            AfkMinus.LOGGER.error("An error running setAfkPlaceholder command!", e);
            return 0;
        }
    }

    public static int setTimeUntilAfk(ServerCommandSource source, @NotNull Integer value) {
        try {
            AfkMinus.CONFIG_MANAGER.getData().timeUntilAfk = value;
            Utils.saveConfig();
            source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Set new time_until_afk to %s", value)), true);
            return 1;
        } catch (JsonIOException e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened converting config to json! Check the console for more details."));
            AfkMinus.LOGGER.error("An error converting config to json in setTimeUntilAfk command!", e);
            return 0;
        } catch (IOException e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened saving the config file! Check the console for more details."));
            AfkMinus.LOGGER.error("An error saving config file in setTimeUntilAfk command!", e);
            return 0;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened running the command! Check the console for more details."));
            AfkMinus.LOGGER.error("An error running setTimeUntilAfk command!", e);
            return 0;
        }
    }

    public static int forceAfk(ServerCommandSource source, @NotNull final Collection<ServerPlayerEntity> targets) {
        try {
            if(targets.isEmpty()) {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.warn("Nothing changed, 0 players provided."), false);
                return 0;
            }
            Text sourceText = source.getDisplayName();
            for (ServerPlayerEntity target : targets) {
                try {
                    AfkPlayer afkPlayer = AfkMinus.PLAYER_LIST.get(target.getUuid());
                    if(afkPlayer == null) {
                        source.sendError(
                            Utils.minecraftLogBuilder.error("Missing afkPlayer for player ")
                                .append(target.getDisplayName())
                                .append("! Ignore if player is a carpet shadowed player.")
                        );
                        AfkMinus.LOGGER.error(String.format("Missing afkPlayer for player %s! May be shadowed player.", target.getName().getString()));
                        return 0;
                    }
                    afkPlayer.setForcedAfk(true, sourceText);
                } catch (Exception e) {
                    AfkMinus.LOGGER.error(String.format("An error happened trying to force player [%s] afk!", target.getName().getString()), e);
                    source.sendError(
                        Utils.minecraftLogBuilder.error("An error happened trying to reset player ")
                            .append(target.getDisplayName())
                            .append("'s afk! Check console for more details.")
                    );
                    return 0;
                }
            }
            if(targets.size() == 1) {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.log("Forced ").append(targets.iterator().next().getDisplayName()).append(" afk!"), true);
            } else {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Forced %s players afk!", targets.size())), true);
            }
            return 1;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to force player(s) afk! Check the console for more details."));
            AfkMinus.LOGGER.error("An error running forceAfk command!", e);
            return 0;
        }
    }

    public static int resetAfk(ServerCommandSource source, @NotNull final Collection<ServerPlayerEntity> targets) {
        try {
            if(targets.isEmpty()) {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.warn("Nothing changed, 0 players provided."), false);
                return 0;
            }
            for (ServerPlayerEntity target : targets) {
                try {
                    AfkPlayer afkPlayer = AfkMinus.PLAYER_LIST.get(target.getUuid());
                    if(afkPlayer == null) {
                        source.sendError(
                            Utils.minecraftLogBuilder.error("Missing afkPlayer for player ")
                                .append(target.getDisplayName())
                                .append("! Ignore if player is a carpet shadowed player.")
                        );
                        AfkMinus.LOGGER.error(String.format("Missing afkPlayer for player %s! May be shadowed player.", target.getName().getString()));
                        return 0;
                    }
                    afkPlayer.setLastInputTime(Util.getEpochTimeMs());
                    afkPlayer.setForcedAfk(false);
                } catch (Exception e) {
                    AfkMinus.LOGGER.error(String.format("An error happened trying to reset player [%s]'s afk!", target.getName().getString()), e);
                    source.sendError(
                        Utils.minecraftLogBuilder.error("An error happened trying to reset player ")
                            .append(target.getDisplayName())
                            .append("'s afk! Check console for more details.")
                    );
                    return 0;
                }
            }
            if(targets.size() == 1) {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.log("Reset ").append(targets.iterator().next().getDisplayName()).append("'s afk status!"), true);
            } else {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Reset %s players' afk status!", targets.size())), true);
            }
            return 1;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to reset player(s)' afk status! Check the console for more details."));
            AfkMinus.LOGGER.error("An error running resetAfk command!", e);
            return 0;
        }
    }

    //TODO: format the time instead of giving it in ms
    public static int getAfkStatus(ServerCommandSource source, @NotNull final ServerPlayerEntity target) {
        try {
            if(target == null) {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.warn("No players provided."), false);
                return 0;
            }
            AfkPlayer afkPlayer = AfkMinus.PLAYER_LIST.get(target.getUuid());
            if(afkPlayer == null) {
                source.sendError(
                    Utils.minecraftLogBuilder.error("Missing afkPlayer for player ")
                        .append(target.getDisplayName())
                        .append("! Ignore if player is a carpet shadowed player.")
                );
                AfkMinus.LOGGER.error(String.format("Missing afkPlayer for player %s! May be shadowed player.", target.getName().getString()));
                return 0;
            }
            if(!afkPlayer.isAfk()) {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.log("").append(target.getDisplayName()).append(" is not afk."), false);
                return 0;
            }
            if(afkPlayer.isForcedAfk()) {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.log("")
                    .append(target.getDisplayName())
                    .append(" has been forced afk by ")
                    .append(afkPlayer.getForcedAfkSource())
                    .append(String.format(" for %sms", Util.getEpochTimeMs() - afkPlayer.getForcedAfkTime())),
                false);
                return 1;
            }
            source.sendFeedback(() -> Utils.minecraftLogBuilder.log("")
                .append(target.getDisplayName())
                .append(String.format(" has been afk for %sms", Util.getEpochTimeMs() - afkPlayer.getLastInputTime() - AfkMinus.CONFIG_MANAGER.getData().timeUntilAfk)),
            false);
            return 1;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to get player(s)' afk status! Check the console for more details."));
            AfkMinus.LOGGER.error("An error running resetAfk command!", e);
            return 0;
        }
    }
}
