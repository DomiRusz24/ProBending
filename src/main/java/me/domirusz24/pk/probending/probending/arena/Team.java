package me.domirusz24.pk.probending.probending.arena;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import com.projectkorra.projectkorra.*;
import java.util.*;
import java.util.function.*;

public class Team
{
    private PBTeamPlayer player1;
    private PBTeamPlayer player2;
    private PBTeamPlayer player3;
    private TeamTag teamTag;
    private Arena arena;
    private int points;
    
    public Team(final Player player1, final Player player2, final Player player3, final TeamTag tag, Arena arena) {
        this.points = 0;
        this.arena = arena;
        this.player1 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player1), "WATER", this);
        this.player2 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player2), "WATER", this);
        this.player3 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player3), "WATER", this);
        this.teamTag = tag;
    }
    
    public PBTeamPlayer getPBPlayer(final Player player) {
        for (final PBTeamPlayer teamPlayer : this.getPBPlayers()) {
            if (player.equals(teamPlayer.getPlayer())) {
                return teamPlayer;
            }
        }
        return null;
    }

    public Arena getArena() {
        return arena;
    }

    public int getPBPlayerNumber(final PBTeamPlayer pbTeamPlayer) {
        int i = 0;
        for (final PBTeamPlayer teamPlayer : this.getPBPlayers()) {
            ++i;
            if (pbTeamPlayer.equals(teamPlayer)) {
                return i;
            }
        }
        return 0;
    }
    
    public PBTeamPlayer getPBPlayer(final int value) {
        switch (value) {
            case 1: {
                return this.player1;
            }
            case 2: {
                return this.player2;
            }
            case 3: {
                return this.player3;
            }
            default: {
                return null;
            }
        }
    }

    public boolean checkWipeOut() {
        if (getPBPlayers().isEmpty()) {
            return true;
        }
        boolean bool = true;
        for (PBTeamPlayer player : this.getPBPlayers()) {
            if (!player.isKilled()) {
                bool = false;
                break;
            }
        }
        return bool;
    }

    public String getPolishName() {
        return this.getTeamTag() == TeamTag.BLUE ? "niebieska" : "czerwona";

    }

    public ChatColor getColor() {
        return this.getTeamTag() == TeamTag.BLUE ? ChatColor.BLUE : ChatColor.RED;
    }

    public TeamTag getEnemyTeamTag() {
        return this.getTeamTag() == TeamTag.BLUE ? TeamTag.RED : TeamTag.BLUE;
    }
    
    public PBTeamPlayer getPlayer1() {
        return this.player1;
    }
    
    public PBTeamPlayer getPlayer2() {
        return this.player2;
    }
    
    public PBTeamPlayer getPlayer3() {
        return this.player3;
    }
    
    public TeamTag getTeamTag() {
        return this.teamTag;
    }
    
    public void raisePoint() {
        ++this.points;
    }
    
    public int getPoints() {
        return this.points;
    }
    
    public ArrayList<Player> getPlayers() {
        final ArrayList<Player> i = new ArrayList<Player>();
        i.add(this.player1 == null ? null : this.player1.getPlayer());
        i.add(this.player2 == null ? null : this.player2.getPlayer());
        i.add(this.player3 == null ? null : this.player3.getPlayer());
        while (i.remove(null)) {
        }
        return i;
    }
    
    public ArrayList<Player> getPlayers(final boolean nullValues) {
        final ArrayList<Player> i = new ArrayList<>();
        i.add(this.player1 == null ? null : this.player1.getPlayer());
        i.add(this.player2 == null ? null : this.player2.getPlayer());
        i.add(this.player3 == null ? null : this.player3.getPlayer());
        if (!nullValues) {
            while (i.remove(null)) {
            }
        }
        return i;
    }
    
    public boolean isFull() {
        return this.player1 != null && this.player2 != null && this.player3 != null;
    }
    
    public ArrayList<PBTeamPlayer> getPBPlayers(final boolean nullValues) {
        final ArrayList<PBTeamPlayer> i = new ArrayList<>();
        i.add(this.player1.getPlayer() == null ? null : this.player1);
        i.add(this.player2.getPlayer() == null ? null : this.player2);
        i.add(this.player3.getPlayer() == null ? null : this.player3);
        if (!nullValues) {
            while (i.remove(null)) {
            }
            i.removeIf(e -> e.getPlayer() == null);
        }
        return i;
    }
    
    public ArrayList<PBTeamPlayer> getPBPlayers() {
        final ArrayList<PBTeamPlayer> i = new ArrayList<>();
        i.add(this.player1.getPlayer() == null ? null : this.player1);
        i.add(this.player2.getPlayer() == null ? null : this.player2);
        i.add(this.player3.getPlayer() == null ? null : this.player3);
        while (i.remove(null)) {
        }
        i.removeIf(e -> e.getPlayer() == null);
        return i;
    }
}
