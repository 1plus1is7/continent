package me.continent.kingdom;

import org.bukkit.Location;
import org.bukkit.Chunk;

import java.util.*;

public class Kingdom {

    private final String name;
    private final UUID king;
    private double fund = 0;
    private long protectionEnd = 0;
    private final Set<UUID> members = new HashSet<>();
    private final Set<String> claimedChunks = new HashSet<>(); // world:x:z
    private Location spawnLocation;
    private Location coreLocation;
    private double treasury;
    private long protectionUntil;




    public boolean hasChunk(Chunk chunk) {
        String key = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        return claimedChunks.contains(key);
    }

    public void setCoreChunk(Chunk chunk) {
        this.coreChunkKey = getChunkKey(chunk);
    }

    public void setSpawnChunk(Chunk chunk) {
        this.spawnChunkKey = getChunkKey(chunk);
    }


    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    // Kingdom.java
    public static String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }


    // 이 메서드는 제거해주세요
    public void removeChunk(Chunk chunk) {
        String key = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        claimedChunks.remove(key);
    }


    public boolean isAdjacent(Chunk chunk) {
        String worldName = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        for (String key : claimedChunks) {
            String[] parts = key.split(":");
            if (!parts[0].equals(worldName)) continue;

            int cx = Integer.parseInt(parts[1]);
            int cz = Integer.parseInt(parts[2]);

            // 상하좌우 확인 (대각선 제외)
            if ((Math.abs(cx - x) == 1 && cz == z) || (Math.abs(cz - z) == 1 && cx == x)) {
                return true;
            }
        }

        return false;
    }

    public boolean isVillage() {
        return claimedChunks.size() <= 16;
    }



    public Kingdom(String name, UUID king) {
        this.name = name;
        this.king = king;
        this.members.add(king);
        this.treasury = 0;
        this.protectionUntil = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L; // 7일
    }
    public double getFund() {
        return fund;
    }

    public void setFund(double fund) {
        this.fund = fund;
    }

    public long getProtectionEnd() {
        return protectionEnd;
    }

    public void setProtectionEnd(long protectionEnd) {
        this.protectionEnd = protectionEnd;
    }

    private String coreChunkKey;
    private String spawnChunkKey;


    // 코어 청크 키 반환
    public String getCoreChunk() {
        return this.coreChunkKey; // 또는 coreChunk 또는 getCoreLocationChunkKey()
    }

    // 스폰 청크 키 반환
    public String getSpawnChunk() {
        return this.spawnChunkKey; // 또는 spawnChunk 또는 getSpawnLocationChunkKey()
    }




    public String getName() { return name; }
    public UUID getKing() { return king; }
    public Set<UUID> getMembers() { return members; }

    public Set<String> getClaimedChunks() { return claimedChunks; }

    public void addChunk(Chunk chunk) {
        claimedChunks.add(chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ());
    }

    public Location getSpawnLocation() { return spawnLocation; }
    public void setSpawnLocation(Location location) { this.spawnLocation = location; }

    public Location getCoreLocation() { return coreLocation; }
    public void setCoreLocation(Location location) { this.coreLocation = location; }

    public double getTreasury() { return treasury; }
    public void addGold(double amount) { this.treasury += amount; }
    public void removeGold(double amount) { this.treasury -= amount; }

    public long getProtectionUntil() { return protectionUntil; }
}
