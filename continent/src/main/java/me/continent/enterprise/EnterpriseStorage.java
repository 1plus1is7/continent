package me.continent.enterprise;

import me.continent.ContinentPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/** Handles persistence of enterprises to YAML files. */
public class EnterpriseStorage {
    private static final File folder = new File(ContinentPlugin.getInstance().getDataFolder(), "enterprises");

    static {
        if (!folder.exists()) folder.mkdirs();
    }

    public static void save(Enterprise enterprise) {
        File file = new File(folder, enterprise.getId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("id", enterprise.getId());
        config.set("name", enterprise.getName());
        config.set("type", enterprise.getType().name());
        config.set("owner", enterprise.getOwner().toString());
        config.set("registeredAt", enterprise.getRegisteredAt());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Enterprise load(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String id = config.getString("id", file.getName().replace(".yml", ""));
        String name = config.getString("name", id);
        EnterpriseType type = EnterpriseType.valueOf(config.getString("type"));
        UUID owner = UUID.fromString(config.getString("owner"));
        long registeredAt = config.getLong("registeredAt", System.currentTimeMillis());
        return new Enterprise(id, name, type, owner, registeredAt);
    }

    public static void loadAll() {
        EnterpriseManager.clear();
        File[] files = folder.listFiles((dir, n) -> n.endsWith(".yml"));
        if (files == null) return;
        for (File file : files) {
            Enterprise e = load(file);
            EnterpriseManager.register(e);
        }
    }

    public static void saveAll() {
        for (Enterprise e : EnterpriseManager.getAll()) {
            save(e);
        }
    }
}
