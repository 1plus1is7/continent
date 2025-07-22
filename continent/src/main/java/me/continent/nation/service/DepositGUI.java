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

public class DepositGUI {
    public static void open(Player player, me.continent.nation.nation kingdom) {
        Holder holder = new Holder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, 45, "국고 입금");
        holder.setInventory(inv);
        holder.setAmount(1);
        fill(inv);
        render(inv, holder.getAmount());
        player.openInventory(inv);
    }

    static void render(Inventory inv, int amount) {
        inv.setItem(20, amountButton(Material.REDSTONE, "-10G", amount - 10));
        inv.setItem(21, amountButton(Material.REDSTONE, "-1G", amount - 1));
        inv.setItem(23, amountButton(Material.LIME_DYE, "+1G", amount + 1));
        inv.setItem(24, amountButton(Material.LIME_DYE, "+10G", amount + 10));

        if (amount < 1) amount = 1;
        ItemStack price = new ItemStack(Material.GOLD_INGOT);
        ItemMeta pm = price.getItemMeta();
        pm.setDisplayName(ChatColor.GOLD + "금액: " + amount + "G");
        price.setItemMeta(pm);
        inv.setItem(31, price);
        inv.setItem(38, createButton(Material.BARRIER, "취소"));
        inv.setItem(40, createButton(Material.EMERALD_BLOCK, "입금"));
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

    private static ItemStack amountButton(Material mat, String name, int amount) {
        if (amount < 1) amount = 1;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        List<String> lore = new ArrayList<>();
        lore.add("§7금액: " + amount + "G");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    static class Holder implements InventoryHolder {
        private final me.continent.nation.nation kingdom;
        private Inventory inv;
        private int amount = 1;
        Holder(me.continent.nation.nation k) { this.kingdom = k; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public me.continent.nation.nation getnation() { return kingdom; }
        public int getAmount() { return amount; }
        public void setAmount(int a) { this.amount = Math.max(1, a); }
    }
}
