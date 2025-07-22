package me.continent.village.service;

import me.continent.specialty.SpecialtyGood;
import me.continent.specialty.SpecialtyManager;
import me.continent.village.Village;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VillageSpecialtyService {
    public static void openMenu(Player player, Village village) {
        int size = ((SpecialtyManager.getAll().size() - 1) / 9 + 1) * 9;
        SpecialtyHolder holder = new SpecialtyHolder(village);
        Inventory inv = Bukkit.createInventory(holder, size, "Village Specialties");
        holder.setInventory(inv);
        int slot = 0;
        for (SpecialtyGood good : SpecialtyManager.getAll()) {
            ItemStack item = good.toItemStack(1);
            ItemMeta meta = item.getItemMeta();
            if (village.getSpecialties().contains(good.getId())) {
                meta.setDisplayName("§a" + good.getName());
            } else {
                meta.setDisplayName("§c" + good.getName());
            }
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }
        player.openInventory(inv);
    }

    static class SpecialtyHolder implements InventoryHolder {
        private final Village village;
        private Inventory inventory;

        SpecialtyHolder(Village village) {
            this.village = village;
        }

        void setInventory(Inventory inv) { this.inventory = inv; }
        @Override public Inventory getInventory() { return inventory; }
        public Village getVillage() { return village; }
    }
}
