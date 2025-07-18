package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.kingdom.KingdomUtils;
import me.continent.storage.KingdomStorage;
import org.bukkit.Chunk;

public class ClaimService {
    public static boolean isChunkClaimed(Chunk chunk) {
        return KingdomManager.isChunkClaimed(chunk);
    }

    public static void claim(Kingdom kingdom, Chunk chunk) {
        if (!kingdom.hasChunk(chunk)) {
            kingdom.addChunk(chunk);
            KingdomStorage.save(kingdom);
        }
    }

    public static boolean unclaim(Kingdom kingdom, Chunk chunk) {
        return KingdomUtils.unclaimChunk(kingdom, chunk);
    }
}
