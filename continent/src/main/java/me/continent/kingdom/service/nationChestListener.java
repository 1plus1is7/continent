package me.continent.kingdom.service;

import me.continent.kingdom.nation;
import me.continent.kingdom.nationStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class nationChestListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof nationChestService.nationChestHolder holder) {
            nation kingdom = holder.getnation();
            kingdom.setChestContents(inv.getContents());
            nationStorage.save(kingdom);
        }
    }
}
