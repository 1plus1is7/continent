package me.continent.player;

import me.continent.village.Village;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;

    private double gold;
    private final Set<String> pendingInvites = new HashSet<>();  // ✅ 중복 제거됨

    private Village village;

    public void setVillage(Village village) {
        this.village = village;
    }

    public Village getVillage() {
        return this.village;
    }

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.gold = 0;
    }

    private boolean villageChat = false;
    private boolean villageChatEnabled = false;
    private boolean kingdomChatEnabled = false;

    private int knownMaintenance = 0;


    public boolean isVillageChatEnabled() {
        return villageChatEnabled;
    }

    public void setVillageChatEnabled(boolean enabled) {
        this.villageChatEnabled = enabled;
    }

    public boolean isnationChatEnabled() {
        return kingdomChatEnabled;
    }

    public void setnationChatEnabled(boolean enabled) {
        this.kingdomChatEnabled = enabled;
    }



    public boolean isInVillageChat() {
        return villageChat;
    }

    public void setVillageChat(boolean villageChat) {
        this.villageChat = villageChat;
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

    public int getKnownMaintenance() {
        return knownMaintenance;
    }

    public void setKnownMaintenance(int count) {
        this.knownMaintenance = count;
    }

}
