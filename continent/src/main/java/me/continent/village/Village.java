package me.continent.village;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class Village {

    private String name;
    private final UUID king;
    private long protectionEnd = 0;
    private final Set<UUID> members = new HashSet<>();
    private final Set<String> claimedChunks = new HashSet<>();
    private Location spawnLocation;
    private Location coreLocation;
    private double vault;
    private long protectionUntil;


    private int maintenanceCount = 0;
    private int unpaidWeeks = 0;
    private long lastMaintenance = 0;

    // Whether members of this village can ignite fire or TNT in protected areas
    private boolean memberIgniteAllowed = false;


    private String coreChunkKey;
    private String spawnChunkKey;

    // 마을 창고 (27칸 단일 체스트)
    private org.bukkit.inventory.ItemStack[] chestContents = new org.bukkit.inventory.ItemStack[27];

    // Village symbol item
    private org.bukkit.inventory.ItemStack symbol;

    // 연구 및 특산품 데이터
    private final Set<String> researchedNodes = new HashSet<>();
    private final Set<String> specialties = new HashSet<>();
    private final Set<String> selectedResearchTrees = new HashSet<>();
    private final Set<String> selectedT4Nodes = new HashSet<>();
    private int researchSlots = 1;


    public Village(String name, UUID king) {
        this.name = name;
        this.king = king;
        this.members.add(king);
        this.vault = 0;
        this.protectionUntil = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L; // 7일
        this.symbol = new org.bukkit.inventory.ItemStack(org.bukkit.Material.WOODEN_SWORD);
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


    public Set<UUID> getMembers() { return members; }

    public Set<String> getClaimedChunks() { return claimedChunks; }


    public Location getSpawnLocation() { return spawnLocation; }

    public void setSpawnLocation(Location location) { this.spawnLocation = location; }

    public Location getCoreLocation() { return coreLocation; }

    public void setCoreLocation(Location location) { this.coreLocation = location; }

    public double getVault() { return vault; }

    public void setVault(double vault) { this.vault = vault; }

    public void addGold(double amount) { this.vault += amount; }

    public void removeGold(double amount) { this.vault -= amount; }

    public int getMaintenanceCount() { return maintenanceCount; }

    public void setMaintenanceCount(int count) { this.maintenanceCount = count; }

    public int getUnpaidWeeks() { return unpaidWeeks; }

    public void setUnpaidWeeks(int weeks) { this.unpaidWeeks = weeks; }

    public long getLastMaintenance() { return lastMaintenance; }

    public void setLastMaintenance(long time) { this.lastMaintenance = time; }


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

    public org.bukkit.inventory.ItemStack getSymbol() {
        return symbol;
    }

    public void setSymbol(org.bukkit.inventory.ItemStack symbol) {
        this.symbol = symbol;
    }

    // ---- 연구/특산품 관련 ----
    public Set<String> getResearchedNodes() {
        return researchedNodes;
    }

    public Set<String> getSpecialties() {
        return specialties;
    }

    public Set<String> getSelectedResearchTrees() {
        return selectedResearchTrees;
    }

    public Set<String> getSelectedT4Nodes() {
        return selectedT4Nodes;
    }

    public int getResearchSlots() {
        return researchSlots;
    }

    public void setResearchSlots(int slots) {
        this.researchSlots = slots;
    }


    public long getProtectionEnd() { return protectionEnd; }

    public void setProtectionEnd(long protectionEnd) { this.protectionEnd = protectionEnd; }

    public long getProtectionUntil() { return protectionUntil; }

    public boolean isUnderProtection() {
        return System.currentTimeMillis() < protectionUntil;
    }

    public boolean isMemberIgniteAllowed() {
        return memberIgniteAllowed;
    }

    public void setMemberIgniteAllowed(boolean allowed) {
        this.memberIgniteAllowed = allowed;
    }

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
        return true;
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