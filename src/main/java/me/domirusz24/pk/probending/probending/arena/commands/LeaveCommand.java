package me.domirusz24.pk.probending.probending.arena.commands;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.config.winlosecommandsconfig.ConfigEvents;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (Arena.getPlayersSpectating().containsKey(sender)) {
                Arena.getPlayersSpectating().get(sender).removeSpectator((Player) sender);
            } else if (Arena.playersPlaying.contains(sender)) {
                for (Arena a : Arena.Arenas) {
                    if (a.isInGame()) {
                        if (a.getAllPlayers().contains(sender)) {
                            a.killPlayer(a.getPBPlayer((Player) sender));
                            a.removeSpectator((Player) sender);
                            ConfigEvents.PlayerClickLeave.run(a, (Player) sender);
                        }
                    }
                }

            } else {
                sender.sendMessage(ProBending.errorPrefix + "Nie ogladasz, ani nie grasz!");
            }
        }
        return false;
    }
}
