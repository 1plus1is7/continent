package me.continent.storage;

import me.continent.ContinentPlugin;
import me.continent.union.Union;
import me.continent.union.UnionManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Handles saving and loading of unions from unions.yml
 */
public class UnionStorage {
    private static final File file = new File(ContinentPlugin.getInstance().getDataFolder(), "unions.yml");

    public static void saveAll() {
        FileConfiguration config = new YamlConfiguration();
        for (Union u : UnionManager.getAll()) {
            String key = u.getName();
            config.set(key + ".description", u.getDescription());
            config.set(key + ".leader", u.getLeader());
            config.set(key + ".nations", new ArrayList<>(u.getNations()));
            Map<String, List<String>> roleMap = new HashMap<>();
            for (var e : u.getRoles().entrySet()) {
                roleMap.put(e.getKey(), new ArrayList<>(e.getValue()));
            }
            config.set(key + ".roles", roleMap);
            config.set(key + ".treasury", u.getTreasury());
            config.set(key + ".invites", new ArrayList<>(u.getInvites()));
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAll() {
        if (!file.exists()) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            String leader = config.getString(key + ".leader");
            if (leader == null) continue;
            Union u = new Union(key, leader);
            u.setDescription(config.getString(key + ".description", ""));
            u.setTreasury(config.getDouble(key + ".treasury", 0));
            u.getNations().clear();
            u.getNations().addAll(config.getStringList(key + ".nations"));
            Map<String, Object> roleMap = config.getConfigurationSection(key + ".roles") != null
                    ? config.getConfigurationSection(key + ".roles").getValues(false)
                    : Collections.emptyMap();
            for (var e : roleMap.entrySet()) {
                List<String> list = config.getStringList(key + ".roles." + e.getKey());
                u.getRoles().put(e.getKey(), new HashSet<>(list));
            }
            u.getInvites().addAll(config.getStringList(key + ".invites"));
            UnionManager.register(u);
        }
    }
}
