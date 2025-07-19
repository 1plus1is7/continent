package me.continent.village.service;

import me.continent.village.Village;
import me.continent.storage.VillageStorage;
import org.bukkit.Location;

public class SpawnService {
    public static void setSpawn(Village village, Location location) {
        Location ground = Village.getGroundLocation(location);
        village.setSpawnLocation(ground);
        village.setSpawnChunk(ground.getChunk());
        VillageStorage.save(village);
    }
}
