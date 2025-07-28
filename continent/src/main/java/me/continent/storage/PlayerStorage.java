package me.continent.storage;

import me.continent.ContinentPlugin;
import me.continent.player.PlayerData;
import me.continent.stat.PlayerStats;
import me.continent.stat.StatType;
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
        config.set("kingdomChat", data.isKingdomChatEnabled());
        config.set("job", data.getJobId());
        PlayerStats stats = data.getStats();
        for (StatType type : StatType.values()) {
            config.set("stats." + type.name().toLowerCase(), stats.get(type));
        }
        if (stats.getMastery() != null) {
            config.set("stats.mastery", stats.getMastery().name());
        }
        config.set("stats.points", stats.getUnusedPoints());
        config.set("stats.lastLevel", stats.getLastLevelGiven());

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
        data.setKingdomChatEnabled(config.getBoolean("kingdomChat", false));
        data.setJobId(config.getString("job", null));

        PlayerStats stats = data.getStats();
        for (StatType type : StatType.values()) {
            int val = config.getInt("stats." + type.name().toLowerCase(), 0);
            stats.set(type, val);
        }
        String mastery = config.getString("stats.mastery", null);
        if (mastery != null) {
            try {
                stats.setMastery(StatType.valueOf(mastery));
            } catch (IllegalArgumentException ignored) {}
        }
        stats.addPoints(config.getInt("stats.points", 0));
        stats.setLastLevelGiven(config.getInt("stats.lastLevel", 0));
        return data;
    }
}
