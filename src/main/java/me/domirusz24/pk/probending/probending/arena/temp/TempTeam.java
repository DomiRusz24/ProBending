package me.domirusz24.pk.probending.probending.arena.temp;

import org.bukkit.entity.Player;

public class TempTeam {
    Player player1;
    Player player2;
    Player player3;

    public TempTeam() {

    }

    public TempTeam(Player player1) {
        this.player1 = player1;
    }

    public TempTeam(Player player1,Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }
    public TempTeam(Player player1,Player player2,Player player3) {
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
    }
}
