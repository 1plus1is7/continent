package me.continent.kingdom;

import me.continent.player.PlayerDataManager;
import me.continent.storage.KingdomStorage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;

public class KingdomManager {

    // KingdomManager.java에 추가
    public static void addKingdom(Kingdom kingdom) {
        kingdomsByName.put(kingdom.getName().toLowerCase(), kingdom); // 이름은 소문자로 일치시킴
        kingdomsByPlayer.put(kingdom.getKing(), kingdom); // 플레이어 UUID로 등록
        KingdomStorage.save(kingdom);
    }

    private static final Map<String, Kingdom> kingdomsByName = new HashMap<>();
    private static final Map<UUID, Kingdom> kingdomsByPlayer = new HashMap<>();

    private static final Map<UUID, Kingdom> playerKingdoms = new HashMap<>();
    private static final Map<String, Kingdom> kingdoms = new HashMap<>();
    private static final Map<UUID, Set<String>> playerInvites = new HashMap<>();


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
        int y = world.getHighestBlockYAt(centerX, centerZ)+1;
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
        Location coreLocation = kingdom.getCoreLocation();
        if (coreLocation != null && coreLocation.getBlock().getType() == Material.BEACON) {
            coreLocation.getBlock().setType(Material.AIR);
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
        int y = (world.getHighestBlockYAt(x, z)+1);

        Location coreLocation = getSurfaceCenter(chunk);

        kingdom.setSpawnLocation(coreLocation);
        kingdom.setCoreLocation(coreLocation); // 위치 저장
        coreLocation.getBlock().setType(Material.BEACON); // 신호기 설치
        register(kingdom);
        KingdomStorage.save(kingdom);
        return true;
    }

    // 현재 플레이어가 속한 국가 반환
    public static Kingdom getKingdom(UUID playerUUID) {
        return playerKingdoms.get(playerUUID);
    }

    // 국가 이름으로 국가 객체 가져오기
    public static Kingdom getKingdomByName(String name) {
        if (name == null) return null;
        return kingdomsByName.get(name.toLowerCase());
    }

    // 초대 목록에서 해당 국가 제거
    public static void removeInvite(UUID playerUUID, String kingdomName) {
        Set<String> invites = playerInvites.get(playerUUID);
        if (invites != null) {
            invites.remove(kingdomName);
        }
    }

    // 플레이어의 초대 목록 반환
    public static Set<String> getInvites(UUID playerUUID) {
        return playerInvites.getOrDefault(playerUUID, new HashSet<>());
    }


    // 국가 가입 처리 (중복 방지, 기존 소속 제거 포함)
    public static void joinKingdom(UUID playerUUID, Kingdom kingdom) {
        Kingdom oldKingdom = getKingdom(playerUUID);
        if (oldKingdom != null) {
            oldKingdom.getMembers().remove(playerUUID);
        }

        kingdom.addMember(playerUUID);
        playerKingdoms.put(playerUUID, kingdom);

        PlayerDataManager.get(playerUUID).setKingdom(kingdom);

        KingdomStorage.savePlayerData(playerUUID);
        KingdomStorage.saveKingdomData(kingdom);
    }

    public static void removeMember(UUID uuid) {
        kingdomsByPlayer.remove(uuid);
    }

    public static void updateName(String oldName, Kingdom kingdom) {
        kingdomsByName.remove(oldName.toLowerCase());
        kingdomsByName.put(kingdom.getName().toLowerCase(), kingdom);
    }

}
