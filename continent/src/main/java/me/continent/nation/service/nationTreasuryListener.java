package me.continent.nation.service;

import me.continent.nation.nation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class nationTreasuryListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof nationTreasuryService.TreasuryHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            nation kingdom = holder.getnation();
            if (slot == 2) {
                nationTreasuryService.promptDeposit(player, kingdom);
                player.closeInventory();
            } else if (slot == 4) {
                nationTreasuryService.promptWithdraw(player, kingdom);
                player.closeInventory();
            } else if (slot == 6) {
                nationTreasuryService.promptTax(player, kingdom);
                player.closeInventory();
            }
        }
    }
}
