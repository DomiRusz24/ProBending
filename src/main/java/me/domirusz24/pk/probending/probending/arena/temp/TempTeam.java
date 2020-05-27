package me.domirusz24.pk.probending.probending.arena.temp;

import org.bukkit.entity.*;
import java.util.*;
import java.util.function.*;

public class TempTeam
{
    public static HashMap<Player, TempTeam> playersWaiting;
    Player player1;
    Player player2;
    Player player3;
    
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
        if (TempTeam.playersWaiting.containsKey(player)) {
            TempTeam.playersWaiting.get(player).removePlayer(player);
        }
        if (this.player1 == null) {
            this.player1 = player;
            TempTeam.playersWaiting.put(player, this);
            return;
        }
        if (this.player2 == null) {
            this.player2 = player;
            TempTeam.playersWaiting.put(player, this);
            return;
        }
        if (this.player3 == null) {
            this.player3 = player;
            TempTeam.playersWaiting.put(player, this);
        }
    }
    
    public void removePlayer(final Player player) {
        if (!this.getAllPlayers().contains(player)) {
            return;
        }
        if (this.player1.equals(player)) {
            this.player1 = null;
            TempTeam.playersWaiting.remove(player);
            return;
        }
        if (this.player2.equals(player)) {
            this.player2 = null;
            TempTeam.playersWaiting.remove(player);
            return;
        }
        if (this.player3.equals(player)) {
            this.player3 = null;
            TempTeam.playersWaiting.remove(player);
        }
    }
    
    public void removeAllPlayers() {
        this.getAllPlayers().forEach(this::removePlayer);
    }
    
    public List<Player> getAllPlayers() {
        final List<Player> list = new ArrayList<Player>();
        list.add(this.player1);
        list.add(this.player2);
        list.add(this.player3);
        list.removeIf(Objects::isNull);
        return list;
    }
    
    public List<Player> getAllPlayers(final boolean nullValues) {
        final List<Player> list = new ArrayList<Player>();
        list.add(this.player1);
        list.add(this.player2);
        list.add(this.player3);
        if (!nullValues) {
            list.removeIf(Objects::isNull);
        }
        return list;
    }
    
    public boolean readyToPlay() {
        return this.getAllPlayers().size() == 3;
    }
    
    public Player getPlayer1() {
        return this.player1;
    }
    
    public Player getPlayer2() {
        return this.player2;
    }
    
    public Player getPlayer3() {
        return this.player3;
    }
    
    static {
        TempTeam.playersWaiting = new HashMap<Player, TempTeam>();
    }
}
