package fr.azrotho.aibuildplugin.command.tabcomplete;

import fr.azrotho.aibuildplugin.AIBuildPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DebugTabCompletor implements TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("chat", "help");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> response = new ArrayList<>();

        if (args.length == 0) {
            return SUBCOMMANDS;
        }

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], SUBCOMMANDS, response);
            Collections.sort(response);
            return response;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("chat")) {
            List<String> availableModels = AIBuildPlugin.getInstance().getAvailableModels();
            if (availableModels == null || availableModels.isEmpty()) {
                availableModels = AIBuildPlugin.NOT_WORKING_LIST;
            }

            String currentArg = args[1].toLowerCase();

            // Génère une liste de candidats avec :
            // 1) Le nom complet du modèle (ex: "deepseek/deepseek-chat")
            // 2) Le nom court sans le préfixe fournisseur (ex: "deepseek-chat", "gpt-4o", "claude-3.5-sonnet")
            Set<String> candidates = new HashSet<>();
            for (String model : availableModels) {
                candidates.add(model);
                int slashIndex = model.indexOf('/');
                if (slashIndex != -1 && slashIndex < model.length() - 1) {
                    candidates.add(model.substring(slashIndex + 1));
                }
            }

            // 1. Filtrage par préfixe direct (compatible avec le filtrage client Brigadier de Minecraft)
            for (String candidate : candidates) {
                if (candidate.toLowerCase().startsWith(currentArg)) {
                    response.add(candidate);
                }
            }

            // 2. Si aucun match par préfixe, recherche par sous-chaîne
            if (response.isEmpty() && !currentArg.isBlank()) {
                for (String candidate : candidates) {
                    if (candidate.toLowerCase().contains(currentArg)) {
                        response.add(candidate);
                    }
                }
            }

            Collections.sort(response);
            return response;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("chat")) {
            return List.of("<message>");
        }

        return Collections.emptyList();
    }
}
