package me.continent.storage;

import me.continent.ContinentPlugin;
import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import me.continent.utils.ItemSerialization;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NationStorage {
    private static final File folder = new File(ContinentPlugin.getInstance().getDataFolder(), "nations");

    static {
        if (!folder.exists()) folder.mkdirs();
    }

    public static void delete(Nation nation) {
        File file = new File(folder, nation.getName() + ".yml");
        if (file.exists()) file.delete();
    }

    public static void save(Nation nation) {
        File file = new File(folder, nation.getName().toLowerCase() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("name", nation.getName());
        config.set("king", nation.getKing().toString());
        List<String> members = new ArrayList<>();
        for (UUID uuid : nation.getMembers()) members.add(uuid.toString());
        config.set("members", members);
        config.set("core-chunk", nation.getCoreChunk());
        config.set("spawn-chunk", nation.getSpawnChunk());
        config.set("chunks", new ArrayList<>(nation.getClaimedChunks()));
        config.set("spawn", serializeLocation(nation.getSpawnLocation()));
        config.set("core", serializeLocation(nation.getCoreLocation()));
        config.set("protectionEnd", nation.getProtectionEnd());
        config.set("vault", nation.getVault());
        config.set("chest", serializeItems(nation.getChestContents()));
        config.set("symbol", ItemSerialization.serializeItem(nation.getSymbol()));
        config.set("memberIgnite", nation.isMemberIgniteAllowed());
        config.set("maintenanceCount", nation.getMaintenanceCount());
        config.set("unpaidWeeks", nation.getUnpaidWeeks());
        config.set("lastMaintenance", nation.getLastMaintenance());

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
            double vault = config.contains("vault") ? config.getDouble("vault") : config.getDouble("treasury");
            ItemStack[] chest = deserializeItems(config.getString("chest"));
            org.bukkit.inventory.ItemStack symbol = ItemSerialization.deserializeItem(config.getString("symbol"));
            boolean memberIgnite = config.getBoolean("memberIgnite", false);
            int maintenanceCount = config.getInt("maintenanceCount", 0);
            int unpaidWeeks = config.getInt("unpaidWeeks", 0);
            long lastMaintenance = config.getLong("lastMaintenance", 0);
            

            Nation nation = new Nation(name, king);
            nation.getMembers().addAll(members);
            nation.getClaimedChunks().addAll(chunks);
            nation.setSpawnLocation(spawn);
            nation.setCoreLocation(core);
            nation.setProtectionEnd(protectionEnd);
            nation.setVault(vault);
            nation.setChestContents(chest);
            if (symbol != null) {
                nation.setSymbol(symbol);
            }
            nation.setMemberIgniteAllowed(memberIgnite);
            nation.setMaintenanceCount(maintenanceCount);
            nation.setUnpaidWeeks(unpaidWeeks);
            nation.setLastMaintenance(lastMaintenance);
            nation.setCoreChunkKey(config.getString("core-chunk"));
            nation.setSpawnChunkKey(config.getString("spawn-chunk"));

            NationManager.register(nation);
        }
    }

    public static void savePlayerData(UUID playerUUID) {
        PlayerData data = PlayerDataManager.get(playerUUID);
        if (data != null) {
            PlayerDataManager.save(playerUUID);
        }
    }

    public static void saveNationData(Nation nation) {
        save(nation);
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

    public static String serializeItems(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ItemStack[] deserializeItems(String data) {
        if (data == null || data.isEmpty()) return new ItemStack[27];
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            int size = dataInput.readInt();
            ItemStack[] items = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ItemStack[27];
        }
    }
}
