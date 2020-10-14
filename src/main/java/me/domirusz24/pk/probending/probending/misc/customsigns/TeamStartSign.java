package me.domirusz24.pk.probending.probending.misc.customsigns;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.team.TeamTag;
import me.domirusz24.pk.probending.probending.config.ConfigMethods;
import me.domirusz24.pk.probending.probending.misc.CustomSign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamStartSign extends CustomSign {

    Arena arena;

    TeamTag team;

    public TeamStartSign(Arena arena, TeamTag team) {
        super();
        this.arena = arena;
        this.team = team;
        setSign(ConfigMethods.getTeamSign(this));
    }

    @Override
    public List<String> getText() {
        List<String> t = new ArrayList<>();
        t.add("-------");
        return t;
    }

    @Override
    public void onRightClick(Player player) {

    }

    public Arena getArena() {
        return arena;
    }

    public TeamTag getTeam() {
        return team;
    }

    public String getPath() {
        return arena.getPath() + "." + team.name() + "sign";
    }
}
