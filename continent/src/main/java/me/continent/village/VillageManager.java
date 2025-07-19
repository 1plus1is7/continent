package me.continent.village;

import me.continent.player.PlayerDataManager;
import me.continent.storage.VillageStorage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;

public class VillageManager {

    private static final Map<String, Village> villagesByName = new HashMap<>();
    private static final Map<UUID, Village> villagesByPlayer = new HashMap<>();
    private static final Map<String, Village> chunkMap = new HashMap<>();
    private static final Map<UUID, Set<String>> playerInvites = new HashMap<>();

    public static void mapChunk(Village village, Chunk chunk) {
        chunkMap.put(Village.getChunkKey(chunk), village);
    }

    public static void unmapChunk(Chunk chunk) {
        chunkMap.remove(Village.getChunkKey(chunk));
    }


    // Village 이름으로 중복 확인
    public static boolean exists(String name) {
        return villagesByName.containsKey(name.toLowerCase());
    }

    public static Village getByChunk(Chunk chunk) {
        return chunkMap.get(Village.getChunkKey(chunk));
    }

    //11

    public static Location getSurfaceCenter(Chunk chunk) {
        World world = chunk.getWorld();
        int centerX = (chunk.getX() << 4) + 8;
        int centerZ = (chunk.getZ() << 4) + 8;
        int y = world.getHighestBlockYAt(centerX, centerZ)+1;
        return new Location(world, centerX, y, centerZ);
    }


    // Village 등록
    public static void register(Village village) {
        villagesByName.put(village.getName().toLowerCase(), village);
        for (UUID uuid : village.getMembers()) {
            villagesByPlayer.put(uuid, village);
        }
        for (String key : village.getClaimedChunks()) {
            chunkMap.put(key, village);
        }
    }

    // Village 제거
    public static void unregister(Village village) {
        Location coreLocation = village.getCoreLocation();
        if (coreLocation != null && coreLocation.getBlock().getType() == Material.BEACON) {
            coreLocation.getBlock().setType(Material.AIR);
        }

        // 2. 데이터 해제
        villagesByName.remove(village.getName().toLowerCase());
        for (UUID uuid : village.getMembers()) {
            villagesByPlayer.remove(uuid);
        }
        for (String key : village.getClaimedChunks()) {
            chunkMap.remove(key);
        }
    }

    // 이름으로 가져오기
    public static Village getByName(String name) {
        return villagesByName.get(name.toLowerCase());
    }

    // 플레이어로 가져오기
    public static Village getByPlayer(UUID uuid) {
        return villagesByPlayer.get(uuid);
    }

    // 모든 Village 반환
    public static Collection<Village> getAll() {
        return villagesByName.values();
    }

    // ✅ 플레이어가 Village에 속해있는지 확인
    public static boolean isPlayerInVillage(UUID uuid) {
        return villagesByPlayer.containsKey(uuid);
    }

    // ✅ 특정 청크가 이미 점령되었는지 확인
    public static boolean isChunkClaimed(Chunk chunk) {
        return chunkMap.containsKey(Village.getChunkKey(chunk));
    }

    // 특정 청크가 다른 마을의 영토와 일정 거리 이내인지 확인
    public static boolean isNearOtherVillage(Chunk chunk, Village self, int distance) {
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        for (int dx = -distance + 1; dx < distance; dx++) {
            for (int dz = -distance + 1; dz < distance; dz++) {
                if (dx == 0 && dz == 0) continue;
                Village other = chunkMap.get(world + ":" + (x + dx) + ":" + (z + dz));
                if (other != null && other != self) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearOtherVillage(Chunk chunk, int distance) {
        return isNearOtherVillage(chunk, null, distance);
    }



    // ✅ 새로운 Village 생성 및 등록
    public static boolean createVillage(String name, UUID king, Chunk chunk) {
        Village village = new Village(name, king);
        village.addChunk(chunk);

        // 청크의 중앙 지점 (지면 기준 Y=chunk.getWorld().getHighestBlockYAt())
        World world = chunk.getWorld();
        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;
        int y = (world.getHighestBlockYAt(x, z)+1);

        Location coreLocation = getSurfaceCenter(chunk);

        village.setSpawnLocation(coreLocation);
        village.setCoreLocation(coreLocation); // 위치 저장
        coreLocation.getBlock().setType(Material.BEACON); // 신호기 설치
        register(village);
        VillageStorage.save(village);
        return true;
    }

    // 현재 플레이어가 속한 마을 반환
    public static Village getVillage(UUID playerUUID) {
        return villagesByPlayer.get(playerUUID);
    }

    // 마을 이름으로 마을 객체 가져오기
    public static Village getVillageByName(String name) {
        if (name == null) return null;
        return villagesByName.get(name.toLowerCase());
    }

    // 초대 목록에서 해당 마을 제거
    public static void removeInvite(UUID playerUUID, String villageName) {
        Set<String> invites = playerInvites.get(playerUUID);
        if (invites != null) {
            invites.remove(villageName);
        }
    }

    // 플레이어의 초대 목록 반환
    public static Set<String> getInvites(UUID playerUUID) {
        return playerInvites.getOrDefault(playerUUID, new HashSet<>());
    }


    // 마을 가입 처리 (중복 방지, 기존 소속 제거 포함)
    public static void joinVillage(UUID playerUUID, Village village) {
        Village oldVillage = getVillage(playerUUID);
        if (oldVillage != null) {
            oldVillage.getMembers().remove(playerUUID);
        }

        village.addMember(playerUUID);
        villagesByPlayer.put(playerUUID, village);

        PlayerDataManager.get(playerUUID).setVillage(village);

        VillageStorage.savePlayerData(playerUUID);
        VillageStorage.saveVillageData(village);
    }

    public static void removeMember(UUID uuid) {
        villagesByPlayer.remove(uuid);
    }

    public static void updateName(String oldName, Village village) {
        villagesByName.remove(oldName.toLowerCase());
        villagesByName.put(village.getName().toLowerCase(), village);
    }

}
