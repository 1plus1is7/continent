package me.continent.enterprise.contract;

import me.continent.ContinentPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/** Handles persistence of contracts to YAML files. */
public class ContractStorage {
    private static final File folder = new File(ContinentPlugin.getInstance().getDataFolder(), "contracts");

    static {
        if (!folder.exists()) folder.mkdirs();
    }

    public static void save(Contract c) {
        File file = new File(folder, c.getId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("id", c.getId().toString());
        config.set("issuer", c.getIssuer());
        config.set("desc", c.getDescription());
        config.set("item", c.getItem());
        config.set("amount", c.getAmount());
        config.set("reward", c.getReward());
        config.set("due", c.getDueTime());
        config.set("grade", c.getGrade().name());
        config.set("state", c.getState().name());
        config.set("enterprise", c.getEnterpriseId());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Contract load(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        UUID id = UUID.fromString(config.getString("id", file.getName().replace(".yml", "")));
        String issuer = config.getString("issuer", "SYSTEM");
        String desc = config.getString("desc", "");
        String item = config.getString("item", "ITEM");
        int amount = config.getInt("amount", 1);
        int reward = config.getInt("reward", 0);
        long due = config.getLong("due", System.currentTimeMillis());
        ContractGrade grade = ContractGrade.valueOf(config.getString("grade", "D"));
        Contract c = new Contract(id, issuer, desc, item, amount, reward, due, grade);
        c.setState(ContractState.valueOf(config.getString("state", "AVAILABLE")));
        c.setEnterpriseId(config.getString("enterprise", null));
        return c;
    }

    public static void loadAll() {
        ContractManager.clearAll();
        File[] files = folder.listFiles((d, n) -> n.endsWith(".yml"));
        if (files == null) return;
        for (File f : files) {
            Contract c = load(f);
            if (c.getState() == ContractState.AVAILABLE) {
                ContractManager.addAvailable(c);
            } else {
                String ent = c.getEnterpriseId();
                if (ent != null) {
                    ContractManager.addToEnterprise(ent, c);
                }
            }
        }
    }
}
