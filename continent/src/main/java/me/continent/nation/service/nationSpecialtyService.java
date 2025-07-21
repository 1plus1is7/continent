package me.continent.nation.service;

import me.continent.nation.nation;
import me.continent.specialty.SpecialtyGood;
import me.continent.specialty.SpecialtyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class nationSpecialtyService {
    public static void openMenu(Player player, nation kingdom) {
        int size = ((SpecialtyManager.getAll().size() - 1) / 9 + 1) * 9;
        SpecialtyHolder holder = new SpecialtyHolder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, size, "nation Specialties");
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
        private final nation kingdom;
        private Inventory inventory;
        SpecialtyHolder(nation kingdom) {
            this.kingdom = kingdom;
        }
        void setInventory(Inventory inv) { this.inventory = inv; }
        @Override
        public Inventory getInventory() { return inventory; }
        public nation getnation() { return kingdom; }
    }
}
