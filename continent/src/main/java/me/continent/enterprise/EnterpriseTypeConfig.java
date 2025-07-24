package me.continent.enterprise;

import me.continent.ContinentPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

/** Loads enterprise type info from enterprise_types.yml. */
public class EnterpriseTypeConfig {
    private static final Map<EnterpriseType, EnterpriseTypeInfo> map = new EnumMap<>(EnterpriseType.class);

    public static void load(ContinentPlugin plugin) {
        map.clear();
        File file = new File(plugin.getDataFolder(), "enterprise_types.yml");
        if (!file.exists()) {
            plugin.saveResource("enterprise_types.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (EnterpriseType type : EnterpriseType.values()) {
            String path = type.name();
            String name = config.getString(path + ".name", type.name());
            double cost = config.getDouble(path + ".cost", 100);
            map.put(type, new EnterpriseTypeInfo(type, name, cost));
        }
    }

    public static EnterpriseTypeInfo get(EnterpriseType type) {
        return map.get(type);
    }
}
