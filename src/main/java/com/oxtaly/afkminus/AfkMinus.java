package com.oxtaly.afkminus;

import com.oxtaly.afkminus.commands.ModCommands;
import com.oxtaly.afkminus.config.ConfigManager;
import com.oxtaly.afkminus.utils.Utils;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AfkMinus implements ModInitializer {

	public static Map<UUID, AfkPlayer> PLAYER_LIST = new HashMap<>();

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "afkminus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ConfigManager CONFIG_MANAGER = new ConfigManager();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		try {
			Utils.initConfig();
			Utils.saveConfig();
			ModCommands.init();
			AfkMinusPlaceHolders.register();
			Utils.initEvents();
			LOGGER.info("[AfkMinus] Loaded!");
		} catch (IOException e) {
			LOGGER.error("[AfkMinus] An error happened trying to load config!", e);
		} catch (Exception e) {
			LOGGER.error("[AfkMinus] An error happened trying to load the mod!", e);
		}
	}
}