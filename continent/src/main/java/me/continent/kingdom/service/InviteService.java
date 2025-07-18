package me.continent.kingdom.service;

import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;

import java.util.Set;
import java.util.UUID;

public class InviteService {
    public static void sendInvite(UUID target, String kingdomName) {
        PlayerData data = PlayerDataManager.get(target);
        data.getPendingInvites().add(kingdomName);
        PlayerDataManager.save(target);
    }

    public static void removeInvite(UUID target, String kingdomName) {
        PlayerData data = PlayerDataManager.get(target);
        data.getPendingInvites().remove(kingdomName);
        PlayerDataManager.save(target);
    }

    public static Set<String> getInvites(UUID target) {
        return PlayerDataManager.get(target).getPendingInvites();
    }
}
