package me.continent.kingdom.service;

import me.continent.specialty.SpecialtyGood;
import me.continent.specialty.SpecialtyManager;
import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KingdomSpecialtyListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof KingdomSpecialtyService.SpecialtyHolder holder) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasCustomModelData()) return;
            int model = item.getItemMeta().getCustomModelData();
            SpecialtyGood good = SpecialtyManager.getByModel(model);
            if (good == null) return;
            Kingdom kingdom = holder.getKingdom();
            if (kingdom.getSpecialties().contains(good.getId())) {
                kingdom.getSpecialties().remove(good.getId());
            } else {
                kingdom.getSpecialties().add(good.getId());
            }
            ItemStack newItem = good.toItemStack(1);
            org.bukkit.inventory.meta.ItemMeta meta = newItem.getItemMeta();
            if (kingdom.getSpecialties().contains(good.getId())) {
                meta.setDisplayName("§a" + good.getName());
            } else {
                meta.setDisplayName("§c" + good.getName());
            }
            newItem.setItemMeta(meta);
            inv.setItem(event.getSlot(), newItem);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof KingdomSpecialtyService.SpecialtyHolder holder) {
            KingdomStorage.save(holder.getKingdom());
        }
    }
}
