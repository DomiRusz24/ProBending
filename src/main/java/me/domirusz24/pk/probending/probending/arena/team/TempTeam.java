package me.domirusz24.pk.probending.probending.arena.team;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TempTeam
{
    public static HashMap<Player, TempTeam> playersWaiting = new HashMap<>();
    private Player player1;
    private Player player2;
    private Player player3;
    
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

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        if (getAllPlayers(true) != null) {
            int i = 0;
            for (final Player player : getAllPlayers(true)) {
                ++i;
                final String playerstatus = (player == null) ? "NIE DODANY" : player.getName();
                info.add("Gracz " + i + ": " + playerstatus);
            }
        }
        return info;
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
        if (player == null) {
            return;
        }
        if (TempTeam.playersWaiting.containsKey(player)) {
            TempTeam.playersWaiting.get(player).removePlayer(player);
        }
        if (player1 == null) {
            player1 = player;
            TempTeam.playersWaiting.put(player, this);
        } else  if (player2 == null) {
            player2 = player;
            TempTeam.playersWaiting.put(player, this);
        } else if (player3 == null) {
            player3 = player;
            TempTeam.playersWaiting.put(player, this);
        }
    }
    
    public void removePlayer(final Player player) {
        if (!this.getAllPlayers().contains(player) || player == null) {
            return;
        }
        if (player.equals(player1)) {
            TempTeam.playersWaiting.remove(player);
            player1 = null;
        } else if (player.equals(player2)) {
            TempTeam.playersWaiting.remove(player);
            player2 = null;
        } else if (player.equals(player3)) {
            TempTeam.playersWaiting.remove(player);
            player3 = null;
        }
    }

    public Player getPlayer(int id) {
        switch(id) {
            case 1: {
                return player1;
            }
            case 2: {
                return player2;
            }
            case 3: {
                return player3;
            }
            default: {
                return null;
            }
        }
    }

    public int getPlayerID(Player player) {
        if (!this.getAllPlayers().contains(player)) {
            return 0;
        }
        int e = 0;
        for (Player i : this.getAllPlayers(true)) {
            e++;
            if (i.equals(player)) {
                return e;
            }
        }
        return 0;
    }
    
    public void removeAllPlayers() {
        if (getAllPlayers() == null) {
            return;
        }
        this.getAllPlayers().forEach(this::removePlayer);
    }
    
    public List<Player> getAllPlayers() {
        final List<Player> list = new ArrayList<>();
        list.add(getPlayer1());
        list.add(getPlayer2());
        list.add(getPlayer3());
        while (list.remove(null)) {
        }
        return list;
    }
    
    public List<Player> getAllPlayers(final boolean nullValues) {
        final List<Player> list = new ArrayList<>();
        list.add(getPlayer1());
        list.add(getPlayer2());
        list.add(getPlayer3());
        if (!nullValues) {
            while (list.remove(null)) {
            }
        }
        return list;
    }

    private void setPlayerTo(int id, Player player) {
        switch(id) {
            case 1: {
                player1 = player;
                return;
            }
            case 2: {
                player2 = player;
                return;
            }
            case 3: {
                player3 = player;
                return;
            }
        }
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
}
    


