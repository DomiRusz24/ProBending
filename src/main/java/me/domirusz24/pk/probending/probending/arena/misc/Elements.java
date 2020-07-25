package me.domirusz24.pk.probending.probending.arena.misc;

import com.projectkorra.projectkorra.Element;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public enum Elements {
    Earth(Element.EARTH, "[Z]", ChatColor.DARK_GREEN),
    Air(Element.AIR, "[P]", ChatColor.WHITE),
    Fire(Element.FIRE, "[O]", ChatColor.RED),
    Water(Element.WATER, "[W]", ChatColor.AQUA),
    Illegal(null, "[ILLEGAL]", ChatColor.BLACK),
    NonBender(null,"[N-B]", ChatColor.DARK_GRAY);

    public static List<Element> getValidElements() {
        List<Element> i = new ArrayList<>();
        for (Elements e : Elements.values()) {
            if (e.getElement() != null) {
                i.add(e.getElement());
            }
        }
        return i;
    }

    public static Elements getElement(Element element) {
        for (Elements e : Elements.values()) {
            if (e.getElement() != null) {
                if (e.getElement().equals(element)) {
                    return e;
                }
            }
        }
        return null;
    }

    private final Element element;
    private final String polish;
    private final ChatColor color;

    Elements(Element element, String polish, ChatColor color) {
        this.element = element;
        this.polish = polish;
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }

    public Element getElement() {
        return element;
    }

    public String getPolish() {
        return  getColor()  + "" + ChatColor.BOLD + polish + "" + ChatColor.RESET + "";
    }
}
