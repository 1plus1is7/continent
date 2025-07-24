package me.continent.nation.service;

import me.continent.nation.Nation;
import me.continent.storage.NationStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ChestListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof ChestService.NationChestHolder holder) {
            Nation nation = holder.getNation();
            nation.setChestContents(inv.getContents());
            NationStorage.save(nation);
        }
    }
}
