package me.domirusz24.pk.probending.probending.arena.commands;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import me.domirusz24.pk.probending.probending.arena.*;
import java.util.*;

public class ArenaCreateCompleter implements TabCompleter
{
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("arena")) {
            final List<String> complete = new ArrayList<>();
            if (args.length == 1) {
                if (sender.hasPermission("probending.arena.create") || sender.isOp()) {
                    complete.add("create");
                }
                if (sender.hasPermission("probending.arena.config") || sender.isOp()) {
                    complete.add("setspawn");
                    complete.add("teleportspawn");
                    for (final Arena arena : Arena.Arenas) {
                        complete.add(String.valueOf(arena.getID()));
                    }
                }
            }else
            if (args.length == 2) {
                if (sender.hasPermission("probending.arena.config") || sender.isOp()) {
                    for (final StageEnum e : StageEnum.values()) {
                        complete.add(e.toString());
                    }
                    complete.add("setTBStage");
                    complete.add("getTBStage");
                    complete.add("setRollBack");
                    complete.add("getRollBack");
                }
            }else
            if (args.length == 3) {
                if (sender.hasPermission("probending.arena.config") || sender.isOp()) {
                    if (!args[1].equalsIgnoreCase("setRollBack") || !args[1].equalsIgnoreCase("getRollBack")) {
                        if (args[1].equalsIgnoreCase("setTBStage") || args[1].equalsIgnoreCase("getTBStage")) {
                            complete.add("1 - 10");
                        } else {
                            complete.add("player1");
                            complete.add("player2");
                            complete.add("player3");
                            complete.add("center");
                        }
                    }
                }
            }else
            if (args.length == 4) {
                if (sender.hasPermission("probending.arena.config") || sender.isOp()) {
                    complete.add("teleport");
                }
            }
            return complete;
        }
        return null;
    }
}
