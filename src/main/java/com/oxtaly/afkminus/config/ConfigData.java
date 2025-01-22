package com.oxtaly.afkminus.config;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNullElse;

public class ConfigData implements Cloneable {
    public static ConfigData DEFAULT = createDefault();

    @SerializedName("config_version_DO_NOT_TOUCH")
    public String configVersion;

    @SerializedName("afk_placeholder")
    public String afkPlaceholder;
    @SerializedName("time_until_afk")
    public Integer timeUntilAfk;
    @SerializedName("add_tag_on_afk")
    public Boolean addTagOnAfk;
    @SerializedName("afk_tag")
    public String afkTag;

    public static @NotNull ConfigData createDefault() {
        ConfigData configData = new ConfigData();

        configData.configVersion = "1.0.1";
        configData.afkPlaceholder = "<i><dark_gray>[<gray>AFK</gray>]</dark_gray></i> ";
        configData.timeUntilAfk = 300;
        configData.addTagOnAfk = true;
        configData.afkTag = "afkminus.afk";

        return configData;
    }

    public void fillMissing() {
        this.configVersion = requireNonNullElse(this.configVersion, DEFAULT.configVersion);
        this.afkPlaceholder = requireNonNullElse(this.afkPlaceholder, DEFAULT.afkPlaceholder);
        this.timeUntilAfk = requireNonNullElse(this.timeUntilAfk, DEFAULT.timeUntilAfk);
        this.addTagOnAfk = requireNonNullElse(this.addTagOnAfk, DEFAULT.addTagOnAfk);
        this.afkTag = requireNonNullElse(this.afkTag, DEFAULT.afkTag);
    }

    public ConfigData clone() {
        try {
            return (ConfigData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
