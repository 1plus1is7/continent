package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import me.continent.storage.KingdomStorage;
import org.bukkit.Location;

public class SpawnService {
    public static void setSpawn(Kingdom kingdom, Location location) {
        Location ground = Kingdom.getGroundLocation(location);
        kingdom.setSpawnLocation(ground);
        kingdom.setSpawnChunk(ground.getChunk());
        KingdomStorage.save(kingdom);
    }
}
