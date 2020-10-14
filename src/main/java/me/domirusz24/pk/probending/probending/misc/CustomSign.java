package me.domirusz24.pk.probending.probending.misc;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomSign {

    public static final ArrayList<CustomSign> CUSTOM_SIGNS = new ArrayList<>();

    private Sign sign = null;

    public CustomSign() {
        CUSTOM_SIGNS.add(this);
    }

    public CustomSign(Sign sign) {
        CUSTOM_SIGNS.add(this);
        if (sign != null) this.sign = sign;
    }

    public boolean isSet() {
        return sign != null && sign.getBlock() instanceof Sign;
    }

    public void updateSign() {
        if (sign != null) return;
        List<String> text = getText();
        if (!text.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                if (text.size() < i + 1) break;
                sign.setLine(i, text.get(i));
            }
        }
    }

    public void setSign(Sign sign) {
        if (isSet()) {
            this.sign.setLine(0, "");
            this.sign.setLine(1, "");
            this.sign.setLine(2, "");
            this.sign.setLine(3, "");
        }
        if (sign == null) return;
        List<String> text = getText();
        if (!text.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                if (text.size() < i + 1) break;
                sign.setLine(i, text.get(i));
            }
        }
        this.sign = sign;
    }

    public Sign getSign() {
        return sign;
    }

    abstract public List<String> getText();

    abstract public void onRightClick(Player player);
}
