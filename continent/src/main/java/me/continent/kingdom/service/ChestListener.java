package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import me.continent.storage.KingdomStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ChestListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof ChestService.KingdomChestHolder holder) {
            Kingdom kingdom = holder.getKingdom();
            kingdom.setChestContents(inv.getContents());
            KingdomStorage.save(kingdom);
        }
    }
}
