package me.domirusz24.pk.probending.probending.misc;

public class CustomScore {

    private String string;
    private String value = "";
    private String prefix = "";

    public CustomScore(String string, String value) {
        this.string = string;
        this.value = value;
        this.prefix = ":";
    }

    public CustomScore(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public String getValue() {
        return value;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getCombined() {
        return string + "" + prefix + " " + value;
    }
}
