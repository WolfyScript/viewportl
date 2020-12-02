package me.wolfyscript.utilities.main.messages;

public class WolfyUtilitiesVerifyMessage {

    private final boolean hasWU;
    private final String version;

    public WolfyUtilitiesVerifyMessage(boolean hasWU, String version) {
        this.hasWU = hasWU;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public boolean hasWolfyUtilities() {
        return hasWU;
    }

    @Override
    public String toString() {
        return "WolfyUtilitiesVerifyMessage{" +
                "hasWU=" + hasWU +
                ", version='" + version + '\'' +
                '}';
    }
}
