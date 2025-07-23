package me.continent.nation.service;

import me.continent.nation.Nation;
import me.continent.storage.NationStorage;
import org.bukkit.Location;
import org.bukkit.Material;

public class CoreService {
    public static void placeCore(Nation nation, Location location) {
        Location ground = Nation.getGroundLocation(location);
        nation.setCoreLocation(ground);
        nation.setCoreChunk(ground.getChunk());
        ground.getBlock().setType(Material.BEACON);
        NationStorage.save(nation);
    }

    public static void removeCore(Nation nation) {
        Location loc = nation.getCoreLocation();
        if (loc != null && loc.getBlock().getType() == Material.BEACON) {
            loc.getBlock().setType(Material.AIR);
        }
    }
}
