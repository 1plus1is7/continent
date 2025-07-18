// KingdomUtils.java

package me.continent.kingdom;

import org.bukkit.Chunk;

import java.util.*;

public class KingdomUtils {

    public static boolean unclaimChunk(Kingdom kingdom, Chunk chunk) {
        String key = Kingdom.getChunkKey(chunk);

        if (key.equals(kingdom.getCoreChunk()) || key.equals(kingdom.getSpawnChunk())) {
            return false; // 보호
        }


        // 스폰/코어 청크 해제 방지
        String spawnKey = kingdom.getSpawnChunk();
        String coreKey = kingdom.getCoreChunk();
        if (key.equals(spawnKey) || key.equals(coreKey)) {
            return false;
        }

        Set<String> claims = new HashSet<>(kingdom.getClaimedChunks());
        claims.remove(key);

        if (!isConnected(claims, spawnKey)) {
            return false;
        }

        kingdom.getClaimedChunks().remove(key);
        KingdomStorage.save(kingdom);
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
