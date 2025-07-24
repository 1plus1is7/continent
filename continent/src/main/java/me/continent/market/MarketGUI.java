package me.continent.market;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketGUI {

    public static void open(Player player, int page, MarketManager.SortMode sort, boolean mine) {
        List<MarketItem> list = MarketManager.getSorted(sort);
        if (mine) {
            UUID u = player.getUniqueId();
            list = list.stream().filter(i -> i.getSeller().equals(u)).toList();
        }
        int maxPage = Math.max(1, (list.size() - 1) / 45 + 1);
        page = Math.max(1, Math.min(page, maxPage));
        Holder holder = new Holder(page, sort, mine);
        Inventory inv = Bukkit.createInventory(holder, 54, "\uE000§f\uE002");
        holder.setInventory(inv);
        fill(inv);

        PlayerData data = PlayerDataManager.get(player.getUniqueId());

        int start = (page - 1) * 45;
        for (int i = 0; i < 45; i++) {
            int idx = start + i;
            if (idx >= list.size()) break;
            MarketItem mi = list.get(idx);
            ItemStack item = mi.getItem().clone();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(item.getItemMeta().getDisplayName());
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : new ArrayList<>(meta.getLore());
            lore.add("§7가격: " + mi.getPricePerUnit() + "G");
            lore.add("§7재고: " + mi.getStock());
            OfflinePlayer op = Bukkit.getOfflinePlayer(mi.getSeller());
            String sellerName = op.getName() != null ? op.getName() : mi.getSeller().toString();
            lore.add("§7판매자: " + sellerName);
            lore.add("§e클릭하여 구매");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        inv.setItem(45, createButton(Material.BARRIER, "이전 페이지"));
        inv.setItem(46, createButton(Material.BARRIER, "다음 페이지"));
        inv.setItem(47, createButton(Material.BARRIER, mine ? "전체 보기" : "내 판매 보기"));
        inv.setItem(48, createButton(Material.BARRIER, "상품 등록"));
        inv.setItem(49, createButton(Material.BARRIER, sort == MarketManager.SortMode.NEWEST ? "가격순" : "최신순"));
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta im = info.getItemMeta();
        im.setDisplayName(ChatColor.AQUA + "페이지 " + page + "/" + maxPage);
        List<String> ilore = new ArrayList<>();
        ilore.add(ChatColor.GRAY + "정렬: " + (sort == MarketManager.SortMode.NEWEST ? "최신순" : "가격순"));
        ilore.add(ChatColor.GRAY + "필터: " + (mine ? "내 상품" : "전체"));
        im.setLore(ilore);
        info.setItemMeta(im);
        inv.setItem(50, info);

        ItemStack bal = new ItemStack(Material.GOLD_INGOT);
        ItemMeta bm = bal.getItemMeta();
        bm.setDisplayName(ChatColor.GOLD + "보유 골드: " + data.getGold() + "G");
        bal.setItemMeta(bm);
        inv.setItem(51, bal);
        inv.setItem(53, createButton(Material.BARRIER, "닫기"));
        player.openInventory(inv);
    }

    private static ItemStack createButton(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        CustomModelDataComponent btnCmd = meta.getCustomModelDataComponent();
        btnCmd.setStrings(java.util.List.of("1"));
        meta.setCustomModelDataComponent(btnCmd);
        item.setItemMeta(meta);
        return item;
    }

    private static void fill(Inventory inv) {
        ItemStack pane = new ItemStack(Material.BARRIER);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        CustomModelDataComponent paneCmd = meta.getCustomModelDataComponent();
        paneCmd.setStrings(java.util.List.of("1"));
        meta.setCustomModelDataComponent(paneCmd);
        pane.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, pane);
        }
    }

    static class Holder implements InventoryHolder {
        private final int page;
        private final MarketManager.SortMode sort;
        private final boolean mine;
        private Inventory inv;
        Holder(int page, MarketManager.SortMode sort, boolean mine) {
            this.page = page; this.sort = sort; this.mine = mine;
        }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public int getPage() { return page; }
        public MarketManager.SortMode getSort() { return sort; }
        public boolean isMine() { return mine; }
    }
}
