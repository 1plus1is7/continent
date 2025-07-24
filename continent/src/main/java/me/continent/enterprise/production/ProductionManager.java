package me.continent.enterprise.production;

import me.continent.ContinentPlugin;
import me.continent.enterprise.EnterpriseType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

/** Handles production recipes and active jobs. */
public class ProductionManager {
    private static final Map<EnterpriseType, List<ProductionRecipe>> recipes = new EnumMap<>(EnterpriseType.class);
    private static final Map<String, ProductionJob> activeJobs = new HashMap<>();

    public static void load(ContinentPlugin plugin) {
        recipes.clear();
        File file = new File(plugin.getDataFolder(), "enterprise_products.yml");
        if (!file.exists()) plugin.saveResource("enterprise_products.yml", false);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (EnterpriseType type : EnterpriseType.values()) {
            String path = type.name();
            if (!config.contains(path)) continue;
            var section = config.getConfigurationSection(path);
            if (section == null) continue;
            List<ProductionRecipe> list = new ArrayList<>();
            for (String id : section.getKeys(false)) {
                var cs = section.getConfigurationSection(id);
                if (cs == null) continue;
                String name = cs.getString("name", id);
                String mat = cs.getString("output", "STONE");
                ItemStack out = new ItemStack(Material.matchMaterial(mat));
                int time = cs.getInt("time", 10);
                int amount = cs.getInt("amount", 1);
                Map<Material, Integer> res = new HashMap<>();
                var rs = cs.getConfigurationSection("resources");
                if (rs != null)
                    for (String k : rs.getKeys(false))
                        res.put(Material.matchMaterial(k), rs.getInt(k));
                list.add(new ProductionRecipe(id, name, type, out, res, time, amount));
            }
            recipes.put(type, list);
        }
    }

    public static List<ProductionRecipe> getRecipes(EnterpriseType type) {
        return recipes.getOrDefault(type, Collections.emptyList());
    }

    /** Return active job for enterprise or null. */
    public static ProductionJob getJob(String enterpriseId) {
        return activeJobs.get(enterpriseId);
    }

    /** Attempt to start production for player enterprise. */
    public static boolean startJob(Player player, String enterpriseId, ProductionRecipe recipe) {
        if (activeJobs.containsKey(enterpriseId)) return false; // slot busy
        // check resources in player inventory
        for (var e : recipe.getResources().entrySet()) {
            if (!player.getInventory().containsAtLeast(new ItemStack(e.getKey()), e.getValue())) {
                return false;
            }
        }
        // remove resources
        for (var e : recipe.getResources().entrySet()) {
            player.getInventory().removeItem(new ItemStack(e.getKey(), e.getValue()));
        }
        ProductionJob job = new ProductionJob(enterpriseId, recipe, System.currentTimeMillis());
        activeJobs.put(enterpriseId, job);
        return true;
    }

    /** Collect completed production. */
    public static boolean collect(Player player, String enterpriseId) {
        ProductionJob job = activeJobs.get(enterpriseId);
        if (job == null || !job.isComplete() || job.isCollected()) return false;
        ItemStack out = job.getRecipe().getOutput();
        out.setAmount(job.getRecipe().getAmount());
        player.getInventory().addItem(out);
        job.setCollected();
        activeJobs.remove(enterpriseId);
        return true;
    }

    /** Schedule periodic checks to notify players. */
    public static void schedule() {
        Bukkit.getScheduler().runTaskTimer(ContinentPlugin.getInstance(), () -> {
            for (ProductionJob job : activeJobs.values()) {
                if (job.isComplete() && !job.isCollected()) {
                    // simply keep job until player collects
                }
            }
        }, 20L, 20L);
    }
}
