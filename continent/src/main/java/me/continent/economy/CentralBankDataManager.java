package me.continent.economy;

import me.continent.ContinentPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CentralBankDataManager {

    private static File file;
    private static YamlConfiguration config;

    public static void load() {
        file = new File(ContinentPlugin.getInstance().getDataFolder(), "centralbank.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        CentralBank.setGold(config.getInt("gold", 0));
        CentralBank.setBaseRate(config.getDouble("baseRate", 20.0));
        CentralBank.setMinRate(config.getDouble("minRate", 10.0));
        CentralBank.setMaxRate(config.getDouble("maxRate", 50.0));
        CentralBank.setExchangeRate(config.getDouble("lastRate", 20.0));
        CentralBank.setAutoRate(config.getBoolean("autoRate", true));
    }

    public static void save() {
        config.set("gold", CentralBank.getGold());
        config.set("baseRate", CentralBank.getBaseRate());
        config.set("minRate", CentralBank.getMinRate());
        config.set("maxRate", CentralBank.getMaxRate());
        config.set("lastRate", CentralBank.getExchangeRate());
        config.set("autoRate", CentralBank.isAutoRate());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
