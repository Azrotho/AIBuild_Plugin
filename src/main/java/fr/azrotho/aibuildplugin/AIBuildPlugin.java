package fr.azrotho.aibuildplugin;

import com.github.dedinc.oprouter4j.client.OpenRouterClient;
import fr.azrotho.aibuildplugin.command.DebugCommand;
import fr.azrotho.aibuildplugin.runnable.RefreshModelsRunnable;
import fr.azrotho.aibuildplugin.utils.ConfigUtility;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AIBuildPlugin extends JavaPlugin {

    public static final List<String> NOT_WORKING_LIST = List.of(
            "no/model"
    );

    private static AIBuildPlugin instance;
    private String openRouterApiKey;
    private List<String> availableModels = Collections.synchronizedList(new ArrayList<>(NOT_WORKING_LIST));

    @Override
    public void onEnable() {
        instance = this;

        ConfigUtility configUtility = new ConfigUtility();
        configUtility.saveDefaultConfigIfNotExists(this);
        configUtility.reloadConfig(this);

        this.openRouterApiKey = getOpenRouterApiKey();

        // Register modern Paper command
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register("debug", "Debug command for AI Build Plugin", new DebugCommand());
        });

        RefreshModelsRunnable refreshModelsRunnable = new RefreshModelsRunnable();
        refreshModelsRunnable.runTaskTimerAsynchronously(this, 0L, 20L * 5 * 60); // Refresh every 5 minutes

        getLogger().info("AiBuildPlugin enabled with " + availableModels.size() + " default models loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AiBuildPlugin disabled");
    }

    public static AIBuildPlugin getInstance() {
        return instance;
    }

    public OpenRouterClient getOpenRouterClient(String modelName) {
        return new OpenRouterClient(getOpenRouterApiKey(), modelName);
    }

    public String getOpenRouterApiKey() {
        // 1. Check root OPENROUTER_API_KEY (from user config)
        String rootKey = getConfig().getString("OPENROUTER_API_KEY");
        if (rootKey != null && !rootKey.isBlank() && !rootKey.equalsIgnoreCase("example_api_key")) {
            this.openRouterApiKey = rootKey;
            return rootKey;
        }

        // 2. Check nested openrouter.api_key
        String nestedKey = getConfig().getString("openrouter.api_key");
        if (nestedKey != null && !nestedKey.isBlank() && !nestedKey.equalsIgnoreCase("example_api_key")) {
            this.openRouterApiKey = nestedKey;
            return nestedKey;
        }

        // 3. Check environment variable
        String envKey = System.getenv("OPENROUTER_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            this.openRouterApiKey = envKey;
            return envKey;
        }

        return rootKey != null ? rootKey : (nestedKey != null ? nestedKey : this.openRouterApiKey);
    }

    public List<String> getAvailableModels() {
        return availableModels;
    }

    public void setAvailableModels(List<String> availableModels) {
        this.availableModels = Collections.synchronizedList(new ArrayList<>(availableModels));
    }
}
