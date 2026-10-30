package fr.azrotho.aibuildplugin.command;

import fr.azrotho.aibuildplugin.command.tabcomplete.DebugTabCompletor;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DebugCommand implements BasicCommand {

    private final DebugExecutor executor = new DebugExecutor();
    private final DebugTabCompletor tabCompletor = new DebugTabCompletor();

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        executor.onCommand(commandSourceStack.getSender(), null, "debug", args);
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        List<String> suggestions = tabCompletor.onTabComplete(commandSourceStack.getSender(), null, "debug", args);
        return suggestions != null ? suggestions : Collections.emptyList();
    }
}
