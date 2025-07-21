package me.continent.war;

import java.util.*;

public class War {
    private final String attacker;
    private final String defender;
    private final long startTime;
    private final Map<String, Integer> coreHp = new HashMap<>();
    private final Map<String, String> destroyedVillages = new HashMap<>();
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

    public int getCoreHp(String village) {
        return coreHp.getOrDefault(village.toLowerCase(), 0);
    }

    public void setCoreHp(String village, int hp) {
        coreHp.put(village.toLowerCase(), hp);
    }

    public Map<String, Integer> getAllCoreHp() {
        return coreHp;
    }

    // ---- Core destruction tracking ----
    public void addDestroyedVillage(String village, String attackernation) {
        destroyedVillages.put(village.toLowerCase(), attackernation);
    }

    public boolean isVillageDestroyed(String village) {
        return destroyedVillages.containsKey(village.toLowerCase());
    }

    public String getCapturer(String village) {
        return destroyedVillages.get(village.toLowerCase());
    }

    public Map<String, String> getDestroyedVillages() {
        return destroyedVillages;
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
