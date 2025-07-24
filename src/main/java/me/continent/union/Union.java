package me.continent.union;

import java.util.*;

/**
 * Represents a union of nations. Basic data model for expansion.
 */
public class Union {
    private String name;
    private String description = "";
    private String leader; // nation name
    private final Set<String> nations = new HashSet<>();
    private final Map<String, Set<String>> roles = new HashMap<>();
    private double treasury = 0.0;
    private final Set<String> invites = new HashSet<>();

    public Union(String name, String leader) {
        this.name = name;
        this.leader = leader;
        this.nations.add(leader);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public Set<String> getNations() {
        return nations;
    }

    public Map<String, Set<String>> getRoles() {
        return roles;
    }

    public double getTreasury() {
        return treasury;
    }

    public void setTreasury(double treasury) {
        this.treasury = treasury;
    }

    public Set<String> getInvites() {
        return invites;
    }

    // Utility methods
    public void addNation(String nation) {
        nations.add(nation);
    }

    public void removeNation(String nation) {
        nations.remove(nation);
        roles.remove(nation);
        invites.remove(nation);
    }

    public boolean hasNation(String nation) {
        return nations.contains(nation);
    }
}
