package me.continent.nation.service;

import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;

import java.util.Set;
import java.util.UUID;

public class InviteService {
    public static void sendInvite(UUID target, String nationName) {
        PlayerData data = PlayerDataManager.get(target);
        data.getPendingInvites().add(nationName);
        PlayerDataManager.save(target);
    }

    public static void removeInvite(UUID target, String nationName) {
        PlayerData data = PlayerDataManager.get(target);
        data.getPendingInvites().remove(nationName);
        PlayerDataManager.save(target);
    }

    public static Set<String> getInvites(UUID target) {
        return PlayerDataManager.get(target).getPendingInvites();
    }
}
