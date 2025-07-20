package me.continent.kingdom;

import me.continent.ContinentPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.continent.village.VillageManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KingdomStorage {
    private static final File folder = new File(ContinentPlugin.getInstance().getDataFolder(), "kingdoms");

    static {
        if (!folder.exists()) folder.mkdirs();
    }

    public static void save(Kingdom kingdom) {
        File file = new File(folder, kingdom.getName().toLowerCase() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("name", kingdom.getName());
        config.set("leader", kingdom.getLeader().toString());
        config.set("capital", kingdom.getCapital());
        config.set("villages", new ArrayList<>(kingdom.getVillages()));
        config.set("treasury", kingdom.getTreasury());
        config.set("maintenanceCount", kingdom.getMaintenanceCount());
        config.set("unpaidWeeks", kingdom.getUnpaidWeeks());
        config.set("lastMaintenance", kingdom.getLastMaintenance());
        config.set("researched", new ArrayList<>(kingdom.getResearchedNodes()));
        config.set("specialties", new ArrayList<>(kingdom.getSpecialties()));

        Map<String, String> roleMap = new HashMap<>();
        for (Map.Entry<UUID, String> e : kingdom.getRoles().entrySet()) {
            roleMap.put(e.getKey().toString(), e.getValue());
        }
        config.set("roles", roleMap);

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
            UUID leader = UUID.fromString(config.getString("leader"));
            String capitalName = config.getString("capital");
            List<String> villages = config.getStringList("villages");
            double treasury = config.getDouble("treasury");
            int maintenanceCount = config.getInt("maintenanceCount", 0);
            int unpaidWeeks = config.getInt("unpaidWeeks", 0);
            long lastMaintenance = config.getLong("lastMaintenance", 0);
            List<String> researched = config.getStringList("researched");
            List<String> specialties = config.getStringList("specialties");
            Map<String, Object> rolesObj = config.getConfigurationSection("roles") != null ? config.getConfigurationSection("roles").getValues(false) : new HashMap<>();

            Kingdom kingdom = new Kingdom(name, leader, VillageManager.getByName(capitalName));
            kingdom.getVillages().addAll(villages);
            kingdom.setTreasury(treasury);
            kingdom.setMaintenanceCount(maintenanceCount);
            kingdom.setUnpaidWeeks(unpaidWeeks);
            kingdom.setLastMaintenance(lastMaintenance);
            kingdom.getResearchedNodes().addAll(researched);
            kingdom.getSpecialties().addAll(specialties);
            for (Map.Entry<String, Object> e : rolesObj.entrySet()) {
                kingdom.getRoles().put(UUID.fromString(e.getKey()), Objects.toString(e.getValue(), ""));
            }
            KingdomManager.register(kingdom);
        }
    }

    public static void saveAll() {
        for (Kingdom k : KingdomManager.getAll()) {
            save(k);
        }
    }

    public static void delete(Kingdom kingdom) {
        File file = new File(folder, kingdom.getName().toLowerCase() + ".yml");
        if (file.exists()) file.delete();
    }
}
