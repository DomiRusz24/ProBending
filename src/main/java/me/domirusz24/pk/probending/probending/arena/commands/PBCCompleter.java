package me.domirusz24.pk.probending.probending.arena.commands;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PBCCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            if (command.getName().equalsIgnoreCase("pbc")) {
                List<String> complete = new ArrayList<>();
                if (args.length == 0) {
                    for (Arena arena : Arena.Arenas) {
                        complete.add(String.valueOf(arena.getID()));
                    }
                }
                if (args.length == 1) {
                    complete.add("stop");
                    complete.add("add");
                    complete.add("remove");
                    complete.add("stop");
                }
                if (args.length == 2) {
                    for(Player player : ProBending.plugin.getServer().getOnlinePlayers()) {
                        if (!Arena.playersPlaying.contains(player)) {
                            complete.add(player.getName());
                        }
                    }
                }
                if (args.length == 3) {
                    complete.add("blue");
                    complete.add("red");
                }

                return complete;
            }

        }

        return null;
    }
}
