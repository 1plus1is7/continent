package me.continent.nation.service;

import me.continent.nation.Nation;
import me.continent.storage.NationStorage;
import org.bukkit.Location;

public class SpawnService {
    public static void setSpawn(Nation nation, Location location) {
        Location ground = Nation.getGroundLocation(location);
        nation.setSpawnLocation(ground);
        nation.setSpawnChunk(ground.getChunk());
        NationStorage.save(nation);
    }
}
