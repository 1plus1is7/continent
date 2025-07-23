// NationUtils.java

package me.continent.nation;

import org.bukkit.Chunk;
import me.continent.storage.NationStorage;

import java.util.*;

public class NationUtils {

    public static boolean unclaimChunk(Nation nation, Chunk chunk) {
        String key = Nation.getChunkKey(chunk);

        if (key.equals(nation.getCoreChunk()) || key.equals(nation.getSpawnChunk())) {
            return false; // 보호
        }


        // 스폰/코어 청크 해제 방지
        String spawnKey = nation.getSpawnChunk();
        String coreKey = nation.getCoreChunk();
        if (key.equals(spawnKey) || key.equals(coreKey)) {
            return false;
        }

        Set<String> claims = new HashSet<>(nation.getClaimedChunks());
        claims.remove(key);

        if (!isConnected(claims, spawnKey)) {
            return false;
        }

        nation.getClaimedChunks().remove(key);
        NationStorage.save(nation);
        return true;
    }

    // BFS로 청크 연결 여부 확인
    private static boolean isConnected(Set<String> claims, String startKey) {
        if (claims.isEmpty()) return true; // 영토가 없는 경우 연결 확인 필요 없음

        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        visited.add(startKey);
        queue.add(startKey);

        // 상하좌우 이동 벡터 미리 정의
        final int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };

        while (!queue.isEmpty()) {
            String current = queue.poll();
            String[] parts = current.split(":");
            String world = parts[0];
            int x = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);

            String base = world + ":";
            for (int[] d : dirs) {
                String neighbor = base + (x + d[0]) + ":" + (z + d[1]);
                if (claims.contains(neighbor) && visited.add(neighbor)) {
                    queue.add(neighbor);
                    // 모든 영토를 방문하면 바로 종료
                    if (visited.size() == claims.size()) {
                        return true;
                    }
                }
            }
        }

        return visited.size() == claims.size();
    }
}
