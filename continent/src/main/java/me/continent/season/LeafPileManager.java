package me.continent.season;

import me.continent.ContinentPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LeafPileManager {
    private static final Set<Location> piles = ConcurrentHashMap.newKeySet();
    private static File file;
    private static int taskId = -1;
    private static final Set<ChunkCoord> processed = ConcurrentHashMap.newKeySet();

    static void init(ContinentPlugin plugin) {
        file = new File(plugin.getDataFolder(), "leafpiles.yml");
        load();
    }

    static void save() {
        FileConfiguration cfg = new YamlConfiguration();
        List<String> list = new ArrayList<>();
        for (Location loc : piles) {
            list.add(loc.getWorld().getUID() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
        }
        cfg.set("piles", list);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load() {
        if (!file.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String s : cfg.getStringList("piles")) {
            String[] p = s.split(",");
            World world = Bukkit.getWorld(UUID.fromString(p[0]));
            if (world == null) continue;
            int x = Integer.parseInt(p[1]);
            int y = Integer.parseInt(p[2]);
            int z = Integer.parseInt(p[3]);
            piles.add(new Location(world, x, y, z));
        }
    }

    static void start() {
        stop();
        processed.clear();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                ContinentPlugin.getInstance(), LeafPileManager::scanChunks, 0L, 200L);
    }

    static void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    static void removeAll() {
        stop();
        Bukkit.getScheduler().runTask(ContinentPlugin.getInstance(), () -> {
            for (Location loc : piles) {
                Block b = loc.getBlock();
                if (b.getType() == Material.LEAF_LITTER) {
                    b.setType(Material.AIR);
                }
            }
        });
        piles.clear();
    }

    private static void scanChunks() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                ChunkCoord coord = new ChunkCoord(chunk);
                if (!processed.add(coord)) continue;
                ChunkSnapshot snapshot = chunk.getChunkSnapshot();
                Bukkit.getScheduler().runTaskAsynchronously(ContinentPlugin.getInstance(), () -> scanSnapshot(snapshot, world));
            }
        }
    }

    private static void scanSnapshot(ChunkSnapshot snap, World world) {
        List<Location> toPlace = new ArrayList<>();
        int baseX = snap.getX() << 4;
        int baseZ = snap.getZ() << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = snap.getHighestBlockYAt(x, z); y > 0; y--) {
                    Material type = snap.getBlockType(x, y, z);
                    if (type == Material.AIR) continue;
                    if (type.name().endsWith("_LEAVES")) {
                        if (!snap.getBlockData(x, y, z).getMaterial().isBlock()) continue;
                        BlockData data = snap.getBlockData(x, y, z);
                        if (data instanceof Leaves leaves && !leaves.isPersistent()) {
                            for (int dx = -2; dx <= 2; dx++) {
                                for (int dz = -2; dz <= 2; dz++) {
                                    int gx = x + dx;
                                    int gz = z + dz;
                                    if (gx < 0 || gx >= 16 || gz < 0 || gz >= 16) continue;
                                    int gy = snap.getHighestBlockYAt(gx, gz);
                                    Material ground = snap.getBlockType(gx, gy, gz);
                                    if (ground == Material.DIRT || ground == Material.GRASS_BLOCK) {
                                        Material up = snap.getBlockType(gx, gy + 1, gz);
                                        if (up == Material.AIR && Math.random() < 0.05) {
                                            toPlace.add(new Location(world, baseX + gx, gy + 1, baseZ + gz));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!toPlace.isEmpty()) {
            Bukkit.getScheduler().runTask(ContinentPlugin.getInstance(), () -> {
                for (Location loc : toPlace) {
                    Block b = loc.getBlock();
                    if (b.getType() == Material.AIR) {
                        b.setType(Material.LEAF_LITTER);
                        piles.add(b.getLocation());
                    }
                }
            });
        }
    }

    static Collection<Location> getPiles() {
        return piles;
    }
}
