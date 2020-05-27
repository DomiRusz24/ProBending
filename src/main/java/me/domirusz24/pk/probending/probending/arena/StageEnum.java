package me.domirusz24.pk.probending.probending.arena;

import java.util.*;

public enum StageEnum
{
    Line(14, (TeamTag)null, "SWAMPLAND"), 
    TieBreakerBLUE(13, TeamTag.BLUE, "RIVER"), 
    TieBreakerRED(12, TeamTag.RED, "TAIGA"), 
    WholeArena(11, (TeamTag)null, "PLAINS"), 
    BackBLUE(8, TeamTag.BLUE, "OCEAN"), 
    ThirdBLUE(7, TeamTag.BLUE, "JUNGLE"), 
    SecondBLUE(6, TeamTag.BLUE, "FOREST"), 
    FirstBLUE(5, TeamTag.BLUE, "MESA_ROCK"), 
    FirstRED(4, TeamTag.RED, "JUNGLE_HILLS"), 
    SecondRED(3, TeamTag.RED, "SAVANNA"), 
    ThirdRED(2, TeamTag.RED, "MESA"), 
    BackRED(1, TeamTag.RED, "BEACHES");
    
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
        System.out.println("Niepoprawny biome (" + biome + ")");
        throw new IllegalArgumentException();
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
}
