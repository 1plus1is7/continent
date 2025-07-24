package me.continent.enterprise.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/** Handles clicks in the delivery status GUI. */
public class DeliveryMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof DeliveryStatusGUI.Holder) {
            event.setCancelled(true);
            // In future, could open detailed logs.
        }
    }
}
