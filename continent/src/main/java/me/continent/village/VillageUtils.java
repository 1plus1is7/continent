// VillageUtils.java

package me.continent.village;

import org.bukkit.Chunk;
import me.continent.storage.VillageStorage;

import java.util.*;

public class VillageUtils {

    public static boolean unclaimChunk(Village village, Chunk chunk) {
        String key = Village.getChunkKey(chunk);

        if (key.equals(village.getCoreChunk()) || key.equals(village.getSpawnChunk())) {
            return false; // 보호
        }


        // 스폰/코어 청크 해제 방지
        String spawnKey = village.getSpawnChunk();
        String coreKey = village.getCoreChunk();
        if (key.equals(spawnKey) || key.equals(coreKey)) {
            return false;
        }

        Set<String> claims = new HashSet<>(village.getClaimedChunks());
        claims.remove(key);

        if (!isConnected(claims, spawnKey)) {
            return false;
        }

        village.getClaimedChunks().remove(key);
        VillageStorage.save(village);
        return true;
    }

    // BFS 기반 연결 확인 함수
    private static boolean isConnected(Set<String> claims, String startKey) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(startKey);
        visited.add(startKey);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            String[] parts = current.split(":");
            String world = parts[0];
            int x = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) != 1) continue; // 상하좌우만
                    String neighbor = world + ":" + (x + dx) + ":" + (z + dz);
                    if (claims.contains(neighbor) && visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }

        return visited.containsAll(claims);
    }
}
