package me.continent.specialty;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SpecialtyGood {
    private final String id;
    private final String name;
    private final List<String> lore;
    private final int modelData;
    private final int hunger;

    public SpecialtyGood(String id, String name, List<String> lore, int modelData, int hunger) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.modelData = modelData;
        this.hunger = hunger;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getModelData() {
        return modelData;
    }

    public int getHunger() {
        return hunger;
    }

    public ItemStack toItemStack(int amount) {
        ItemStack item = new ItemStack(Material.BREAD, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.setCustomModelData(modelData);
            item.setItemMeta(meta);
        }
        return item;
    }
}
