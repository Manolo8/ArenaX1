package br.com.tinycraft.arenax1;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

/**
 * @author Willian
 */
public final class ArenaConfig {

    private final FileConfiguration config;
    private final int startWaitTime;
    private final int endindTime;
    private final int defaultRemainingTime;
    private final int defaultAcceptedWait;
    private final int defaultExpireTime;
    private final String language;
    private final double _VERSION = 1.2;
    private final int guiItem;

    public ArenaConfig(ArenaX1 plugin) {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
        if (this.config.getDouble("Config.VERSION") != _VERSION) {
            File file = new File(plugin.getDataFolder(), "config.yml");
            file.delete();
            plugin.saveDefaultConfig();
            Bukkit.getLogger().info("---------------------------------");
            Bukkit.getLogger().info("A NEW CONFIG FILE HAS GENERATED!");
            Bukkit.getLogger().info("---------------------------------");
        }

        this.startWaitTime = getInt(5, "Config.START_WAIT_TIME");
        this.endindTime = getInt(15, "Config.ENDING_TIME");
        this.defaultRemainingTime = getInt(500, "Config.DEFAULT_REMAINING_TIME");
        this.defaultAcceptedWait = getInt(5, "Config.DEFAULT_ACCEPTED_WAIT");
        this.defaultExpireTime = getInt(60, "Config.DEFAULT_EXPIRE_TIME");
        this.language = getString("EN-US", "Config.LANGUAGE");
        this.guiItem = getInt(283, "Config.GUI_ITEM");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public int getInt(int defaultValue, String path) {
        if (this.config.contains(path)) {
            return this.config.getInt(path);
        } else {
            return defaultValue;
        }
    }

    public String getString(String path) {
        return this.config.getString(path);
    }

    public String getString(String defaultValue, String path) {
        if (this.config.contains(path)) {
            return this.config.getString(path);
        } else {
            return defaultValue;
        }
    }

    public int getStartWaitTime() {
        return startWaitTime;
    }

    public int getEndindTime() {
        return endindTime;
    }

    public int getDefaultRemainingTime() {
        return defaultRemainingTime;
    }

    public int getDefaultAcceptedWait() {
        return defaultAcceptedWait;
    }

    public int getDefaultExpireTime() {
        return defaultExpireTime;
    }

    public String getLanguage() {
        return language;
    }

    public int getGuiItem() {
        return this.guiItem;
    }
}
