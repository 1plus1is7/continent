package me.continent.market;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketPurchaseGUI {
    public static void open(Player player, MarketItem item) {
        Holder holder = new Holder(player.getUniqueId(), item.getId());
        Inventory inv = Bukkit.createInventory(holder, 45, "상품 구매");
        holder.setInventory(inv);
        fill(inv);
        inv.setItem(22, item.getItem().clone());
        renderButtons(inv, 1, item.getPricePerUnit(), item.getStock());
        player.openInventory(inv);
    }

    static void renderButtons(Inventory inv, int qty, int unitPrice, int stock) {
        inv.setItem(20, qtyButton(Material.REDSTONE, "-10개", qty - 10, stock));
        inv.setItem(21, qtyButton(Material.REDSTONE, "-1개", qty - 1, stock));
        inv.setItem(23, qtyButton(Material.LIME_DYE, "+1개", qty + 1, stock));
        inv.setItem(24, qtyButton(Material.LIME_DYE, "+10개", qty + 10, stock));
        if (qty < 1) qty = 1; if (qty > stock) qty = stock;
        ItemStack price = new ItemStack(Material.GOLD_INGOT);
        ItemMeta pm = price.getItemMeta();
        pm.setDisplayName(ChatColor.GOLD + "가격: " + (unitPrice * qty) + "C");
        price.setItemMeta(pm);
        inv.setItem(31, price);
        inv.setItem(38, createButton(Material.BARRIER, "취소"));

        ItemStack confirm = createButton(Material.EMERALD_BLOCK, "구매");
        ItemMeta cm = confirm.getItemMeta();
        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add("§7수량: " + qty + "/" + stock);
        lore.add("§7가격: " + (unitPrice * qty) + "C");
        cm.setLore(lore);
        confirm.setItemMeta(cm);
        inv.setItem(40, confirm);

        inv.setItem(42, createButton(Material.ARROW, "돌아가기"));
    }

    private static ItemStack createButton(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        item.setItemMeta(meta);
        return item;
    }

    private static void fill(Inventory inv) {
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, pane);
        }
    }

    private static ItemStack qtyButton(Material mat, String name, int qty, int stock) {
        if (qty < 1) qty = 1; if (qty > stock) qty = stock;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        List<String> lore = new ArrayList<>();
        lore.add("§7수량: " + qty + "/" + stock);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    static class Holder implements InventoryHolder {
        private final UUID buyer;
        private final UUID itemId;
        private int quantity = 1;
        private Inventory inv;
        Holder(UUID buyer, UUID itemId) { this.buyer = buyer; this.itemId = itemId; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public UUID getBuyer() { return buyer; }
        public UUID getItemId() { return itemId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int q) { this.quantity = q; }
    }
}
