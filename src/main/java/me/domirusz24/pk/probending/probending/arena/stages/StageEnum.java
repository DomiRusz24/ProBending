package me.domirusz24.pk.probending.probending.arena.stages;

import me.domirusz24.pk.probending.probending.arena.team.TeamTag;

import java.util.ArrayList;

public enum StageEnum
{
    BackRED(1, TeamTag.RED, (byte) 3),
    ThirdRED(2, TeamTag.RED, (byte) 7),
    SecondRED(3, TeamTag.RED, (byte) 2),
    FirstRED(4, TeamTag.RED, (byte) 13),
    TieBreakerRED(5, TeamTag.RED, (byte) 9),
    TieBreakerBLUE(6, TeamTag.BLUE, (byte) 5),
    FirstBLUE(7, TeamTag.BLUE, (byte) 4),
    SecondBLUE(8, TeamTag.BLUE, (byte) 1),
    ThirdBLUE(9, TeamTag.BLUE, (byte) 10),
    BackBLUE(10, TeamTag.BLUE, (byte) 0),


    WholeArena(11, null, (byte) 12),
    Line(12, null, (byte) 15);
    
    private final int ID;
    private final byte data;
    private final TeamTag teamTag;
    
    StageEnum(final int ID, final TeamTag teamTag, final byte data) {
        this.ID = ID;
        this.data = data;
        this.teamTag = teamTag;
    }


    
    public int getID() {
        return this.ID;
    }

    public int getID(TeamTag team) {
        if (this == Line || this == WholeArena) {
            return this.getID();
        }
        // 11 - (((10 - 1) % 10) + 1)
        if (team == TeamTag.RED) {
            return this.getID();
        } else if (team == TeamTag.BLUE) {
            return 11 - (((this.getID() - 1) % 10) + 1);
        } else {
            return this.getID();
        }
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


    public static int convertID(int ID, TeamTag team) {
        // 11 - (((10 - 1) % 10) + 1)
        if (team == TeamTag.RED) {
            return ID;
        } else if (team == TeamTag.BLUE) {
            return 11 - (((ID - 1) % 10) + 1);
        } else {
            return ID;
        }
    }

    public static int convertID(int ID) {
        return 11 - (((ID - 1) % 10) + 1);
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

    public String polishName() {
        switch (this) {
            case BackRED: return "Tyl";
            case ThirdRED: return "Strefa 1";
            case SecondRED: return "Strefa 2";
            case FirstRED: return "Strefa 3";
            case TieBreakerRED: return "Strefa 4";
            case TieBreakerBLUE: return "Strefa 5";
            case FirstBLUE: return "Strefa 6";
            case SecondBLUE: return "Strefa 7";
            case ThirdBLUE: return "Strefa 8";
            case BackBLUE: return "Tyl druzyny przeciwnej";
            case WholeArena: return "Bok areny";
            case Line: return "Lina";
            default: return "null";
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
