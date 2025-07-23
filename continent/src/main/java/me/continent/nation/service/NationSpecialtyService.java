package me.continent.nation.service;

import me.continent.specialty.SpecialtyGood;
import me.continent.specialty.SpecialtyManager;
import me.continent.nation.Nation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NationSpecialtyService {
    public static void openMenu(Player player, Nation nation) {
        int size = ((SpecialtyManager.getAll().size() - 1) / 9 + 1) * 9;
        SpecialtyHolder holder = new SpecialtyHolder(nation);
        Inventory inv = Bukkit.createInventory(holder, size, "Nation Specialties");
        holder.setInventory(inv);
        int slot = 0;
        for (SpecialtyGood good : SpecialtyManager.getAll()) {
            ItemStack item = good.toItemStack(1);
            ItemMeta meta = item.getItemMeta();
            if (nation.getSpecialties().contains(good.getId())) {
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
        private final Nation nation;
        private Inventory inventory;

        SpecialtyHolder(Nation nation) {
            this.nation = nation;
        }

        void setInventory(Inventory inv) { this.inventory = inv; }
        @Override public Inventory getInventory() { return inventory; }
        public Nation getNation() { return nation; }
    }
}
