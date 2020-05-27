package me.domirusz24.pk.probending.probending.arena.temp;

import me.domirusz24.pk.probending.probending.ProBending;
import org.bukkit.entity.*;
import java.util.*;

public class TempTeam
{
    public static HashMap<String, TempTeam> playersWaiting = new HashMap<>();
    private String player1;
    private String player2;
    private String player3;
    
    public TempTeam() {
        this.player1 = null;
        this.player2 = null;
        this.player3 = null;
    }
    
    public TempTeam(final Player player1) {
        this.player1 = null;
        this.player2 = null;
        this.player3 = null;
        this.addPlayer(player1);
    }
    
    public TempTeam(final Player player1, final Player player2) {
        this.player1 = null;
        this.player2 = null;
        this.player3 = null;
        this.addPlayer(player1);
        this.addPlayer(player2);
    }
    
    public TempTeam(final Player player1, final Player player2, final Player player3) {
        this.player1 = null;
        this.player2 = null;
        this.player3 = null;
        this.addPlayer(player1);
        this.addPlayer(player2);
        this.addPlayer(player3);
    }
    
    public void addPlayer(final Player player) {
        if (this.getAllPlayers().contains(player)) {
            return;
        }
        if (TempTeam.playersWaiting.containsKey(player.getName())) {
            TempTeam.playersWaiting.get(player.getName()).removePlayer(player);
        }
        if (this.player1 == null) {
            this.player1 = player.getName();
            TempTeam.playersWaiting.put(player.getName(), this);
            return;
        }
        if (this.player2 == null) {
            this.player2 = player.getName();
            TempTeam.playersWaiting.put(player.getName(), this);
            return;
        }
        if (this.player3 == null) {
            this.player3 = player.getName();
            TempTeam.playersWaiting.put(player.getName(), this);
        }
    }
    
    public void removePlayer(final Player player) {
        if (!this.getAllPlayers().contains(player)) {
            return;
        }
        if (this.player1.equals(player.getName())) {
            this.player1 = null;
            TempTeam.playersWaiting.remove(player.getName());
            return;
        }
        if (this.player2.equals(player.getName())) {
            this.player2 = null;
            TempTeam.playersWaiting.remove(player.getName());
            return;
        }
        if (this.player3.equals(player.getName())) {
            this.player3 = null;
            TempTeam.playersWaiting.remove(player.getName());
        }
    }

    public void removePlayer(final String name) {
        if (this.player1.equals(name)) {
            this.player1 = null;
            TempTeam.playersWaiting.remove(name);
            return;
        }
        if (this.player2.equals(name)) {
            this.player2 = null;
            TempTeam.playersWaiting.remove(name);
            return;
        }
        if (this.player3.equals(name)) {
            this.player3 = null;
            TempTeam.playersWaiting.remove(name);
        }
    }
    
    public void removeAllPlayers() {
        this.getAllPlayers().forEach(this::removePlayer);
    }
    
    public List<Player> getAllPlayers() {
        final List<Player> list = new ArrayList<>();
        list.add(getPlayer1());
        list.add(getPlayer2());
        list.add(getPlayer3());
        list.removeIf(Objects::isNull);
        return list;
    }
    
    public List<Player> getAllPlayers(final boolean nullValues) {
        final List<Player> list = new ArrayList<>();
        list.add(getPlayer1());
        list.add(getPlayer2());
        list.add(getPlayer3());
        if (!nullValues) {
            list.removeIf(Objects::isNull);
        }
        return list;
    }
    
    public boolean readyToPlay() {
        return this.getAllPlayers().size() == 3;
    }
    
    public Player getPlayer1() {
        return this.player1 == null ? null : ProBending.plugin.getServer().getPlayer(this.player1);
    }
    
    public Player getPlayer2() {
        return this.player2 == null ? null : ProBending.plugin.getServer().getPlayer(this.player2);
    }
    
    public Player getPlayer3() {
        return this.player3 == null ? null : ProBending.plugin.getServer().getPlayer(this.player3);
    }
}
    


