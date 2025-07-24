package me.continent.menu;

import me.continent.market.MarketGUI;
import me.continent.market.MarketManager;
import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.nation.service.NationMenuService;
import me.continent.player.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ServerMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof ServerMenuService.MenuHolder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            switch (slot) {
                case 11 -> {
                    Nation nation = NationManager.getByPlayer(player.getUniqueId());
                    if (nation == null) {
                        player.sendMessage("§c소속된 국가가 없습니다.");
                    } else {
                        NationMenuService.openMenu(player, nation);
                    }
                }
                case 14 -> {
                    var data = PlayerDataManager.get(player.getUniqueId());
                    double gold = data.getGold();
                    Nation nation = NationManager.getByPlayer(player.getUniqueId());
                    String nationName = nation != null ? nation.getName() : "없음";
                    player.sendMessage("§6[플레이어 정보]");
                    player.sendMessage("§f골드: §e" + String.format("%.2f", gold) + "G");
                    player.sendMessage("§f국가: §e" + nationName);
                }
                case 17 -> MarketGUI.open(player, 1, MarketManager.SortMode.NEWEST, false);
                case 38, 41, 44 -> player.sendMessage("§e준비 중인 기능입니다.");
            }
        }
    }
}
