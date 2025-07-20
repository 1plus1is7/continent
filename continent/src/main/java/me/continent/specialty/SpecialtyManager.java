package me.continent.specialty;

import me.continent.ContinentPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class SpecialtyManager {
    private static final Map<String, SpecialtyGood> goods = new HashMap<>();

    public static void load(ContinentPlugin plugin) {
        goods.clear();
        File file = new File(plugin.getDataFolder(), "specialties.yml");
        if (!file.exists()) {
            plugin.saveResource("specialties.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String id : config.getKeys(false)) {
            String name = config.getString(id + ".name", id);
            List<String> lore = config.getStringList(id + ".lore");
            int model = config.getInt(id + ".model", 0);
            int hunger = config.getInt(id + ".hunger", 0);
            SpecialtyGood good = new SpecialtyGood(id, name, lore, model, hunger);
            goods.put(id.toLowerCase(), good);
        }
    }

    public static SpecialtyGood get(String id) {
        return goods.get(id.toLowerCase());
    }

    public static Collection<SpecialtyGood> getAll() {
        return goods.values();
    }
}
