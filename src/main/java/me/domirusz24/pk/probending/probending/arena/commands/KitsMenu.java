package me.domirusz24.pk.probending.probending.arena.commands;

import me.domirusz24.pk.probending.probending.misc.customguis.KitGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitsMenu implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getLabel().equalsIgnoreCase("kity")) {
            if (sender instanceof Player) {
                new KitGUI((Player) sender);
            }
        }
        return false;
    }
}
