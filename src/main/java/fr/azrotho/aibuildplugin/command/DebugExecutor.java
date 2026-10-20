package fr.azrotho.aibuildplugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        // /debug chat <model> <m ess a g e>
        // --> <model> <message>
        if(args.length == 0) {
            sender.sendMessage("nop /debug help");
            return true;
        }
        switch(args[0]) {
            case("chat") -> {

            }
        }

        return true;
    }
}
