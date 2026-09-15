package fr.azrotho.aibuildplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class AiBuildPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("AiBuildPlugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("AiBuildPlugin disabled");
    }
}
