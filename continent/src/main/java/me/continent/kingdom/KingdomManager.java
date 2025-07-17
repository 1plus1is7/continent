package me.continent.kingdom;

import  me.continent.kingdom.Kingdom;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;

public class KingdomManager {

    private static final Map<String, Kingdom> kingdomsByName = new HashMap<>();
    private static final Map<UUID, Kingdom> kingdomsByPlayer = new HashMap<>();

    // Kingdom 이름으로 중복 확인
    public static boolean exists(String name) {
        return kingdomsByName.containsKey(name.toLowerCase());
    }

    public static Kingdom getByChunk(Chunk chunk) {
        String key = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        for (Kingdom kingdom : kingdomsByName.values()) {
            if (kingdom.getClaimedChunks().contains(key)) {
                return kingdom;
            }
        }
        return null;
    }

    //11

    public static Location getSurfaceCenter(Chunk chunk) {
        World world = chunk.getWorld();
        int centerX = (chunk.getX() << 4) + 8;
        int centerZ = (chunk.getZ() << 4) + 8;
        int y = world.getHighestBlockYAt(centerX, centerZ);
        return new Location(world, centerX, y, centerZ);
    }


    // Kingdom 등록
    public static void register(Kingdom kingdom) {
        kingdomsByName.put(kingdom.getName().toLowerCase(), kingdom);
        for (UUID uuid : kingdom.getMembers()) {
            kingdomsByPlayer.put(uuid, kingdom);
        }
    }

    // Kingdom 제거
    public static void unregister(Kingdom kingdom) {
        // 1. 신호기 제거
        Location core = kingdom.getCoreLocation();
        if (core != null && core.getBlock().getType() == Material.BEACON) {
            core.getBlock().setType(Material.AIR);
        }

        // 2. 데이터 해제
        kingdomsByName.remove(kingdom.getName().toLowerCase());
        for (UUID uuid : kingdom.getMembers()) {
            kingdomsByPlayer.remove(uuid);
        }
    }

    // 이름으로 가져오기
    public static Kingdom getByName(String name) {
        return kingdomsByName.get(name.toLowerCase());
    }

    // 플레이어로 가져오기
    public static Kingdom getByPlayer(UUID uuid) {
        return kingdomsByPlayer.get(uuid);
    }

    // 모든 Kingdom 반환
    public static Collection<Kingdom> getAll() {
        return kingdomsByName.values();
    }

    // ✅ 플레이어가 Kingdom에 속해있는지 확인
    public static boolean isPlayerInKingdom(UUID uuid) {
        return kingdomsByPlayer.containsKey(uuid);
    }

    // ✅ 특정 청크가 이미 점령되었는지 확인
    public static boolean isChunkClaimed(Chunk chunk) {
        String key = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        for (Kingdom kingdom : kingdomsByName.values()) {
            if (kingdom.getClaimedChunks().contains(key)) {
                return true;
            }
        }
        return false;
    }

    // ✅ 새로운 Kingdom 생성 및 등록
    public static boolean createKingdom(String name, UUID king, Chunk chunk) {
        Kingdom kingdom = new Kingdom(name, king);
        kingdom.addChunk(chunk);

        // 청크의 중앙 지점 (지면 기준 Y=chunk.getWorld().getHighestBlockYAt())
        World world = chunk.getWorld();
        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;
        int y = world.getHighestBlockYAt(x, z);

        Location coreLocation = getSurfaceCenter(chunk);

        kingdom.setSpawnLocation(coreLocation);
        kingdom.setCoreLocation(coreLocation); // 위치 저장
        coreLocation.getBlock().setType(Material.BEACON); // 신호기 설치
        register(kingdom);
        KingdomStorage.save(kingdom);
        return true;
    }
}
