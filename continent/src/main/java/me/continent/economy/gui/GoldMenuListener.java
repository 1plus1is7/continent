package me.continent.economy.gui;

import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.menu.ServerMenuService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GoldMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof GoldMenuService.MenuHolder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            switch (slot) {
                case 10 -> {
                    PlayerData data = PlayerDataManager.get(player.getUniqueId());
                    player.sendMessage("§6[크라운] §f현재 보유 크라운: §e" + data.getGold() + "C");
                }
                case 12 -> GoldExchangeGUI.open(player, GoldExchangeGUI.Mode.CONVERT, 1);
                case 14 -> GoldExchangeGUI.open(player, GoldExchangeGUI.Mode.EXCHANGE, 1);
                case 16 -> GoldPayService.openSelect(player);
                case 22 -> ServerMenuService.openMenu(player);
            }
        } else if (inv.getHolder() instanceof GoldExchangeGUI.Holder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            switch (slot) {
                case 20 -> { holder.setQty(holder.getQty() - 10); GoldExchangeGUI.renderButtons(inv, player, holder.getMode(), holder.getQty()); }
                case 21 -> { holder.setQty(holder.getQty() - 1); GoldExchangeGUI.renderButtons(inv, player, holder.getMode(), holder.getQty()); }
                case 23 -> { holder.setQty(holder.getQty() + 1); GoldExchangeGUI.renderButtons(inv, player, holder.getMode(), holder.getQty()); }
                case 24 -> { holder.setQty(holder.getQty() + 10); GoldExchangeGUI.renderButtons(inv, player, holder.getMode(), holder.getQty()); }
                case 41 -> { holder.setQty(GoldExchangeGUI.getMaxQty(player, holder.getMode())); GoldExchangeGUI.renderButtons(inv, player, holder.getMode(), holder.getQty()); }
                case 38 -> player.closeInventory();
                case 40 -> { GoldExchangeGUI.perform(player, holder); player.closeInventory(); }
                case 42 -> ServerMenuService.openMenu(player);
            }
        } else if (inv.getHolder() instanceof GoldPayService.SelectHolder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            if (slot == 53) {
                ServerMenuService.openMenu(player);
                return;
            }
            var item = event.getCurrentItem();
            if (item == null || !item.hasItemMeta()) return;
            var meta = item.getItemMeta();
            if (!(meta instanceof org.bukkit.inventory.meta.SkullMeta skull)) return;
            var target = skull.getOwningPlayer();
            if (target == null) return;
            GoldPayService.openAmount(player, target.getUniqueId(), 1);
        } else if (inv.getHolder() instanceof GoldPayService.AmountHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            switch (slot) {
                case 20 -> { holder.setAmount(holder.getAmount() - 10); GoldPayService.render(inv, holder.getAmount()); }
                case 21 -> { holder.setAmount(holder.getAmount() - 1); GoldPayService.render(inv, holder.getAmount()); }
                case 23 -> { holder.setAmount(holder.getAmount() + 1); GoldPayService.render(inv, holder.getAmount()); }
                case 24 -> { holder.setAmount(holder.getAmount() + 10); GoldPayService.render(inv, holder.getAmount()); }
                case 38 -> player.closeInventory();
                case 40 -> { GoldPayService.performPay(player, holder); player.closeInventory(); }
                case 42 -> ServerMenuService.openMenu(player);
            }
        }
    }
}
