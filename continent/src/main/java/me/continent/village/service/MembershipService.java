package me.continent.village.service;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.storage.VillageStorage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MembershipService {

    public static Village createVillage(String name, Player player) {
        if (VillageManager.exists(name)) return null;
        UUID uuid = player.getUniqueId();
        if (VillageManager.getByPlayer(uuid) != null) return null;
        Chunk chunk = player.getLocation().getChunk();
        if (ClaimService.isChunkClaimed(chunk)) return null;

        Village village = new Village(name, uuid);
        village.addChunk(chunk);

        Location ground = Village.getGroundLocation(player.getLocation());
        village.setSpawnLocation(ground);
        village.setCoreLocation(ground);
        String key = Village.getChunkKey(chunk);
        village.setSpawnChunkKey(key);
        village.setCoreChunkKey(key);
        ground.getBlock().setType(Material.BEACON);

        VillageManager.register(village);
        PlayerData data = PlayerDataManager.get(uuid);
        if (data != null) data.setVillage(village);
        VillageStorage.save(village);
        return village;
    }

    public static void joinVillage(Player player, Village village) {
        UUID uuid = player.getUniqueId();
        Village old = VillageManager.getByPlayer(uuid);
        if (old != null) {
            old.getMembers().remove(uuid);
        }
        village.addMember(uuid);
        PlayerData data = PlayerDataManager.get(uuid);
        if (data != null) data.setVillage(village);
        VillageManager.register(village);
        VillageStorage.save(village);
        PlayerDataManager.save(uuid);
    }

    public static void disband(Village village) {
        CoreService.removeCore(village);
        VillageManager.unregister(village);
        VillageStorage.delete(village);
        for (UUID uuid : village.getMembers()) {
            PlayerData data = PlayerDataManager.get(uuid);
            if (data != null) {
                data.setVillage(null);
                PlayerDataManager.save(uuid);
            }
        }
    }

    public static void leaveVillage(Player player, Village village) {
        UUID uuid = player.getUniqueId();
        village.removeMember(uuid);
        VillageManager.removeMember(uuid);

        PlayerData data = PlayerDataManager.get(uuid);
        if (data != null) {
            data.setVillage(null);
            PlayerDataManager.save(uuid);
        }

        VillageStorage.save(village);
    }

    public static boolean kickMember(Village village, UUID target) {
        if (!village.getMembers().contains(target)) return false;
        village.removeMember(target);
        VillageManager.removeMember(target);

        PlayerData data = PlayerDataManager.get(target);
        if (data != null) {
            data.setVillage(null);
            PlayerDataManager.save(target);
        }

        VillageStorage.save(village);
        return true;
    }

    public static boolean renameVillage(Village village, String newName) {
        if (VillageManager.exists(newName)) return false;
        String old = village.getName();
        village.setName(newName);
        VillageManager.updateName(old, village);
        VillageStorage.rename(old, newName);
        VillageStorage.save(village);
        return true;
    }
}
