package me.continent.nation.service;

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

public class TaxGUI {
    public static void open(Player player, me.continent.nation.nation kingdom) {
        Holder holder = new Holder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, 45, "세율 설정");
        holder.setInventory(inv);
        holder.setRate((int) kingdom.getTaxRate());
        fill(inv);
        render(inv, holder.getRate());
        player.openInventory(inv);
    }

    static void render(Inventory inv, int rate) {
        inv.setItem(20, rateButton(Material.REDSTONE, "-10%", rate - 10));
        inv.setItem(21, rateButton(Material.REDSTONE, "-1%", rate - 1));
        inv.setItem(23, rateButton(Material.LIME_DYE, "+1%", rate + 1));
        inv.setItem(24, rateButton(Material.LIME_DYE, "+10%", rate + 10));

        if (rate < 0) rate = 0;
        if (rate > 100) rate = 100;
        ItemStack price = new ItemStack(Material.PAPER);
        ItemMeta pm = price.getItemMeta();
        pm.setDisplayName(ChatColor.GOLD + "세율: " + rate + "%");
        price.setItemMeta(pm);
        inv.setItem(31, price);
        inv.setItem(38, createButton(Material.BARRIER, "취소"));
        inv.setItem(40, createButton(Material.EMERALD_BLOCK, "설정"));
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

    private static ItemStack rateButton(Material mat, String name, int rate) {
        if (rate < 0) rate = 0;
        if (rate > 100) rate = 100;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        List<String> lore = new ArrayList<>();
        lore.add("§7세율: " + rate + "%");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    static class Holder implements InventoryHolder {
        private final me.continent.nation.nation kingdom;
        private Inventory inv;
        private int rate = 0;
        Holder(me.continent.nation.nation k) { this.kingdom = k; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public me.continent.nation.nation getnation() { return kingdom; }
        public int getRate() { return rate; }
        public void setRate(int r) { this.rate = Math.max(0, Math.min(100, r)); }
    }
}
