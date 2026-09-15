package fr.azrotho.aibuildplugin.utils;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigUtility {


    public void saveDefaultConfigIfNotExists(JavaPlugin plugin) {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
    }

    public void reloadConfig(JavaPlugin plugin) {
        plugin.reloadConfig();
    }
    
}
