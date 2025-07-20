package me.continent.kingdom;

import me.continent.village.Village;

import java.util.*;

public class Kingdom {
    private String name;
    private UUID leader;
    private String capital; // village name
    private final Set<String> villages = new HashSet<>();
    private double treasury = 0;
    private int maintenanceCount = 0;
    private int unpaidWeeks = 0;
    private long lastMaintenance = 0;
    private final Map<UUID, String> roles = new HashMap<>();
    private final Set<String> researchedNodes = new HashSet<>();

    public Kingdom(String name, UUID leader, Village capital) {
        this.name = name;
        this.leader = leader;
        this.capital = capital.getName();
        this.villages.add(capital.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public Set<String> getVillages() {
        return villages;
    }

    public double getTreasury() {
        return treasury;
    }

    public void setTreasury(double treasury) {
        this.treasury = treasury;
    }

    public void addGold(double amount) {
        this.treasury += amount;
    }

    public void removeGold(double amount) {
        this.treasury -= amount;
    }

    public int getMaintenanceCount() {
        return maintenanceCount;
    }

    public void setMaintenanceCount(int maintenanceCount) {
        this.maintenanceCount = maintenanceCount;
    }

    public int getUnpaidWeeks() {
        return unpaidWeeks;
    }

    public void setUnpaidWeeks(int unpaidWeeks) {
        this.unpaidWeeks = unpaidWeeks;
    }

    public long getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(long lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public Map<UUID, String> getRoles() {
        return roles;
    }

    public Set<String> getResearchedNodes() {
        return researchedNodes;
    }
}
