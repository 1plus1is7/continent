package me.continent.enterprise.gui;

import me.continent.enterprise.EnterpriseType;
import me.continent.enterprise.gui.DeliveryStatusGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

public class EnterpriseMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof EnterpriseMenuService.RegisterHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            switch (slot) {
                case 11 -> EnterpriseMenuService.requestName(player, holder);
                case 13 -> EnterpriseMenuService.openTypeSelect(player, holder);
                case 15 -> EnterpriseMenuService.tryCreate(player, holder);
            }
        } else if (inv.getHolder() instanceof EnterpriseMenuService.TypeHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            if (slot == 26) {
                EnterpriseMenuService.openRegister(player, holder.getParent());
                return;
            }
            if (slot >= 0 && slot < EnterpriseType.values().length) {
                holder.getParent().setType(EnterpriseType.values()[slot]);
                EnterpriseMenuService.openRegister(player, holder.getParent());
            }
        } else if (inv.getHolder() instanceof EnterpriseMenuService.MainHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            if (slot == 49) {
                player.closeInventory();
            } else if (slot == 13) {
                DeliveryStatusGUI.open(player, holder.getEnterprise());
            } else {
                player.sendMessage("§e준비 중인 기능입니다.");
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        var holder = EnterpriseMenuService.getPendingName(player.getUniqueId());
        if (holder != null) {
            event.setCancelled(true);
            String name = event.getMessage();
            EnterpriseMenuService.provideName(player, name);
        }
    }
}
