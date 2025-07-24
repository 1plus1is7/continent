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

public class MarketRegisterGUI {
    public static void open(Player player) {
        open(player, null);
    }

    public static void open(Player player, String enterpriseId) {
        Holder holder = new Holder(player.getUniqueId(), enterpriseId);
        Inventory inv = Bukkit.createInventory(holder, 45, "상품 등록");
        holder.setInventory(inv);
        fill(inv);
        inv.setItem(22, null);
        renderButtons(inv, 1);
        player.openInventory(inv);
    }

    static void renderButtons(Inventory inv, int price) {
        inv.setItem(20, priceButton(Material.REDSTONE, "-10G", price - 10));
        inv.setItem(21, priceButton(Material.REDSTONE, "-1G", price - 1));
        inv.setItem(23, priceButton(Material.LIME_DYE, "+1G", price + 1));
        inv.setItem(24, priceButton(Material.LIME_DYE, "+10G", price + 10));

        ItemStack priceItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta pm = priceItem.getItemMeta();
        pm.setDisplayName(ChatColor.GOLD + "가격: " + price + "G");
        priceItem.setItemMeta(pm);
        inv.setItem(31, priceItem);

        inv.setItem(38, createButton(Material.BARRIER, "취소"));

        ItemStack confirm = createButton(Material.EMERALD_BLOCK, "등록");
        ItemMeta cm = confirm.getItemMeta();
        cm.setLore(java.util.List.of("§7가격: " + price + "G"));
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

    private static ItemStack priceButton(Material mat, String name, int result) {
        if (result < 1) result = 1;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        List<String> lore = new ArrayList<>();
        lore.add("§7가격: " + result + "G");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    static class Holder implements InventoryHolder {
        private final UUID owner;
        private final String enterpriseId;
        private int price = 1;
        private Inventory inv;
        Holder(UUID owner, String enterpriseId) { this.owner = owner; this.enterpriseId = enterpriseId; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public UUID getOwner() { return owner; }
        public String getEnterpriseId() { return enterpriseId; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = Math.max(1, price); }
    }
}
