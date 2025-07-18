package me.continent.storage;

import me.continent.ContinentPlugin;
import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KingdomStorage {
    private static final File folder = new File(ContinentPlugin.getInstance().getDataFolder(), "kingdoms");

    static {
        if (!folder.exists()) folder.mkdirs();
    }

    public static void delete(Kingdom kingdom) {
        File file = new File(folder, kingdom.getName() + ".yml");
        if (file.exists()) file.delete();
    }

    public static void save(Kingdom kingdom) {
        File file = new File(folder, kingdom.getName().toLowerCase() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("name", kingdom.getName());
        config.set("king", kingdom.getKing().toString());
        List<String> members = new ArrayList<>();
        for (UUID uuid : kingdom.getMembers()) members.add(uuid.toString());
        config.set("members", members);
        config.set("core-chunk", kingdom.getCoreChunk());
        config.set("spawn-chunk", kingdom.getSpawnChunk());
        config.set("chunks", new ArrayList<>(kingdom.getClaimedChunks()));
        config.set("spawn", serializeLocation(kingdom.getSpawnLocation()));
        config.set("core", serializeLocation(kingdom.getCoreLocation()));
        config.set("protectionEnd", kingdom.getProtectionEnd());
        config.set("fund", kingdom.getFund());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAll() {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            String name = config.getString("name");
            UUID king = UUID.fromString(config.getString("king"));
            List<String> memberStrings = config.getStringList("members");
            Set<UUID> members = new HashSet<>();
            for (String m : memberStrings) members.add(UUID.fromString(m));
            List<String> chunks = config.getStringList("chunks");
            Location spawn = deserializeLocation(config.getString("spawn"));
            Location core = deserializeLocation(config.getString("core"));
            long protectionEnd = config.getLong("protectionEnd");
            double fund = config.getDouble("fund");

            Kingdom kingdom = new Kingdom(name, king);
            kingdom.getMembers().addAll(members);
            kingdom.getClaimedChunks().addAll(chunks);
            kingdom.setSpawnLocation(spawn);
            kingdom.setCoreLocation(core);
            kingdom.setProtectionEnd(protectionEnd);
            kingdom.setFund(fund);
            kingdom.setCoreChunkKey(config.getString("core-chunk"));
            kingdom.setSpawnChunkKey(config.getString("spawn-chunk"));

            KingdomManager.register(kingdom);
        }
    }

    public static void savePlayerData(UUID playerUUID) {
        PlayerData data = PlayerDataManager.get(playerUUID);
        if (data != null) {
            PlayerDataManager.save(playerUUID);
        }
    }

    public static void saveKingdomData(Kingdom kingdom) {
        save(kingdom);
    }

    public static void rename(String oldName, String newName) {
        File oldFile = new File(folder, oldName.toLowerCase() + ".yml");
        File newFile = new File(folder, newName.toLowerCase() + ".yml");
        if (oldFile.exists()) {
            oldFile.renameTo(newFile);
        }
    }

    public static String serializeLocation(Location loc) {
        if (loc == null) return null;
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    public static Location deserializeLocation(String str) {
        if (str == null) return null;
        String[] parts = str.split(",");
        return new Location(
                Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3])
        );
    }
}
