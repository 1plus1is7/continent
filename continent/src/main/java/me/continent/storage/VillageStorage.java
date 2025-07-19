package me.continent.storage;

import me.continent.ContinentPlugin;
import me.continent.village.Village;
import me.continent.village.VillageManager;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

public class VillageStorage {
    private static final File folder = new File(ContinentPlugin.getInstance().getDataFolder(), "villages");

    static {
        if (!folder.exists()) folder.mkdirs();
    }

    public static void delete(Village village) {
        File file = new File(folder, village.getName() + ".yml");
        if (file.exists()) file.delete();
    }

    public static void save(Village village) {
        File file = new File(folder, village.getName().toLowerCase() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("name", village.getName());
        config.set("king", village.getKing().toString());
        List<String> members = new ArrayList<>();
        for (UUID uuid : village.getMembers()) members.add(uuid.toString());
        config.set("members", members);
        config.set("core-chunk", village.getCoreChunk());
        config.set("spawn-chunk", village.getSpawnChunk());
        config.set("chunks", new ArrayList<>(village.getClaimedChunks()));
        config.set("spawn", serializeLocation(village.getSpawnLocation()));
        config.set("core", serializeLocation(village.getCoreLocation()));
        config.set("protectionEnd", village.getProtectionEnd());
        config.set("treasury", village.getTreasury());
        config.set("chest", serializeItems(village.getChestContents()));
        config.set("memberIgnite", village.isMemberIgniteAllowed());
        config.set("maintenanceCount", village.getMaintenanceCount());
        config.set("unpaidWeeks", village.getUnpaidWeeks());
        config.set("lastMaintenance", village.getLastMaintenance());
        config.set("nation", village.isNation());
        config.set("kingdom", village.getKingdom());
        config.set("kingdomInvites", new ArrayList<>(village.getKingdomInvites()));

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
            double treasury = config.getDouble("treasury");
            ItemStack[] chest = deserializeItems(config.getString("chest"));
            boolean memberIgnite = config.getBoolean("memberIgnite", false);
            int maintenanceCount = config.getInt("maintenanceCount", 0);
            int unpaidWeeks = config.getInt("unpaidWeeks", 0);
            long lastMaintenance = config.getLong("lastMaintenance", 0);
            boolean nation = config.getBoolean("nation", false);
            String kingdomName = config.getString("kingdom");
            List<String> kingdomInvites = config.getStringList("kingdomInvites");

            Village village = new Village(name, king);
            village.getMembers().addAll(members);
            village.getClaimedChunks().addAll(chunks);
            village.setSpawnLocation(spawn);
            village.setCoreLocation(core);
            village.setProtectionEnd(protectionEnd);
            village.setTreasury(treasury);
            village.setChestContents(chest);
            village.setMemberIgniteAllowed(memberIgnite);
            village.setMaintenanceCount(maintenanceCount);
            village.setUnpaidWeeks(unpaidWeeks);
            village.setLastMaintenance(lastMaintenance);
            village.setNation(nation);
            village.setKingdom(kingdomName);
            village.getKingdomInvites().addAll(kingdomInvites);
            village.setCoreChunkKey(config.getString("core-chunk"));
            village.setSpawnChunkKey(config.getString("spawn-chunk"));

            VillageManager.register(village);
        }
    }

    public static void savePlayerData(UUID playerUUID) {
        PlayerData data = PlayerDataManager.get(playerUUID);
        if (data != null) {
            PlayerDataManager.save(playerUUID);
        }
    }

    public static void saveVillageData(Village village) {
        save(village);
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
