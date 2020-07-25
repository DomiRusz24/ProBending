package me.domirusz24.pk.probending.probending.data.DataType;

import me.domirusz24.pk.probending.probending.ProBending;
import org.bukkit.ChatColor;

public enum PlayerDataType {
    PlayerDeaths("PlayerDeaths", ChatColor.BLUE + "" + ChatColor.BOLD + "Smierci: " + ChatColor.RESET + ""),
    PlayerKills("PlayerKills", ChatColor.BLUE + "" + ChatColor.BOLD + "Zabojstwa: " + ChatColor.RESET + ""),
    PlayerWins("PlayerWins", ChatColor.BLUE + "" + ChatColor.BOLD + "Wygrane rundy: " + ChatColor.RESET + ""),
    PlayerLoss("PlayerLoss", ChatColor.BLUE + "" + ChatColor.BOLD + "Przegrane rundy: " + ChatColor.RESET + ""),
    WonTieBreakerRounds("WonTieBreakerRounds", ChatColor.BLUE + "" + ChatColor.BOLD + "Wygrane TieBreakery: " + ChatColor.RESET + ""),
    WinStreak("WinStreak", ChatColor.BLUE + "" + ChatColor.BOLD + "Seria wygranych: " + ChatColor.RESET + ""),
    PlayerTie("PlayerTie", ChatColor.BLUE + "" + ChatColor.BOLD + "Remisy: " + ChatColor.RESET + "");

    private final String name;
    private final String polishname;

    PlayerDataType(String name, String polish) {
        this.name = name;
        this.polishname = polish;
    }

    public String getName() {
        return name;
    }

    public String getPolishName() {
        return ProBending.arrow + polishname + ChatColor.GRAY + "";
    }
}
