package me.domirusz24.pk.probending.probending.arena;

import java.util.*;

public enum StageEnum
{
    BackRED(1, TeamTag.RED, (byte) 3),
    ThirdRED(2, TeamTag.RED, (byte) 7),
    SecondRED(3, TeamTag.RED, (byte) 2),
    FirstRED(4, TeamTag.RED, (byte) 13),
    FirstBLUE(5, TeamTag.BLUE, (byte) 4),
    SecondBLUE(6, TeamTag.BLUE, (byte) 1),
    ThirdBLUE(7, TeamTag.BLUE, (byte) 10),
    BackBLUE(8, TeamTag.BLUE, (byte) 0),


    WholeArena(11, null, (byte) 12),
    TieBreakerRED(12, TeamTag.RED, (byte) 9),
    TieBreakerBLUE(13, TeamTag.BLUE, (byte) 5),
    Line(14, null, (byte) 15);
    
    private final int ID;
    private final byte data;
    private final TeamTag teamTag;
    
    private StageEnum(final int ID, final TeamTag teamTag, final byte data) {
        this.ID = ID;
        this.data = data;
        this.teamTag = teamTag;
    }


    
    public int getID() {
        return this.ID;
    }
    
    public int getData() {
        return this.data;
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
    
    public static StageEnum getFromData(final byte data) {
        for (final StageEnum e : values()) {
            if (e.data == data) {
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
