package me.continent.player;

import me.continent.ContinentPlugin;
import me.continent.storage.PlayerStorage;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.*;

public class PlayerDataManager {

    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public static PlayerData get(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, PlayerStorage::load);
    }

    public static void save(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        if (data == null) return;

        PlayerStorage.save(data);
    }


    public static void saveAll() {
        for (PlayerData data : playerDataMap.values()) {
            PlayerStorage.save(data);
        }
    }

    public static void loadAll() {
        File dir = new File(ContinentPlugin.getInstance().getDataFolder(), "players");
        if (!dir.exists()) dir.mkdirs();

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.getName().endsWith(".yml")) continue;
            UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
            PlayerData data = PlayerStorage.load(uuid);
            playerDataMap.put(uuid, data);
        }
    }
}
