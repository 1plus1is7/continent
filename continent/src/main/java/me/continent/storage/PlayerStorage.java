package me.continent.storage;

import me.continent.ContinentPlugin;
import me.continent.player.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

public class PlayerStorage {
    private static final File folder = new File(ContinentPlugin.getInstance().getDataFolder(), "players");

    static {
        if (!folder.exists()) folder.mkdirs();
    }

    public static void save(PlayerData data) {
        File file = new File(folder, data.getUuid().toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("uuid", data.getUuid().toString());
        config.set("gold", data.getGold());
        config.set("invites", new HashSet<>(data.getPendingInvites()));
        config.set("maintenance", data.getKnownMaintenance());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerData load(UUID uuid) {
        File file = new File(folder, uuid.toString() + ".yml");
        if (!file.exists()) return new PlayerData(uuid);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        PlayerData data = new PlayerData(uuid);
        data.setGold(config.getDouble("gold"));
        data.getPendingInvites().addAll(config.getStringList("invites"));
        data.setKnownMaintenance(config.getInt("maintenance", 0));
        return data;
    }
}
