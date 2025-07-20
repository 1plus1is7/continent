package me.continent.season;

import me.continent.ContinentPlugin;
import org.bukkit.*;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SeasonLeafManager {
    private static final Set<Location> leafLocations = ConcurrentHashMap.newKeySet();
    private static File file;

    static void init(ContinentPlugin plugin) {
        file = new File(plugin.getDataFolder(), "leaves.yml");
        load();
    }

    private static void load() {
        if (!file.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String s : cfg.getStringList("leaves")) {
            String[] parts = s.split(",");
            World world = Bukkit.getWorld(UUID.fromString(parts[0]));
            if (world != null) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int z = Integer.parseInt(parts[3]);
                leafLocations.add(new Location(world, x, y, z));
            }
        }
    }

    static void save() {
        FileConfiguration cfg = new YamlConfiguration();
        List<String> list = new ArrayList<>();
        for (Location loc : leafLocations) {
            list.add(loc.getWorld().getUID() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
        }
        cfg.set("leaves", list);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void generateLeavesAsync(Collection<Chunk> chunks, Set<ChunkCoord> processed) {
        ContinentPlugin plugin = ContinentPlugin.getInstance();
        for (Chunk chunk : chunks) {
            ChunkCoord coord = new ChunkCoord(chunk);
            if (!processed.add(coord)) continue;
            var snapshot = chunk.getChunkSnapshot();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> scanSnapshot(snapshot, chunk.getWorld()));
        }
    }

    private static void scanSnapshot(ChunkSnapshot snapshot, World world) {
        List<Location> found = new ArrayList<>();
        int baseX = snapshot.getX() << 4;
        int baseZ = snapshot.getZ() << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = snapshot.getHighestBlockYAt(x, z);
                Material below = snapshot.getBlockType(x, y - 1, z);
                if (below.name().endsWith("_LOG")) {
                    Material above = snapshot.getBlockType(x, y, z);
                    if (above == Material.AIR) {
                        found.add(new Location(world, baseX + x, y, baseZ + z));
                    }
                }
            }
        }
        if (!found.isEmpty()) {
            Bukkit.getScheduler().runTask(ContinentPlugin.getInstance(), () -> {
                for (Location loc : found) {
                    Block b = loc.getBlock();
                    b.setType(Material.LEAF_LITTER);
                    leafLocations.add(b.getLocation());
                }
            });
        }
    }

    static void removeLeafPiles() {
        Bukkit.getScheduler().runTask(ContinentPlugin.getInstance(), () -> {
            for (Location loc : leafLocations) {
                Block b = loc.getBlock();
                if (b.getType() == Material.LEAF_LITTER) {
                    b.setType(Material.AIR);
                }
            }
        });
    }

    static void spawnLeavesFromPiles() {
        Bukkit.getScheduler().runTask(ContinentPlugin.getInstance(), () -> {
            for (Location loc : leafLocations) {
                Block b = loc.getBlock();
                if (b.getType() == Material.AIR || b.getType() == Material.LEAF_LITTER) {
                    b.setType(Material.OAK_LEAVES);
                }
            }
        });
    }

    static void setLeavesPersistent() {
        Bukkit.getScheduler().runTask(ContinentPlugin.getInstance(), () -> {
            Leaves data = (Leaves) Material.OAK_LEAVES.createBlockData();
            data.setPersistent(true);
            for (Location loc : leafLocations) {
                Block b = loc.getBlock();
                if (b.getType() == Material.OAK_LEAVES) {
                    b.setBlockData(data, false);
                }
            }
        });
    }

    static Collection<Location> getLeaves() {
        return leafLocations;
    }
}
