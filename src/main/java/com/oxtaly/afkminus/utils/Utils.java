package com.oxtaly.afkminus.utils;

import com.oxtaly.afkminus.AfkMinus;
import com.oxtaly.afkminus.config.ConfigManager;
import com.oxtaly.afkminus.listeners.TickEventListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Utils {

    public static @NotNull String getConfigPath() {
        return FabricLoader.getInstance().getConfigDir() + "/AfkMinus.json";
    }

    public static class minecraftLogBuilder {
        public static @NotNull MutableText log(String string) {
            return minecraftLogBuilder.log(Text.literal(string));
        };
        public static @NotNull MutableText log(MutableText mutableText) {
            MutableText logString = Text.empty()
                    .append(Text.literal("[").formatted(Formatting.DARK_GRAY))
                    .append(Text.literal("AfkMinus").setStyle(Style.EMPTY.withColor(TextColor.parse("#F9DBDB").getOrThrow())
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("AfkMinus")))))
                    .append(Text.literal("] ").formatted(Formatting.DARK_GRAY));
            logString.append(mutableText);
            return logString;
        };
        public static @NotNull Text warn(String string) {
            return minecraftLogBuilder.warn(Text.literal(string));
        };
        public static @NotNull MutableText warn(MutableText mutableText) {
            return minecraftLogBuilder.log(mutableText).setStyle(Style.EMPTY.withColor(TextColor.parse("#ffc107").getOrThrow()));
        };
        public static @NotNull MutableText error(String string) {
            return minecraftLogBuilder.error(Text.literal(string));
        };
        public static @NotNull MutableText error(MutableText mutableText) {
            return minecraftLogBuilder.log(mutableText).formatted(Formatting.RED);
        };
    }

    public static int initConfig() throws IOException {
        ConfigManager config = AfkMinus.CONFIG_MANAGER;
        File configFile = new File(getConfigPath());
        if(!configFile.exists()) {
            config.writeConfig(configFile);
            return -1;
        }
        return config.readConfig(configFile);
    }

    public static void saveConfig() throws IOException {
        ConfigManager config = AfkMinus.CONFIG_MANAGER;
        File configFile = new File(getConfigPath());
        config.writeConfig(configFile);
    }

    public static void initEvents() {
        TickEventListener.register();
    }

    public static String msToHighest2(long ms) {
        long rest = ms;

        int milliseconds = (int) (ms % 1000);
        rest = (rest - milliseconds) / 1000;

        int seconds = (int) (rest % 60);
        rest = (rest - seconds) / 60;

        int minutes = (int) (rest % 60);
        rest = (rest - minutes) / 60;

        int hours = (int) (rest % 24);
        rest = (rest - hours) / 24;

        int days = (int) rest;

        if(days != 0)
            return String.format("%sd" + "%02dh", days, hours);
        if(hours != 0)
            return String.format("%sh" + "%02dm", hours, minutes);
        if(minutes != 0)
            return String.format("%sm" + "%02ds", minutes, seconds);
        if(seconds != 0)
            return String.format("%s.%03ds", seconds, milliseconds);
        return milliseconds + "ms";
    }
}
