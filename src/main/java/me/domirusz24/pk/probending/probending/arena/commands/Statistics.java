package me.domirusz24.pk.probending.probending.arena.commands;

import me.domirusz24.pk.probending.probending.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Statistics implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getLabel().equalsIgnoreCase("statystyki")) {
            if (args.length > 0) {
                Player p = Bukkit.getServer().getPlayer(args[0]);
                if (p == null) {
                    PlayerData e = new PlayerData(args[0]);
                    if (e.getTotalGames() != 0) {
                        (new PlayerData(args[0])).getInfo().forEach(sender::sendMessage);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player " + args[0] + " jeszcze nie gral na PB!");
                    }
                } else {
                    (new PlayerData(p)).getInfo().forEach(sender::sendMessage);
                }
            } else {
                if (sender instanceof Player) {
                    (new PlayerData((Player) sender)).getInfo().forEach(sender::sendMessage);
                } else {
                    sender.sendMessage("Od kiedy konsola gra w PB?");
                }
            }
        }
        return false;
    }
}
