package fr.azrotho.aibuildplugin;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.dedinc.oprouter4j.client.OpenRouterClient;

import fr.azrotho.aibuildplugin.openrouter.ModelsUtility;
import fr.azrotho.aibuildplugin.runnable.RefreshModelsRunnable;
import fr.azrotho.aibuildplugin.utils.ConfigUtility;

public final class AIBuildPlugin extends JavaPlugin {

    private static AIBuildPlugin instance;
    private String openRouterApiKey;
    private List<String> availableModels;

    @Override
    public void onEnable() {
        instance = this;
        openRouterApiKey = getConfig().getString("openrouter.api_key");

        ConfigUtility configUtility = new ConfigUtility();
        configUtility.saveDefaultConfigIfNotExists(this);
        configUtility.reloadConfig(this);

        RefreshModelsRunnable refreshModelsRunnable = new RefreshModelsRunnable();
        refreshModelsRunnable.runTaskTimer(this, 0L, 20L * 5 * 60); // Refresh every 5 minutes

        getLogger().info("AiBuildPlugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("AiBuildPlugin disabled");
    }

    public static AIBuildPlugin getInstance() {
        return instance;
    }

    public OpenRouterClient getOpenRouterClient(String modelName) {
        return new OpenRouterClient(openRouterApiKey, modelName);
    }

    public String getOpenRouterApiKey() {
        return openRouterApiKey;
    }

    public List<String> getAvailableModels() {
        return availableModels;
    }

    public void setAvailableModels(List<String> availableModels) {
        this.availableModels = availableModels;
    }
}
