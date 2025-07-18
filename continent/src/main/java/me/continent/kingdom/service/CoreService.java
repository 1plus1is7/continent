package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import me.continent.storage.KingdomStorage;
import org.bukkit.Location;
import org.bukkit.Material;

public class CoreService {
    public static void placeCore(Kingdom kingdom, Location location) {
        Location ground = Kingdom.getGroundLocation(location);
        kingdom.setCoreLocation(ground);
        kingdom.setCoreChunk(ground.getChunk());
        ground.getBlock().setType(Material.BEACON);
        KingdomStorage.save(kingdom);
    }

    public static void removeCore(Kingdom kingdom) {
        Location loc = kingdom.getCoreLocation();
        if (loc != null && loc.getBlock().getType() == Material.BEACON) {
            loc.getBlock().setType(Material.AIR);
        }
    }
}
