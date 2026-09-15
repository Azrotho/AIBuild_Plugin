package fr.azrotho.aibuildplugin;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.dedinc.oprouter4j.client.OpenRouterClient;

import fr.azrotho.aibuildplugin.openrouter.ModelsUtility;
import fr.azrotho.aibuildplugin.utils.ConfigUtility;

public final class AiBuildPlugin extends JavaPlugin {

    private static AiBuildPlugin instance;
    private String openRouterApiKey;

    @Override
    public void onEnable() {
        instance = this;
        openRouterApiKey = getConfig().getString("openrouter.api_key");

        ConfigUtility configUtility = new ConfigUtility();
        configUtility.saveDefaultConfigIfNotExists(this);

        getLogger().info("AiBuildPlugin enabled");

        getLogger().info("Models:" + ModelsUtility.getTextModelNames(openRouterApiKey).toString());
    }

    @Override
    public void onDisable() {
        getLogger().info("AiBuildPlugin disabled");
    }

    public static AiBuildPlugin getInstance() {
        return instance;
    }

    public OpenRouterClient getOpenRouterClient(String modelName) {
        return new OpenRouterClient(openRouterApiKey, modelName);
    }

    public String getOpenRouterApiKey() {
        return openRouterApiKey;
    }
}
