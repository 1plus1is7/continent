package me.continent.village.service;

import me.continent.village.Village;
import me.continent.storage.VillageStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ChestListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof ChestService.VillageChestHolder holder) {
            Village village = holder.getVillage();
            village.setChestContents(inv.getContents());
            VillageStorage.save(village);
        }
    }
}
