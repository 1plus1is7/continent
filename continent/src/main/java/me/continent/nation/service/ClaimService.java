package me.continent.nation.service;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.nation.NationUtils;
import me.continent.storage.NationStorage;
import org.bukkit.Chunk;

public class ClaimService {
    public static boolean isChunkClaimed(Chunk chunk) {
        return NationManager.isChunkClaimed(chunk);
    }

    public static void claim(Nation nation, Chunk chunk) {
        if (!nation.hasChunk(chunk)) {
            nation.addChunk(chunk);
            NationManager.mapChunk(nation, chunk);
            NationStorage.save(nation);
        }
    }

    public static boolean unclaim(Nation nation, Chunk chunk) {
        boolean result = NationUtils.unclaimChunk(nation, chunk);
        if (result) {
            NationManager.unmapChunk(chunk);
        }
        return result;
    }
}
