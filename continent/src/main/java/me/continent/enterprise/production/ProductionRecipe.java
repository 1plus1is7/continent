package me.continent.enterprise.production;

import me.continent.enterprise.EnterpriseType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/** Recipe definition for enterprise production. */
public class ProductionRecipe {
    private final String id;
    private final String name;
    private final EnterpriseType type;
    private final ItemStack output;
    private final Map<Material, Integer> resources;
    private final int time; // seconds
    private final int amount;

    public ProductionRecipe(String id, String name, EnterpriseType type, ItemStack output,
                            Map<Material, Integer> resources, int time, int amount) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.output = output;
        this.resources = resources;
        this.time = time;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EnterpriseType getType() {
        return type;
    }

    public ItemStack getOutput() {
        return output.clone();
    }

    public Map<Material, Integer> getResources() {
        return resources;
    }

    public int getTime() {
        return time;
    }

    public int getAmount() {
        return amount;
    }
}
