package me.continent.market;

import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.UUID;

public class MarketListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof MarketGUI.Holder holder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();
            if (slot < 45) {
                ItemStack item = event.getCurrentItem();
                if (item == null) return;
                int index = holder.getPage() - 1;
                var list = MarketManager.getSorted(holder.getSort());
                if (holder.isMine()) {
                    UUID u = player.getUniqueId();
                    list = list.stream().filter(i -> i.getSeller().equals(u)).toList();
                }
                int idx = index * 45 + slot;
                if (idx >= list.size()) return;
                MarketItem mi = list.get(idx);
                MarketPurchaseGUI.open(player, mi);
            } else {
                switch (slot) {
                    case 45 -> MarketGUI.open(player, holder.getPage() - 1, holder.getSort(), holder.isMine());
                    case 46 -> MarketGUI.open(player, holder.getPage() + 1, holder.getSort(), holder.isMine());
                    case 47 -> MarketGUI.open(player, 1, holder.getSort(), !holder.isMine());
                    case 48 -> MarketRegisterGUI.open(player);
                    case 49 -> {
                        MarketManager.SortMode next = holder.getSort() == MarketManager.SortMode.NEWEST ? MarketManager.SortMode.PRICE : MarketManager.SortMode.NEWEST;
                        MarketGUI.open(player, 1, next, holder.isMine());
                    }
                    case 53 -> player.closeInventory();
                }
            }
        } else if (inv.getHolder() instanceof MarketRegisterGUI.Holder holder) {
            int slot = event.getRawSlot();
            if (slot >= 0 && slot < 45) event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack current = event.getCurrentItem();
            switch (slot) {
                case 22 -> event.setCancelled(false);
                case 20 -> { holder.setPrice(holder.getPrice() - 10); MarketRegisterGUI.renderButtons(inv, holder.getPrice()); }
                case 21 -> { holder.setPrice(holder.getPrice() - 1); MarketRegisterGUI.renderButtons(inv, holder.getPrice()); }
                case 23 -> { holder.setPrice(holder.getPrice() + 1); MarketRegisterGUI.renderButtons(inv, holder.getPrice()); }
                case 24 -> { holder.setPrice(holder.getPrice() + 10); MarketRegisterGUI.renderButtons(inv, holder.getPrice()); }
                case 38 -> { player.closeInventory(); }
                case 42 -> MarketGUI.open(player,1,MarketManager.SortMode.NEWEST,false);
                case 40 -> {
                    ItemStack item = inv.getItem(22);
                    if (item == null || item.getType() == Material.AIR) {
                        player.sendMessage("§c아이템을 넣어주세요.");
                        return;
                    }
                    MarketItem mi = new MarketItem(UUID.randomUUID(), player.getUniqueId(), item.clone(), holder.getPrice(), item.getAmount(), LocalDateTime.now());
                    MarketManager.addItem(mi);
                    inv.setItem(22, null);
                    player.getInventory().removeItem(item);
                    player.sendMessage("§a상품이 등록되었습니다.");
                    player.closeInventory();
                }
            }
        } else if (inv.getHolder() instanceof MarketPurchaseGUI.Holder holder) {
            int slot = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();
            MarketItem mi = MarketManager.getItem(holder.getItemId());
            if (mi == null) { player.closeInventory(); return; }
            if (slot >= 0 && slot < 45) event.setCancelled(true);
            switch (slot) {
                case 20 -> { holder.setQuantity(holder.getQuantity() - 10); MarketPurchaseGUI.renderButtons(inv, holder.getQuantity(), mi.getPricePerUnit(), mi.getStock()); }
                case 21 -> { holder.setQuantity(holder.getQuantity() - 1); MarketPurchaseGUI.renderButtons(inv, holder.getQuantity(), mi.getPricePerUnit(), mi.getStock()); }
                case 23 -> { holder.setQuantity(holder.getQuantity() + 1); MarketPurchaseGUI.renderButtons(inv, holder.getQuantity(), mi.getPricePerUnit(), mi.getStock()); }
                case 24 -> { holder.setQuantity(holder.getQuantity() + 10); MarketPurchaseGUI.renderButtons(inv, holder.getQuantity(), mi.getPricePerUnit(), mi.getStock()); }
                case 38 -> player.closeInventory();
                case 42 -> MarketGUI.open(player,1,MarketManager.SortMode.NEWEST,false);
                case 40 -> {
                    int qty = holder.getQuantity();
                    if (qty < 1) qty = 1; if (qty > mi.getStock()) qty = mi.getStock();
                    PlayerData buyerData = PlayerDataManager.get(player.getUniqueId());
                    int total = qty * mi.getPricePerUnit();
                    if (buyerData.getGold() < total) {
                        player.sendMessage("§c골드가 부족합니다");
                        return;
                    }
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage("§c인벤토리가 부족합니다");
                        return;
                    }
                    if (player.getUniqueId().equals(mi.getSeller())) {
                        player.sendMessage("§c자신의 상품은 구매할 수 없습니다");
                        return;
                    }
                    buyerData.removeGold(total);
                    PlayerData sellerData = PlayerDataManager.get(mi.getSeller());
                    sellerData.addGold(total);
                    ItemStack give = mi.getItem().clone();
                    give.setAmount(qty);
                    player.getInventory().addItem(give);
                    mi.setStock(mi.getStock() - qty);
                    if (mi.getStock() <= 0) MarketManager.removeItem(mi.getId());
                    else MarketManager.save();
                    player.sendMessage("§a구매 완료");
                    player.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof MarketRegisterGUI.Holder holder) {
            ItemStack item = inv.getItem(22);
            if (item != null && item.getType() != Material.AIR) {
                event.getPlayer().getInventory().addItem(item);
            }
        }
    }
}
