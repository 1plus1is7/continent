package me.continent.village.service;

import me.continent.village.Village;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class VillageTreasuryListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof VillageTreasuryService.TreasuryHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Village village = holder.getVillage();
            int slot = event.getRawSlot();
            if (slot == 2) {
                VillageTreasuryService.promptDeposit(player, village);
                player.closeInventory();
            } else if (slot == 4) {
                VillageTreasuryService.promptWithdraw(player, village);
                player.closeInventory();
            }
        }
    }
}
