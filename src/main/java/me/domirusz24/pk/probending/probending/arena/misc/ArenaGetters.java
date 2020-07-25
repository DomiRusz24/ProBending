package me.domirusz24.pk.probending.probending.arena.misc;

public enum ArenaGetters {


    Red("RedGetter"),
    Blue("BlueGetter"),
    Spectator("SpectatorGetter");

    public static ArenaGetters getGetterFromName(String name) {
        for (ArenaGetters e : ArenaGetters.values()) {
            if (e.getName().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }

    private final String name;

    ArenaGetters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
