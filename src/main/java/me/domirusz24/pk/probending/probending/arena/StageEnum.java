package me.domirusz24.pk.probending.probending.arena;

import org.bukkit.block.Biome;

import java.util.ArrayList;

public enum StageEnum {
    TieBreakerBLUE(13, TeamTag.BLUE, "river" ),
    TieBreakerRED(12, TeamTag.RED, "taiga"),
    WholeArena(11, null, "plains"), // Cala arena (NAJPIERW TO)
    BackBLUE(8, TeamTag.BLUE, "ocean"),
    ThirdBLUE(7, TeamTag.BLUE, "jungle"),
    SecondBLUE(6, TeamTag.BLUE, "forest" ),
    FirstBLUE(5, TeamTag.BLUE, "beach" ),
    FirstRED(4, TeamTag.RED, "swamp"),
    SecondRED(3, TeamTag.RED, "savanna"),
    ThirdRED(2, TeamTag.RED, "plains"),
    BackRED(1, TeamTag.RED, "mountains");

    private final int ID;
    private final String biome;
    private final TeamTag teamTag;

    StageEnum(int ID, TeamTag teamTag, String biome) {
        this.ID = ID;
        this.biome = biome;
        this.teamTag = teamTag;
    }

    public int getID() {
        return ID;
    }

    public String getBiome() {
        return biome;
    }

    public TeamTag getTeamTag() {
        return teamTag;
    }

    public static StageEnum getFromID(int ID) {
        for (StageEnum e : StageEnum.values()) {
            if (e.ID == ID) {
                return e;
            }
        }
        System.out.println("Niepoprawne ID (" + ID + ")");
        throw new IllegalArgumentException();
    }

    public static StageEnum getFromBiome(String biome) {
        for (StageEnum e : StageEnum.values()) {
            if (e.biome.equalsIgnoreCase(biome)) {
                return e;
            }
        }
        System.out.println("Niepoprawny biome (" + biome + ")");
        throw new IllegalArgumentException();
    }

    public static ArrayList<StageEnum> getTeam(TeamTag tag) {
        ArrayList<StageEnum> enums = new ArrayList<>();
        for (StageEnum e : StageEnum.values()) {
            if (e.teamTag == tag) {
                enums.add(e);
            }
        }
        return enums;
    }
}
