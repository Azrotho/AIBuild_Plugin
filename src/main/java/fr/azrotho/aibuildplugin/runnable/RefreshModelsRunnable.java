package fr.azrotho.aibuildplugin.runnable;

import org.bukkit.scheduler.BukkitRunnable;

import fr.azrotho.aibuildplugin.AIBuildPlugin;
import fr.azrotho.aibuildplugin.openrouter.ModelsUtility;

public class RefreshModelsRunnable extends BukkitRunnable {


    @Override
    public void run() {
        AIBuildPlugin plugin = AIBuildPlugin.getInstance();
        String apiKey = plugin.getOpenRouterApiKey();
        AIBuildPlugin.getInstance().setAvailableModels(ModelsUtility.getTextModelNames(apiKey));
    }
    
}
