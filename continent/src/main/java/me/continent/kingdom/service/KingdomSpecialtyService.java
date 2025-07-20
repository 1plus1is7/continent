package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import me.continent.specialty.SpecialtyGood;
import me.continent.specialty.SpecialtyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KingdomSpecialtyService {
    public static void openMenu(Player player, Kingdom kingdom) {
        int size = ((SpecialtyManager.getAll().size() - 1) / 9 + 1) * 9;
        SpecialtyHolder holder = new SpecialtyHolder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, size, "Kingdom Specialties");
        holder.setInventory(inv);
        int slot = 0;
        for (SpecialtyGood good : SpecialtyManager.getAll()) {
            ItemStack item = good.toItemStack(1);
            ItemMeta meta = item.getItemMeta();
            if (kingdom.getSpecialties().contains(good.getId())) {
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
        private final Kingdom kingdom;
        private Inventory inventory;
        SpecialtyHolder(Kingdom kingdom) {
            this.kingdom = kingdom;
        }
        void setInventory(Inventory inv) { this.inventory = inv; }
        @Override
        public Inventory getInventory() { return inventory; }
        public Kingdom getKingdom() { return kingdom; }
    }
}
