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
            if (slot == 11) {
                NationTreasuryService.openAmount(player, nation, NationTreasuryService.Mode.DEPOSIT, 1);
            } else if (slot == 15) {
                NationTreasuryService.openAmount(player, nation, NationTreasuryService.Mode.WITHDRAW, 1);
            } else if (slot == 22) {
                ServerMenuService.openMenu(player);
            }
        } else if (inv.getHolder() instanceof NationTreasuryService.AmountHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            if (slot == 20) { holder.setAmount(holder.getAmount() - 10); NationTreasuryService.renderAmount(inv, holder, player); }
            else if (slot == 21) { holder.setAmount(holder.getAmount() - 1); NationTreasuryService.renderAmount(inv, holder, player); }
            else if (slot == 23) { holder.setAmount(holder.getAmount() + 1); NationTreasuryService.renderAmount(inv, holder, player); }
            else if (slot == 24) { holder.setAmount(holder.getAmount() + 10); NationTreasuryService.renderAmount(inv, holder, player); }
            else if (slot == 41) { holder.setAmount(NationTreasuryService.getMaxAmount(player, holder)); NationTreasuryService.renderAmount(inv, holder, player); }
            else if (slot == 38) { player.closeInventory(); }
            else if (slot == 40) { NationTreasuryService.perform(player, holder); player.closeInventory(); }
            else if (slot == 42) { NationTreasuryService.openMenu(player, holder.getNation()); }
        }
    }
}
