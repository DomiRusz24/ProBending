package me.domirusz24.pk.probending.probending.pbgroup;

import me.domirusz24.pk.probending.probending.arena.team.TempTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PBGroup extends TempTeam {
    public static HashMap<Player, PBGroup> PBGroups = new HashMap<>();

    private TempTeam tempTeam;

    private Player captain;

    private String name;

    public PBGroup(Player player, String name) {
        tempTeam = new TempTeam(player);
        this.name = name;
        captain = player;
    }

    @Override
    public void addPlayer(Player player) {
        if (PBGroups.containsKey(player)) {
            captain.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Player " + player.getName() + " juz jest w druzynie!");
        } else {
            tempTeam.addPlayer(player);
        }
    }

    @Override
    public void removeAllPlayers() {
    }

    public void disbandGroup(Player player) {
        if (player.equals(captain)) {
            for (Player p : this.getAllPlayers()) {
                p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Druzyna " + name + " zostala usunieta!");
            }
            removeAllPlayers();
        }
    }

    @Override
    public void removePlayer(Player player) {
        if (this.tempTeam.getAllPlayers().contains(player)) {
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Zostales usuniety z druzyny!");
            super.removePlayer(player);
        } else {
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie jestes w tej druzynie!");
        }
    }

    public void removePlayer(Player player, Player captain) {
        if (this.tempTeam.getAllPlayers().contains(player)) {
            captain.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Usunieto gracza " + player.getName() + " z druzyny!");
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Zostales usuniety z druzyny!");
            super.removePlayer(player);
        } else {
            captain.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ten gracz nie jest w twojej druzynie!");
        }
    }

    public String getName() {
        return name;
    }
}
