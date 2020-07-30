package me.domirusz24.pk.probending.probending.data;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.config.ConfigManager;
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

    public void setData(PlayerDataEnum data, int value) {
        ConfigManager.getWinLoseCommands().getConfig().set(path + data.getName(), value);
    }

    public void raiseData(PlayerDataEnum data, int value) {
        ConfigManager.getWinLoseCommands().reloadConfig();
        int previous = getData(data);
        previous+= value;
        ConfigManager.getWinLoseCommands().getConfig().set(path + data.getName(), previous);
        ConfigManager.getWinLoseCommands().saveConfig();
    }

    public int getData(PlayerDataEnum data) {
        ConfigManager.getWinLoseCommands().reloadConfig();
        return ConfigManager.getWinLoseCommands().getConfig().getInt(path + data.getName(), 0);
    }

    public int getTotalGames() {
        return getData(PlayerDataEnum.PlayerTie) + getData(PlayerDataEnum.PlayerLoss) + getData(PlayerDataEnum.PlayerWins);
    }

    public double getRatio() {
        int loss = getData(PlayerDataEnum.PlayerLoss);
        int win = getData(PlayerDataEnum.PlayerWins);
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
        for (PlayerDataEnum e : PlayerDataEnum.values()) {
            l.add(e.getPolishName() + getData(e));
        }
        l.add(ProBending.arrow + ChatColor.BLUE + "" + ChatColor.BOLD + "Liczba gier: " + ChatColor.RESET + "" + ChatColor.GRAY + getTotalGames());
        l.add(ProBending.arrow + ChatColor.BLUE + "" + ChatColor.BOLD + "WLR: " + ChatColor.RESET + "" + ChatColor.GRAY + getRatio());
        l.add(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "---------------------------------------------");
        return l;
    }
}
