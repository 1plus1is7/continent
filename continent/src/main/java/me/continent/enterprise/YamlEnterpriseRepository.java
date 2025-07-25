package me.continent.enterprise;

import me.continent.ContinentPlugin;
import me.continent.utils.ItemSerialization;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/** YAML-based implementation of EnterpriseRepository. */
public class YamlEnterpriseRepository implements EnterpriseRepository {
    private final File folder;

    public YamlEnterpriseRepository(ContinentPlugin plugin) {
        this.folder = new File(plugin.getDataFolder(), "enterprises");
        if (!folder.exists()) folder.mkdirs();
    }

    @Override
    public void save(Enterprise enterprise) {
        File file = new File(folder, enterprise.getId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("id", enterprise.getId());
        config.set("name", enterprise.getName());
        config.set("type", enterprise.getType().name());
        config.set("owner", enterprise.getOwner().toString());
        config.set("registeredAt", enterprise.getRegisteredAt());
        config.set("symbol", ItemSerialization.serializeItem(enterprise.getSymbol()));
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Enterprise load(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String id = config.getString("id", file.getName().replace(".yml", ""));
        String name = config.getString("name", id);
        EnterpriseType type = EnterpriseType.valueOf(config.getString("type"));
        UUID owner = UUID.fromString(config.getString("owner"));
        long registeredAt = config.getLong("registeredAt", System.currentTimeMillis());
        Enterprise ent = new Enterprise(id, name, type, owner, registeredAt);
        var symbol = ItemSerialization.deserializeItem(config.getString("symbol"));
        if (symbol != null) ent.setSymbol(symbol);
        return ent;
    }

    @Override
    public void loadAll() {
        EnterpriseManager.clear();
        File[] files = folder.listFiles((dir, n) -> n.endsWith(".yml"));
        if (files == null) return;
        for (File file : files) {
            Enterprise e = load(file);
            EnterpriseManager.register(e);
        }
    }

    @Override
    public void saveAll() {
        for (Enterprise e : EnterpriseManager.getAll()) {
            save(e);
        }
    }
}
