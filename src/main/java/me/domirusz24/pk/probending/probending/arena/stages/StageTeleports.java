package me.domirusz24.pk.probending.probending.arena.stages;

public enum StageTeleports {

    Player1("player1", "p1"),
    Player2("player2", "p2"),
    Player3("player3", "p3"),
    Center("center", "c");

    public static StageTeleports getStageFromName(String name) {
        for (StageTeleports e : StageTeleports.values()) {
            if (e.getName().equalsIgnoreCase(name) || e.getShortcut().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }

    public static StageTeleports getStageFromNumber(int value) {
        switch (value) {
            case 1:
                return StageTeleports.Player1;
            case 2:
                return StageTeleports.Player2;
            case 3:
                return StageTeleports.Player3;
            default:
                return StageTeleports.Center;
        }
    }

    private final String name;
    private final String shortcut;

    StageTeleports(String name, String shortcut) {
        this.name = name;
        this.shortcut = shortcut;
    }

    public String getName() {
        return name;
    }

    public String getShortcut() {
        return shortcut;
    }
}
