package me.continent.village.service;

import me.continent.village.Village;
import me.continent.storage.VillageStorage;
import org.bukkit.Location;
import org.bukkit.Material;

public class CoreService {
    public static void placeCore(Village village, Location location) {
        Location ground = Village.getGroundLocation(location);
        village.setCoreLocation(ground);
        village.setCoreChunk(ground.getChunk());
        ground.getBlock().setType(Material.BEACON);
        VillageStorage.save(village);
    }

    public static void removeCore(Village village) {
        Location loc = village.getCoreLocation();
        if (loc != null && loc.getBlock().getType() == Material.BEACON) {
            loc.getBlock().setType(Material.AIR);
        }
    }
}
