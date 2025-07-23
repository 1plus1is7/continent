package me.continent.player;

import me.continent.nation.Nation;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;

    private double gold;
    private final Set<String> pendingInvites = new HashSet<>();  // ✅ 중복 제거됨

    private Nation nation;

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public Nation getNation() {
        return this.nation;
    }

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.gold = 0;
    }

    private boolean nationChat = false;
    private boolean nationChatEnabled = false;
    private boolean kingdomChatEnabled = false;

    private int knownMaintenance = 0;


    public boolean isNationChatEnabled() {
        return nationChatEnabled;
    }

    public void setNationChatEnabled(boolean enabled) {
        this.nationChatEnabled = enabled;
    }

    public boolean isKingdomChatEnabled() {
        return kingdomChatEnabled;
    }

    public void setKingdomChatEnabled(boolean enabled) {
        this.kingdomChatEnabled = enabled;
    }



    public boolean isInNationChat() {
        return nationChat;
    }

    public void setNationChat(boolean nationChat) {
        this.nationChat = nationChat;
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
