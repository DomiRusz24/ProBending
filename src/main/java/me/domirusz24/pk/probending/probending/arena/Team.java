package me.domirusz24.pk.probending.probending.arena;

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
    public boolean claimingStage;
    private int points;
    
    public Team(final Player player1, final Player player2, final Player player3, final TeamTag tag) {
        this.claimingStage = false;
        this.points = 0;
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
        i.add(this.player1.getPlayer());
        i.add(this.player2.getPlayer());
        i.add(this.player3.getPlayer());
        i.removeIf(Objects::isNull);
        return i;
    }
    
    public ArrayList<Player> getPlayers(final boolean nullValues) {
        final ArrayList<Player> i = new ArrayList<Player>();
        i.add(this.player1.getPlayer());
        i.add(this.player2.getPlayer());
        i.add(this.player3.getPlayer());
        if (!nullValues) {
            i.removeIf(Objects::isNull);
        }
        return i;
    }
    
    public boolean isFull() {
        return this.player1 != null && this.player2 != null && this.player3 != null;
    }
    
    public ArrayList<PBTeamPlayer> getPBPlayers(final boolean nullValues) {
        final ArrayList<PBTeamPlayer> i = new ArrayList<PBTeamPlayer>();
        i.add(this.player1);
        i.add(this.player2);
        i.add(this.player3);
        if (!nullValues) {
            i.removeIf(Objects::isNull);
        }
        return i;
    }
    
    public ArrayList<PBTeamPlayer> getPBPlayers() {
        final ArrayList<PBTeamPlayer> i = new ArrayList<PBTeamPlayer>();
        i.add(this.player1);
        i.add(this.player2);
        i.add(this.player3);
        i.removeIf(Objects::isNull);
        return i;
    }
}
