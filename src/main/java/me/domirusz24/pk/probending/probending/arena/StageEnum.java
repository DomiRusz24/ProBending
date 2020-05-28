package me.domirusz24.pk.probending.probending.arena;

import java.util.*;

public enum StageEnum
{
    BackRED(1, TeamTag.RED, "BEACHES"),
    ThirdRED(2, TeamTag.RED, "MESA"),
    SecondRED(3, TeamTag.RED, "SAVANNA"),
    FirstRED(4, TeamTag.RED, "JUNGLE_HILLS"),
    FirstBLUE(5, TeamTag.BLUE, "MESA_ROCK"),
    SecondBLUE(6, TeamTag.BLUE, "FOREST"),
    ThirdBLUE(7, TeamTag.BLUE, "JUNGLE"),
    BackBLUE(8, TeamTag.BLUE, "OCEAN"),
    WholeArena(11, null, "PLAINS"),
    TieBreakerRED(12, TeamTag.RED, "STONE_BEACH"),
    TieBreakerBLUE(13, TeamTag.BLUE, "RIVER"),
    Line(14, null, "SWAMPLAND");
    
    private final int ID;
    private final String biome;
    private final TeamTag teamTag;
    
    private StageEnum(final int ID, final TeamTag teamTag, final String biome) {
        this.ID = ID;
        this.biome = biome;
        this.teamTag = teamTag;
    }


    
    public int getID() {
        return this.ID;
    }
    
    public String getBiome() {
        return this.biome;
    }
    
    public TeamTag getTeamTag() {
        return this.teamTag;
    }
    
    public static StageEnum getFromID(final int ID) {
        for (final StageEnum e : values()) {
            if (e.ID == ID) {
                return e;
            }
        }
        System.out.println("Niepoprawne ID (" + ID + ")");
        throw new IllegalArgumentException();
    }
    
    public static StageEnum getFromBiome(final String biome) {
        for (final StageEnum e : values()) {
            if (e.biome.equalsIgnoreCase(biome)) {
                return e;
            }
        }
        return StageEnum.WholeArena;
    }
    
    public static ArrayList<StageEnum> getTeam(final TeamTag tag) {
        final ArrayList<StageEnum> enums = new ArrayList<>();
        for (final StageEnum e : values()) {
            if (e.teamTag == tag) {
                enums.add(e);
            }
        }
        return enums;
    }

    @Override
    public String toString() {
        switch (this) {
            case Line: return "Line";
            case TieBreakerBLUE: return "1BLUE";
            case TieBreakerRED: return "1RED";
            case WholeArena: return "Arena";
            case BackBLUE: return "5BLUE";
            case ThirdBLUE: return "4BLUE";
            case SecondBLUE: return "3BLUE";
            case FirstBLUE: return "2BLUE";
            case FirstRED: return "2RED";
            case SecondRED: return "3RED";
            case ThirdRED: return "4RED";
            case BackRED:return "5RED";
            default: return null;
        }
    }

    public static StageEnum getFromConfigName(String string) {
        for (StageEnum i : StageEnum.values()) {
            if (string.equalsIgnoreCase(i.toString())) {
                return i;
            }
        }
        return null;
    }


}
