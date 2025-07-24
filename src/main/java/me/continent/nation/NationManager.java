package me.continent.nation;

import me.continent.player.PlayerDataManager;
import me.continent.storage.NationStorage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;

public class NationManager {

    private static final Map<String, Nation> nationsByName = new HashMap<>();
    private static final Map<UUID, Nation> nationsByPlayer = new HashMap<>();
    private static final Map<String, Nation> chunkMap = new HashMap<>();
    private static final Map<UUID, Set<String>> playerInvites = new HashMap<>();

    public static void mapChunk(Nation nation, Chunk chunk) {
        chunkMap.put(Nation.getChunkKey(chunk), nation);
    }

    public static void unmapChunk(Chunk chunk) {
        chunkMap.remove(Nation.getChunkKey(chunk));
    }


    // Nation 이름으로 중복 확인
    public static boolean exists(String name) {
        return nationsByName.containsKey(name.toLowerCase());
    }

    public static Nation getByChunk(Chunk chunk) {
        return chunkMap.get(Nation.getChunkKey(chunk));
    }

    //11

    public static Location getSurfaceCenter(Chunk chunk) {
        World world = chunk.getWorld();
        int centerX = (chunk.getX() << 4) + 8;
        int centerZ = (chunk.getZ() << 4) + 8;
        int y = world.getHighestBlockYAt(centerX, centerZ)+1;
        return new Location(world, centerX, y, centerZ);
    }


    // Nation 등록
    public static void register(Nation nation) {
        nationsByName.put(nation.getName().toLowerCase(), nation);
        for (UUID uuid : nation.getMembers()) {
            nationsByPlayer.put(uuid, nation);
        }
        for (String key : nation.getClaimedChunks()) {
            chunkMap.put(key, nation);
        }
    }

    // Nation 제거
    public static void unregister(Nation nation) {
        Location coreLocation = nation.getCoreLocation();
        if (coreLocation != null && coreLocation.getBlock().getType() == Material.BEACON) {
            coreLocation.getBlock().setType(Material.AIR);
        }

        // 2. 데이터 해제
        nationsByName.remove(nation.getName().toLowerCase());
        for (UUID uuid : nation.getMembers()) {
            nationsByPlayer.remove(uuid);
        }
        for (String key : nation.getClaimedChunks()) {
            chunkMap.remove(key);
        }
    }

    // 이름으로 가져오기
    public static Nation getByName(String name) {
        return nationsByName.get(name.toLowerCase());
    }

    // 플레이어로 가져오기
    public static Nation getByPlayer(UUID uuid) {
        return nationsByPlayer.get(uuid);
    }

    // 모든 Nation 반환
    public static Collection<Nation> getAll() {
        return nationsByName.values();
    }

    // ✅ 플레이어가 Nation에 속해있는지 확인
    public static boolean isPlayerInNation(UUID uuid) {
        return nationsByPlayer.containsKey(uuid);
    }

    // ✅ 특정 청크가 이미 점령되었는지 확인
    public static boolean isChunkClaimed(Chunk chunk) {
        return chunkMap.containsKey(Nation.getChunkKey(chunk));
    }

    // 특정 청크가 다른 국가의 영토와 일정 거리 이내인지 확인
    public static boolean isNearOtherNation(Chunk chunk, Nation self, int distance) {
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        for (int dx = -distance + 1; dx < distance; dx++) {
            for (int dz = -distance + 1; dz < distance; dz++) {
                if (dx == 0 && dz == 0) continue;
                Nation other = chunkMap.get(world + ":" + (x + dx) + ":" + (z + dz));
                if (other != null && other != self) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearOtherNation(Chunk chunk, int distance) {
        return isNearOtherNation(chunk, null, distance);
    }



    // ✅ 새로운 Nation 생성 및 등록
    public static boolean createNation(String name, UUID king, Chunk chunk) {
        Nation nation = new Nation(name, king);
        nation.addChunk(chunk);

        // 청크의 중앙 지점 (지면 기준 Y=chunk.getWorld().getHighestBlockYAt())
        World world = chunk.getWorld();
        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;
        int y = (world.getHighestBlockYAt(x, z)+1);

        Location coreLocation = getSurfaceCenter(chunk);

        nation.setSpawnLocation(coreLocation);
        nation.setCoreLocation(coreLocation); // 위치 저장
        coreLocation.getBlock().setType(Material.BEACON); // 신호기 설치
        register(nation);
        NationStorage.save(nation);
        return true;
    }

    // 현재 플레이어가 속한 국가 반환
    public static Nation getNation(UUID playerUUID) {
        return nationsByPlayer.get(playerUUID);
    }

    // 국가 이름으로 국가 객체 가져오기
    public static Nation getNationByName(String name) {
        if (name == null) return null;
        return nationsByName.get(name.toLowerCase());
    }

    // 초대 목록에서 해당 국가 제거
    public static void removeInvite(UUID playerUUID, String nationName) {
        Set<String> invites = playerInvites.get(playerUUID);
        if (invites != null) {
            invites.remove(nationName);
        }
    }

    // 플레이어의 초대 목록 반환
    public static Set<String> getInvites(UUID playerUUID) {
        return playerInvites.getOrDefault(playerUUID, new HashSet<>());
    }


    // 국가 가입 처리 (중복 방지, 기존 소속 제거 포함)
    public static void joinNation(UUID playerUUID, Nation nation) {
        Nation oldNation = getNation(playerUUID);
        if (oldNation != null) {
            oldNation.getMembers().remove(playerUUID);
        }

        nation.addMember(playerUUID);
        nationsByPlayer.put(playerUUID, nation);

        PlayerDataManager.get(playerUUID).setNation(nation);

        NationStorage.savePlayerData(playerUUID);
        NationStorage.saveNationData(nation);
    }

    public static void removeMember(UUID uuid) {
        nationsByPlayer.remove(uuid);
    }

    public static void updateName(String oldName, Nation nation) {
        nationsByName.remove(oldName.toLowerCase());
        nationsByName.put(nation.getName().toLowerCase(), nation);
    }

}
