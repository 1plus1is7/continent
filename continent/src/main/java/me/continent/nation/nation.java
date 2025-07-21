package me.continent.nation;

import me.continent.village.Village;

import java.util.*;

public class nation {
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
    private final Set<String> specialties = new HashSet<>();
    private final Set<String> selectedResearchTrees = new HashSet<>();
    private final Set<String> selectedT4Nodes = new HashSet<>();
    private int researchSlots = 1;
    private org.bukkit.inventory.ItemStack flag;
    private String description = "";
    private org.bukkit.inventory.ItemStack[] chestContents = new org.bukkit.inventory.ItemStack[27];
    private double taxRate = 0;
    private boolean territoryProtection = true;

    public nation(String name, UUID leader, Village capital) {
        this.name = name;
        this.leader = leader;
        this.capital = capital.getName();
        this.villages.add(capital.getName());
        this.flag = new org.bukkit.inventory.ItemStack(org.bukkit.Material.WHITE_BANNER);
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

    public Set<String> getSpecialties() {
        return specialties;
    }

    public Set<String> getSelectedResearchTrees() {
        return selectedResearchTrees;
    }

    public Set<String> getSelectedT4Nodes() {
        return selectedT4Nodes;
    }

    public int getResearchSlots() {
        return researchSlots;
    }

    public void setResearchSlots(int researchSlots) {
        this.researchSlots = researchSlots;
    }

    public org.bukkit.inventory.ItemStack getFlag() {
        return flag;
    }

    public void setFlag(org.bukkit.inventory.ItemStack flag) {
        this.flag = flag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public org.bukkit.inventory.ItemStack[] getChestContents() {
        return chestContents;
    }

    public void setChestContents(org.bukkit.inventory.ItemStack[] items) {
        if (items == null) {
            this.chestContents = new org.bukkit.inventory.ItemStack[27];
        } else {
            this.chestContents = java.util.Arrays.copyOf(items, 27);
        }
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public boolean isTerritoryProtectionEnabled() {
        return territoryProtection;
    }

    public void setTerritoryProtectionEnabled(boolean enabled) {
        this.territoryProtection = enabled;
    }
}
