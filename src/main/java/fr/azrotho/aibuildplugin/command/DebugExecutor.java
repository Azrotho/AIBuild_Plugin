package fr.azrotho.aibuildplugin.command;

import com.github.dedinc.oprouter4j.client.OpenRouterClient;
import com.github.dedinc.oprouter4j.model.APIResponse;
import fr.azrotho.aibuildplugin.AIBuildPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DebugExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /debug <chat|help>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "chat" -> handleChatCommand(sender, args);
            case "help" -> handleHelpCommand(sender);
            default -> sender.sendMessage("§cSous-commande inconnue. Faites §e/debug help");
        }

        return true;
    }

    private void handleChatCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /debug chat <model> <message>");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("§cVeuillez spécifier un message à envoyer.");
            sender.sendMessage("§7Usage: /debug chat " + args[1] + " <message>");
            return;
        }

        String rawModel = args[1];
        String model = resolveModelName(rawModel);
        String userMessage = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        String apiKey = AIBuildPlugin.getInstance().getOpenRouterApiKey();
        if (apiKey == null || apiKey.isBlank() || apiKey.equalsIgnoreCase("example_api_key")) {
            sender.sendMessage("§c[AI] Clé d'API OpenRouter non configurée dans config.yml.");
            return;
        }

        sender.sendMessage("§7[AI] Envoi de la requête au modèle §b" + model + "§7...");

        Bukkit.getScheduler().runTaskAsynchronously(AIBuildPlugin.getInstance(), () -> {
            try (OpenRouterClient client = AIBuildPlugin.getInstance().getOpenRouterClient(model)) {
                List<Map<String, String>> messages = List.of(
                    Map.of("role", "user", "content", userMessage)
                );

                APIResponse response = client.chatCompletion(messages);
                if (response.isSuccess() && response.getData() != null) {
                    Object choicesObj = response.getData().get("choices");
                    if (choicesObj instanceof List<?> choices && !choices.isEmpty()) {
                        Object firstChoice = choices.get(0);
                        if (firstChoice instanceof Map<?, ?> choiceMap) {
                            Object messageObj = choiceMap.get("message");
                            if (messageObj instanceof Map<?, ?> messageMap) {
                                String content = (String) messageMap.get("content");
                                if (content != null && !content.isBlank()) {
                                    sender.sendMessage("§8[§bAI §7- §f" + model + "§8]");
                                    for (String line : content.split("\n")) {
                                        sender.sendMessage("§7" + line);
                                    }
                                    return;
                                }
                            }
                        }
                    }
                    sender.sendMessage("§e[AI] Aucune réponse renvoyée par le modèle.");
                } else {
                    String errorMsg = response.getError() != null ? response.getError() : "Erreur inconnue";
                    Integer code = response.getStatusCode();
                    sender.sendMessage("§c[AI] Erreur OpenRouter (" + (code != null ? code : "inconnu") + ") : " + errorMsg);
                }
            } catch (Exception e) {
                sender.sendMessage("§c[AI] Erreur lors de l'appel API : " + e.getMessage());
            }
        });
    }

    private void handleHelpCommand(@NotNull CommandSender sender) {
        sender.sendMessage("§6=== §eAiBuildPlugin Debug Help §6===");
        sender.sendMessage("§e/debug chat <model> <message> §7- Envoie un message à un modèle d'IA");
        sender.sendMessage("§e/debug help §7- Affiche cette aide");
    }

    public static String resolveModelName(String inputModel) {
        if (inputModel == null || inputModel.isBlank()) {
            return inputModel;
        }

        List<String> models = AIBuildPlugin.getInstance().getAvailableModels();
        if (models == null || models.isEmpty()) {
            models = AIBuildPlugin.NOT_WORKING_LIST;
        }

        for (String m : models) {
            if (m.equalsIgnoreCase(inputModel)) {
                return m;
            }
        }

        for (String m : models) {
            if (m.toLowerCase().endsWith("/" + inputModel.toLowerCase())) {
                return m;
            }
        }

        return inputModel;
    }
}
