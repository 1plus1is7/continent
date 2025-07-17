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
        CentralBank.setExchangeRate(config.getDouble("exchangeRate", 20.0));
    }

    public static void save() {
        config.set("exchangeRate", CentralBank.getExchangeRate());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
