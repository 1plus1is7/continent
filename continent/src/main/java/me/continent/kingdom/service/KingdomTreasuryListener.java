package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class KingdomTreasuryListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof KingdomTreasuryService.TreasuryHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            Kingdom kingdom = holder.getKingdom();
            if (slot == 2) {
                KingdomTreasuryService.promptDeposit(player, kingdom);
                player.closeInventory();
            } else if (slot == 4) {
                KingdomTreasuryService.promptWithdraw(player, kingdom);
                player.closeInventory();
            } else if (slot == 6) {
                KingdomTreasuryService.promptTax(player, kingdom);
                player.closeInventory();
            }
        }
    }
}
