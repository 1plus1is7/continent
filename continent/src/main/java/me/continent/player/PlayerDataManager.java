package me.continent.player;

import me.continent.ContinentPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.Bukkit;
import java.io.IOException;
import java.util.*;

public class PlayerDataManager {

    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public static PlayerData get(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, PlayerDataStorage::load);
    }

    public static void save(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        if (data == null) return;

        File dir = new File(ContinentPlugin.getInstance().getDataFolder(), "players");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, uuid.toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("gold", data.getGold());
        config.set("invites", new ArrayList<>(data.getPendingInvites()));

        if (data.getKingdom() != null) {
            config.set("kingdom", data.getKingdom().getName());
        } else {
            config.set("kingdom", null);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("플레이어 데이터 저장 실패: " + file.getName());
        }
    }


    public static void saveAll() {
        for (PlayerData data : playerDataMap.values()) {
            File dir = new File(ContinentPlugin.getInstance().getDataFolder(), "players");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, data.getUuid().toString() + ".yml");
            YamlConfiguration config = new YamlConfiguration();
            config.set("gold", data.getGold());
            config.set("invites", new ArrayList<>(data.getPendingInvites()));

            try {
                config.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().warning("플레이어 데이터 저장 실패: " + file.getName());
            }
        }
    }

    public static void loadAll() {
        File dir = new File(ContinentPlugin.getInstance().getDataFolder(), "players");
        if (!dir.exists()) dir.mkdirs();

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.getName().endsWith(".yml")) continue;
            UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            PlayerData data = new PlayerData(uuid);
            data.setGold(config.getDouble("gold"));
            data.getPendingInvites().addAll(config.getStringList("invites"));
            playerDataMap.put(uuid, data);
        }
    }
}
