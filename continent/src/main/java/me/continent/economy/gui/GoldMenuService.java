package me.continent.economy.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GoldMenuService {
    public static void openMenu(Player player) {
        MenuHolder holder = new MenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, "Gold Menu");
        holder.setInventory(inv);

        inv.setItem(10, createItem(Material.GOLD_INGOT, "잔액 확인"));
        inv.setItem(12, createItem(Material.GOLD_BLOCK, "금괴 구매"));
        inv.setItem(14, createItem(Material.RAW_GOLD, "금 괴 환전"));
        inv.setItem(16, createItem(Material.PLAYER_HEAD, "송금"));
        inv.setItem(22, createItem(Material.ARROW, "메인 메뉴"));

        player.openInventory(inv);
    }

    static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    static class MenuHolder implements InventoryHolder {
        private Inventory inv;
        void setInventory(Inventory i) { this.inv = i; }
        @Override public Inventory getInventory() { return inv; }
    }
}
