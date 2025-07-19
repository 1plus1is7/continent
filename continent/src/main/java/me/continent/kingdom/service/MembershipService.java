package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.storage.KingdomStorage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MembershipService {

    public static Kingdom createKingdom(String name, Player player) {
        if (KingdomManager.exists(name)) return null;
        UUID uuid = player.getUniqueId();
        if (KingdomManager.getByPlayer(uuid) != null) return null;
        Chunk chunk = player.getLocation().getChunk();
        if (ClaimService.isChunkClaimed(chunk)) return null;

        Kingdom kingdom = new Kingdom(name, uuid);
        String defaultColor = me.continent.ContinentPlugin.getInstance()
                .getConfig().getString("colors.kingdom-default", "§a");
        kingdom.setColor(defaultColor);
        kingdom.addChunk(chunk);

        Location ground = Kingdom.getGroundLocation(player.getLocation());
        kingdom.setSpawnLocation(ground);
        kingdom.setCoreLocation(ground);
        String key = Kingdom.getChunkKey(chunk);
        kingdom.setSpawnChunkKey(key);
        kingdom.setCoreChunkKey(key);
        ground.getBlock().setType(Material.BEACON);

        KingdomManager.register(kingdom);
        PlayerData data = PlayerDataManager.get(uuid);
        if (data != null) data.setKingdom(kingdom);
        KingdomStorage.save(kingdom);
        return kingdom;
    }

    public static void joinKingdom(Player player, Kingdom kingdom) {
        UUID uuid = player.getUniqueId();
        Kingdom old = KingdomManager.getByPlayer(uuid);
        if (old != null) {
            old.getMembers().remove(uuid);
        }
        kingdom.addMember(uuid);
        PlayerData data = PlayerDataManager.get(uuid);
        if (data != null) data.setKingdom(kingdom);
        KingdomManager.register(kingdom);
        KingdomStorage.save(kingdom);
        PlayerDataManager.save(uuid);
    }

    public static void disband(Kingdom kingdom) {
        CoreService.removeCore(kingdom);
        KingdomManager.unregister(kingdom);
        KingdomStorage.delete(kingdom);
        for (UUID uuid : kingdom.getMembers()) {
            PlayerData data = PlayerDataManager.get(uuid);
            if (data != null) {
                data.setKingdom(null);
                PlayerDataManager.save(uuid);
            }
        }
    }

    public static void leaveKingdom(Player player, Kingdom kingdom) {
        UUID uuid = player.getUniqueId();
        kingdom.removeMember(uuid);
        KingdomManager.removeMember(uuid);

        PlayerData data = PlayerDataManager.get(uuid);
        if (data != null) {
            data.setKingdom(null);
            PlayerDataManager.save(uuid);
        }

        KingdomStorage.save(kingdom);
    }

    public static boolean kickMember(Kingdom kingdom, UUID target) {
        if (!kingdom.getMembers().contains(target)) return false;
        kingdom.removeMember(target);
        KingdomManager.removeMember(target);

        PlayerData data = PlayerDataManager.get(target);
        if (data != null) {
            data.setKingdom(null);
            PlayerDataManager.save(target);
        }

        KingdomStorage.save(kingdom);
        return true;
    }

    public static boolean renameKingdom(Kingdom kingdom, String newName) {
        if (KingdomManager.exists(newName)) return false;
        String old = kingdom.getName();
        kingdom.setName(newName);
        KingdomManager.updateName(old, kingdom);
        KingdomStorage.rename(old, newName);
        KingdomStorage.save(kingdom);
        return true;
    }
}
