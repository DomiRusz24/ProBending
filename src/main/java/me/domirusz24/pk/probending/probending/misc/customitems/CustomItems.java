package me.domirusz24.pk.probending.probending.misc.customitems;

import me.domirusz24.pk.probending.probending.misc.CustomItem;

public enum CustomItems {
    SpectatorLeave(new SpectatorLeaveItem());

    private CustomItem i;

    CustomItems(CustomItem i) {
        this.i = i;
    }

    public CustomItem getItem() {
        return i;
    }
}
