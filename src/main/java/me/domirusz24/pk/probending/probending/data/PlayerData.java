package me.domirusz24.pk.probending.probending.data;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.config.ConfigManager;
import me.domirusz24.pk.probending.probending.data.datatype.PlayerDataType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {

    private final String name;
    private final String path;

    public PlayerData(Player player) {
        this.name = player.getName();
        path = name + ".";
    }

    public PlayerData(String name) {
        this.name = name;
        path = name + ".";
    }

    public void setData(PlayerDataType data, int value) {
        ConfigManager.getWinLoseCommands().getConfig().set(path + data.getName(), value);
    }

    public void raiseData(PlayerDataType data, int value) {
        ConfigManager.getWinLoseCommands().reloadConfig();
        int previous = getData(data);
        previous+= value;
        ConfigManager.getWinLoseCommands().getConfig().set(path + data.getName(), previous);
        ConfigManager.getWinLoseCommands().saveConfig();
    }

    public int getData(PlayerDataType data) {
        ConfigManager.getWinLoseCommands().reloadConfig();
        return ConfigManager.getWinLoseCommands().getConfig().getInt(path + data.getName(), 0);
    }

    public int getTotalGames() {
        return getData(PlayerDataType.PlayerTie) + getData(PlayerDataType.PlayerLoss) + getData(PlayerDataType.PlayerWins);
    }

    public double getRatio() {
        int loss = getData(PlayerDataType.PlayerLoss);
        int win = getData(PlayerDataType.PlayerWins);
        if (loss == 0) {
            return win;
        }
        if (win == 0) {
            return 0;
        }
        double ratio = (double) win / loss;
        ratio = ratio * 100;
        ratio = Math.round(ratio);
        return ratio / 100;
    }

    public List<String> getInfo() {
        List<String> l = new ArrayList<>();
        l.add(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "---------------------------------------------");
        l.add( ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ChatColor.GREEN + "" + ChatColor.BOLD + "Statystyki" + ChatColor.GRAY + "" + ChatColor.BOLD + "]");
        l.add(ProBending.arrow + ChatColor.BLUE + "" + ChatColor.BOLD + "Nick: " + ChatColor.RESET + "" + ChatColor.GRAY + name);
        for (PlayerDataType e : PlayerDataType.values()) {
            l.add(e.getPolishName() + getData(e));
        }
        l.add(ProBending.arrow + ChatColor.BLUE + "" + ChatColor.BOLD + "Liczba gier: " + ChatColor.RESET + "" + ChatColor.GRAY + getTotalGames());
        l.add(ProBending.arrow + ChatColor.BLUE + "" + ChatColor.BOLD + "WLR: " + ChatColor.RESET + "" + ChatColor.GRAY + getRatio());
        l.add(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "---------------------------------------------");
        return l;
    }
}
