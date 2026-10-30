package fr.azrotho.aibuildplugin.runnable;

import fr.azrotho.aibuildplugin.AIBuildPlugin;
import fr.azrotho.aibuildplugin.openrouter.ModelsUtility;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class RefreshModelsRunnable extends BukkitRunnable {

    @Override
    public void run() {
        AIBuildPlugin plugin = AIBuildPlugin.getInstance();
        String apiKey = plugin.getOpenRouterApiKey();

        try {
            List<String> models = ModelsUtility.getTextModelNames(apiKey);
            if (models != null && !models.isEmpty()) {
                plugin.setAvailableModels(models);
                plugin.getLogger().info("Successfully loaded " + models.size() + " models from OpenRouter.");
            } else {
                plugin.getLogger().warning("No models loaded from OpenRouter (API response was empty).");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error while refreshing OpenRouter models: " + e.getMessage());
        }
    }
}
