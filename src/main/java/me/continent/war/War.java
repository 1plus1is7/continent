package me.continent.war;

import java.util.*;

public class War {
    private final String attacker;
    private final String defender;
    private final long startTime;
    private final Map<String, Integer> coreHp = new HashMap<>();
    private final Map<String, String> destroyedNations = new HashMap<>();
    private final Map<UUID, Long> bannedPlayers = new HashMap<>();

    public War(String attacker, String defender) {
        this.attacker = attacker;
        this.defender = defender;
        this.startTime = System.currentTimeMillis();
    }

    public String getAttacker() {
        return attacker;
    }

    public String getDefender() {
        return defender;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getCoreHp(String nation) {
        return coreHp.getOrDefault(nation.toLowerCase(), 0);
    }

    public void setCoreHp(String nation, int hp) {
        coreHp.put(nation.toLowerCase(), hp);
    }

    public Map<String, Integer> getAllCoreHp() {
        return coreHp;
    }

    // ---- Core destruction tracking ----
    public void addDestroyedNation(String nation, String attackerName) {
        destroyedNations.put(nation.toLowerCase(), attackerName);
    }

    public boolean isNationDestroyed(String nation) {
        return destroyedNations.containsKey(nation.toLowerCase());
    }

    public String getCapturer(String nation) {
        return destroyedNations.get(nation.toLowerCase());
    }

    public Map<String, String> getDestroyedNations() {
        return destroyedNations;
    }

    // ---- Player ban tracking ----
    public void banPlayer(UUID uuid) {
        bannedPlayers.put(uuid, System.currentTimeMillis());
    }

    public boolean isPlayerBanned(UUID uuid) {
        return bannedPlayers.containsKey(uuid);
    }

    public Map<UUID, Long> getBannedPlayers() {
        return bannedPlayers;
    }
}
