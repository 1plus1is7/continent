package me.continent.nation.service;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.storage.NationStorage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MembershipService {

    public static Nation createNation(String name, Player player) {
        if (NationManager.exists(name)) return null;
        UUID uuid = player.getUniqueId();
        if (NationManager.getByPlayer(uuid) != null) return null;
        Chunk chunk = player.getLocation().getChunk();
        if (ClaimService.isChunkClaimed(chunk)) return null;

        Nation nation = new Nation(name, uuid);
        nation.addChunk(chunk);

        Location ground = Nation.getGroundLocation(player.getLocation());
        nation.setSpawnLocation(ground);
        nation.setCoreLocation(ground);
        String key = Nation.getChunkKey(chunk);
        nation.setSpawnChunkKey(key);
        nation.setCoreChunkKey(key);
        ground.getBlock().setType(Material.BEACON);

        NationManager.register(nation);
        PlayerData data = PlayerDataManager.get(uuid);
        if (data != null) data.setNation(nation);
        NationStorage.save(nation);
        return nation;
    }

    public static void joinNation(Player player, Nation nation) {
        UUID uuid = player.getUniqueId();
        Nation old = NationManager.getByPlayer(uuid);
        if (old != null) {
            old.getMembers().remove(uuid);
        }
        nation.addMember(uuid);
        PlayerData data = PlayerDataManager.get(uuid);
        if (data != null) data.setNation(nation);
        NationManager.register(nation);
        NationStorage.save(nation);
        PlayerDataManager.save(uuid);
    }

    public static void disband(Nation nation) {
        CoreService.removeCore(nation);
        NationManager.unregister(nation);
        NationStorage.delete(nation);
        for (UUID uuid : nation.getMembers()) {
            PlayerData data = PlayerDataManager.get(uuid);
            if (data != null) {
                data.setNation(null);
                PlayerDataManager.save(uuid);
            }
        }
    }

    public static void leaveNation(Player player, Nation nation) {
        UUID uuid = player.getUniqueId();
        nation.removeMember(uuid);
        NationManager.removeMember(uuid);

        PlayerData data = PlayerDataManager.get(uuid);
        if (data != null) {
            data.setNation(null);
            PlayerDataManager.save(uuid);
        }

        NationStorage.save(nation);
    }

    public static boolean kickMember(Nation nation, UUID target) {
        if (!nation.getMembers().contains(target)) return false;
        nation.removeMember(target);
        NationManager.removeMember(target);

        PlayerData data = PlayerDataManager.get(target);
        if (data != null) {
            data.setNation(null);
            PlayerDataManager.save(target);
        }

        NationStorage.save(nation);
        return true;
    }

    public static boolean renameNation(Nation nation, String newName) {
        if (NationManager.exists(newName)) return false;
        String old = nation.getName();
        nation.setName(newName);
        NationManager.updateName(old, nation);
        NationStorage.rename(old, newName);
        NationStorage.save(nation);
        return true;
    }
}
