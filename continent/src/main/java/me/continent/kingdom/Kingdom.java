package me.continent.kingdom;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class Kingdom {

    private String name;
    private final UUID king;
    private long protectionEnd = 0;
    private final Set<UUID> members = new HashSet<>();
    private final Set<String> claimedChunks = new HashSet<>();
    private Location spawnLocation;
    private Location coreLocation;
    private double treasury;
    private long protectionUntil;

    // Nation color in hex format (e.g. #FF0000)
    private String color = "#ADFF2F";

    private String coreChunkKey;
    private String spawnChunkKey;

    // 국가 창고 (27칸 단일 체스트)
    private org.bukkit.inventory.ItemStack[] chestContents = new org.bukkit.inventory.ItemStack[27];


    public Kingdom(String name, UUID king) {
        this.name = name;
        this.king = king;
        this.members.add(king);
        this.treasury = 0;
        this.protectionUntil = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L; // 7일
    }

    public static Location getGroundLocation(Location loc) {
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        int y = (world.getHighestBlockYAt(x, z)+1); // 지면 위
        return new Location(world, x + 0.5, y, z + 0.5); // 중앙 보정
    }


    // ---- 청크 키 유틸 ----
    public static String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }

    public void setSpawnChunkKey(String key) {
        this.spawnChunkKey = key;
    }

    public void setCoreChunkKey(String key) {
        this.coreChunkKey = key;
    }


    // ---- 초기화 및 Setter ----
    public void setCoreChunk(Chunk chunk) {
        this.coreChunkKey = getChunkKey(chunk);
    }

    public void setSpawnChunk(Chunk chunk) {
        this.spawnChunkKey = getChunkKey(chunk);
    }

    // ---- Getter ----
    public String getCoreChunk() {
        return this.coreChunkKey;
    }

    public String getSpawnChunk() {
        return this.spawnChunkKey;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public UUID getKing() { return king; }

    public boolean isAuthorized(UUID uuid) {
        return king.equals(uuid);
    }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public Set<UUID> getMembers() { return members; }

    public Set<String> getClaimedChunks() { return claimedChunks; }

    public Location getSpawnLocation() { return spawnLocation; }

    public void setSpawnLocation(Location location) { this.spawnLocation = location; }

    public Location getCoreLocation() { return coreLocation; }

    public void setCoreLocation(Location location) { this.coreLocation = location; }

    public double getTreasury() { return treasury; }

    public void setTreasury(double treasury) { this.treasury = treasury; }

    public void addGold(double amount) { this.treasury += amount; }

    public void removeGold(double amount) { this.treasury -= amount; }

    public org.bukkit.inventory.ItemStack[] getChestContents() {
        return chestContents;
    }

    public void setChestContents(org.bukkit.inventory.ItemStack[] items) {
        if (items == null) {
            this.chestContents = new org.bukkit.inventory.ItemStack[27];
        } else {
            this.chestContents = java.util.Arrays.copyOf(items, 27);
        }
    }


    public long getProtectionEnd() { return protectionEnd; }

    public void setProtectionEnd(long protectionEnd) { this.protectionEnd = protectionEnd; }

    public long getProtectionUntil() { return protectionUntil; }

    // ---- 기능성 메서드 ----
    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public void addChunk(Chunk chunk) {
        claimedChunks.add(getChunkKey(chunk));
    }

    public void removeChunk(Chunk chunk) {
        claimedChunks.remove(getChunkKey(chunk));
    }

    public boolean hasChunk(Chunk chunk) {
        return claimedChunks.contains(getChunkKey(chunk));
    }

    public boolean isVillage() {
        return claimedChunks.size() <= 16;
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

            if ((Math.abs(cx - x) == 1 && cz == z) || (Math.abs(cz - z) == 1 && cx == x)) {
                return true;
            }
        }

        return false;
    }
}