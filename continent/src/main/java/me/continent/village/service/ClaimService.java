package me.continent.village.service;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.village.VillageUtils;
import me.continent.storage.VillageStorage;
import org.bukkit.Chunk;

public class ClaimService {
    public static boolean isChunkClaimed(Chunk chunk) {
        return VillageManager.isChunkClaimed(chunk);
    }

    public static void claim(Village village, Chunk chunk) {
        if (!village.hasChunk(chunk)) {
            village.addChunk(chunk);
            VillageManager.mapChunk(village, chunk);
            VillageStorage.save(village);
        }
    }

    public static boolean unclaim(Village village, Chunk chunk) {
        boolean result = VillageUtils.unclaimChunk(village, chunk);
        if (result) {
            VillageManager.unmapChunk(chunk);
        }
        return result;
    }
}
