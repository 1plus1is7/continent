package me.continent.village.service;

import me.continent.specialty.SpecialtyGood;
import me.continent.specialty.SpecialtyManager;
import me.continent.storage.VillageStorage;
import me.continent.village.Village;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VillageSpecialtyListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof VillageSpecialtyService.SpecialtyHolder holder) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasCustomModelData()) return;
            int model = item.getItemMeta().getCustomModelData();
            SpecialtyGood good = SpecialtyManager.getByModel(model);
            if (good == null) return;
            Village village = holder.getVillage();
            if (village.getSpecialties().contains(good.getId())) {
                village.getSpecialties().remove(good.getId());
            } else {
                village.getSpecialties().add(good.getId());
            }
            ItemStack newItem = good.toItemStack(1);
            org.bukkit.inventory.meta.ItemMeta meta = newItem.getItemMeta();
            if (village.getSpecialties().contains(good.getId())) {
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
        if (inv.getHolder() instanceof VillageSpecialtyService.SpecialtyHolder holder) {
            VillageStorage.save(holder.getVillage());
        }
    }
}
