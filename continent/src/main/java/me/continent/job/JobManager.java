package me.continent.job;

import me.continent.ContinentPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/** Loads job definitions from jobs.yml and provides lookup. */
public class JobManager {
    private static final Map<String, Job> jobs = new LinkedHashMap<>();

    public static void load(ContinentPlugin plugin) {
        jobs.clear();
        File file = new File(plugin.getDataFolder(), "jobs.yml");
        if (!file.exists()) {
            plugin.saveResource("jobs.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String id : config.getKeys(false)) {
            String name = config.getString(id + ".name", id);
            String desc = config.getString(id + ".description", "");
            jobs.put(id.toLowerCase(), new Job(id, name, desc));
        }
    }

    public static Collection<Job> getAll() {
        return jobs.values();
    }

    public static Job get(String id) {
        if (id == null) return null;
        return jobs.get(id.toLowerCase());
    }
}
