package me.continent.nation;

import me.continent.ContinentPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.continent.village.VillageManager;
import me.continent.utils.ItemSerialization;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class nationStorage {
    private static final File folder = new File(ContinentPlugin.getInstance().getDataFolder(), "kingdoms");

    static {
        if (!folder.exists()) folder.mkdirs();
    }

    public static void save(nation kingdom) {
        File file = new File(folder, kingdom.getName().toLowerCase() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("name", kingdom.getName());
        config.set("leader", kingdom.getLeader().toString());
        config.set("capital", kingdom.getCapital());
        config.set("villages", new ArrayList<>(kingdom.getVillages()));
        config.set("treasury", kingdom.getTreasury());
        config.set("description", kingdom.getDescription());
        config.set("chest", me.continent.storage.VillageStorage.serializeItems(kingdom.getChestContents()));
        config.set("taxRate", kingdom.getTaxRate());
        config.set("territoryProtection", kingdom.isTerritoryProtectionEnabled());
        config.set("maintenanceCount", kingdom.getMaintenanceCount());
        config.set("unpaidWeeks", kingdom.getUnpaidWeeks());
        config.set("lastMaintenance", kingdom.getLastMaintenance());
        config.set("flag", ItemSerialization.serializeItem(kingdom.getFlag()));
        config.set("researched", new ArrayList<>(kingdom.getResearchedNodes()));
        config.set("specialties", new ArrayList<>(kingdom.getSpecialties()));
        config.set("researchSlots", kingdom.getResearchSlots());
        config.set("selectedTrees", new ArrayList<>(kingdom.getSelectedResearchTrees()));
        config.set("selectedT4", new ArrayList<>(kingdom.getSelectedT4Nodes()));

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
            String description = config.getString("description", "");
            org.bukkit.inventory.ItemStack[] chest = me.continent.storage.VillageStorage.deserializeItems(config.getString("chest"));
            double taxRate = config.getDouble("taxRate", 0);
            boolean territoryProtection = config.getBoolean("territoryProtection", true);
            int maintenanceCount = config.getInt("maintenanceCount", 0);
            int unpaidWeeks = config.getInt("unpaidWeeks", 0);
            long lastMaintenance = config.getLong("lastMaintenance", 0);
            List<String> researched = config.getStringList("researched");
            List<String> specialties = config.getStringList("specialties");
            int researchSlots = config.getInt("researchSlots", 1);
            List<String> selectedTrees = config.getStringList("selectedTrees");
            List<String> selectedT4 = config.getStringList("selectedT4");
            org.bukkit.inventory.ItemStack flag = ItemSerialization.deserializeItem(config.getString("flag"));
            Map<String, Object> rolesObj = config.getConfigurationSection("roles") != null ? config.getConfigurationSection("roles").getValues(false) : new HashMap<>();

            nation kingdom = new nation(name, leader, VillageManager.getByName(capitalName));
            kingdom.getVillages().addAll(villages);
            kingdom.setTreasury(treasury);
            kingdom.setDescription(description);
            kingdom.setChestContents(chest);
            kingdom.setTaxRate(taxRate);
            kingdom.setTerritoryProtectionEnabled(territoryProtection);
            kingdom.setMaintenanceCount(maintenanceCount);
            kingdom.setUnpaidWeeks(unpaidWeeks);
            kingdom.setLastMaintenance(lastMaintenance);
            kingdom.getResearchedNodes().addAll(researched);
            kingdom.getSpecialties().addAll(specialties);
            kingdom.setResearchSlots(researchSlots);
            if (flag != null) {
                kingdom.setFlag(flag);
            }
            kingdom.getSelectedResearchTrees().addAll(selectedTrees);
            kingdom.getSelectedT4Nodes().addAll(selectedT4);
            for (Map.Entry<String, Object> e : rolesObj.entrySet()) {
                kingdom.getRoles().put(UUID.fromString(e.getKey()), Objects.toString(e.getValue(), ""));
            }
            nationManager.register(kingdom);
        }
    }

    public static void saveAll() {
        for (nation k : nationManager.getAll()) {
            save(k);
        }
    }

    public static void delete(nation kingdom) {
        File file = new File(folder, kingdom.getName().toLowerCase() + ".yml");
        if (file.exists()) file.delete();
    }

    public static void rename(String oldName, String newName) {
        File oldFile = new File(folder, oldName.toLowerCase() + ".yml");
        File newFile = new File(folder, newName.toLowerCase() + ".yml");
        if (oldFile.exists()) {
            oldFile.renameTo(newFile);
        }
    }
}
