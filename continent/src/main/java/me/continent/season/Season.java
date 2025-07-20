package me.continent.season;

public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;

    public Season next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
