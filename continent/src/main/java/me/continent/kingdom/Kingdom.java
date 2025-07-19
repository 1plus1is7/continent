package me.continent.kingdom;

import me.continent.village.Village;

import java.util.*;

public class Kingdom {
    private String name;
    private UUID leader;
    private String capital; // village name
    private final Set<String> villages = new HashSet<>();
    private double treasury = 0;
    private String color = "§6";
    private final Map<UUID, String> roles = new HashMap<>();

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Map<UUID, String> getRoles() {
        return roles;
    }
}
