package me.continent.stat;

import java.util.EnumMap;
import java.util.Map;

public class PlayerStats {
    private final Map<StatType, Integer> stats = new EnumMap<>(StatType.class);
    private StatType mastery = null;
    private int unusedPoints = 0;
    private int lastLevelGiven = 0;

    public PlayerStats() {
        for (StatType type : StatType.values()) {
            stats.put(type, 0);
        }
    }

    public int get(StatType type) {
        return stats.getOrDefault(type, 0);
    }

    public void set(StatType type, int value) {
        stats.put(type, value);
    }

    public Map<StatType, Integer> getAll() {
        return stats;
    }

    public StatType getMastery() {
        return mastery;
    }

    public void setMastery(StatType mastery) {
        this.mastery = mastery;
    }

    public int getUnusedPoints() {
        return unusedPoints;
    }

    public void addPoints(int amount) {
        this.unusedPoints += amount;
    }

    public void usePoint() {
        if (unusedPoints > 0) unusedPoints--;
    }

    public int getLastLevelGiven() {
        return lastLevelGiven;
    }

    public void setLastLevelGiven(int lvl) {
        this.lastLevelGiven = lvl;
    }
}
