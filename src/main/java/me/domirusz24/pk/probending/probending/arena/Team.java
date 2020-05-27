package me.domirusz24.pk.probending.probending.arena;

import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Team {
    private PBTeamPlayer player1;
    private PBTeamPlayer player2;
    private PBTeamPlayer player3;
    private TeamTag teamTag;
    public boolean claimingStage = false;
    private int points = 0;

    public Team(Player player1, Player player2, Player player3, TeamTag tag) {
        this.player1 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player1), "WATER", this);
        this.player2 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player2), "WATER", this);
        this.player3 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player3), "WATER", this);
        this.teamTag = tag;
    }


    public PBTeamPlayer getPlayer1() {
        return player1;
    }

    public PBTeamPlayer getPlayer2() {
        return player2;
    }

    public PBTeamPlayer getPlayer3() {
        return player3;
    }

    public TeamTag getTeamTag() {
        return teamTag;
    }

    public void raisePoint() {
        points++;
    }

    public int getPoints() {
        return points;
    }

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> i = new ArrayList<>();
        i.add(player1.getPlayer());
        i.add(player2.getPlayer());
        i.add(player3.getPlayer());
        return i;
    }

    public boolean isFull() {
        return player1 != null && player2 != null &&player3 != null;
    }

    public ArrayList<PBTeamPlayer> getPBPlayers() {
        ArrayList<PBTeamPlayer> i = new ArrayList<>();
        i.add(player1);
        i.add(player2);
        i.add(player3);
        return i;
    }
}
