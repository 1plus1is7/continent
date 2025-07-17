package me.continent.player;

import me.continent.kingdom.Kingdom;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;

    private double gold;
    private final Set<String> pendingInvites = new HashSet<>();  // ✅ 중복 제거됨

    private Kingdom kingdom;

    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
    }

    public Kingdom getKingdom() {
        return this.kingdom;
    }

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.gold = 0;
    }

    public void setGold(double amount) {
        this.gold = amount;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getGold() {
        return gold;
    }

    public void addGold(double amount) {
        this.gold += amount;
    }

    public void removeGold(double amount) {
        this.gold -= amount;
    }

    // ✅ 초대 목록 getter
    public Set<String> getPendingInvites() {
        return pendingInvites;
    }
}
