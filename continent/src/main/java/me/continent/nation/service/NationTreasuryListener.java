package me.continent.nation.service;

import me.continent.nation.Nation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import me.continent.menu.ServerMenuService;

public class NationTreasuryListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof NationTreasuryService.TreasuryHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Nation nation = holder.getNation();
            int slot = event.getRawSlot();
            if (slot == 2) {
                NationTreasuryService.promptDeposit(player, nation);
                player.closeInventory();
            } else if (slot == 4) {
                NationTreasuryService.promptWithdraw(player, nation);
                player.closeInventory();
            } else if (slot == 8) {
                ServerMenuService.openMenu(player);
            }
        }
    }
}
