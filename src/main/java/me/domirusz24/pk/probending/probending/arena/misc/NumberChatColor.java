package me.domirusz24.pk.probending.probending.arena.misc;

import org.bukkit.ChatColor;

public enum NumberChatColor {
    ONE(1, ChatColor.DARK_RED),
    TWO(2, ChatColor.DARK_RED),
    THREE(3, ChatColor.RED),
    FOUR(4, ChatColor.RED),
    FIVE(5, ChatColor.YELLOW),
    SIX(6, ChatColor.YELLOW),
    SEVEN(7, ChatColor.GREEN),
    EIGHT(8, ChatColor.GREEN),
    NINE(9, ChatColor.DARK_GREEN),
    TEN(10, ChatColor.DARK_GREEN),
    NOTHING(999, ChatColor.DARK_BLUE);


    private final int value;
    private final ChatColor chatColor;


    NumberChatColor(int value, ChatColor chatColor) {
        this.value = value;
        this.chatColor = chatColor;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public int getValue() {
        return value;
    }

    public static NumberChatColor getFromValue(int value) {
        for (NumberChatColor i : NumberChatColor.values()) {
            if (value == i.getValue()) {
                return i;
            }
        }
        return NOTHING;
    }
}
