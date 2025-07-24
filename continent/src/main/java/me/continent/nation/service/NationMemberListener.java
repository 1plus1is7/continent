package me.continent.nation.service;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import me.continent.menu.ServerMenuService;

public class NationMemberListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof NationMemberService.MemberHolder) {
            event.setCancelled(true);
            if (event.getRawSlot() == 26) {
                Player player = (Player) event.getWhoClicked();
                ServerMenuService.openMenu(player);
            }
        }
    }
}
