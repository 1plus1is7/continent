package me.continent.nation.gui;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NationListGUI {
    public static void open(Player player) {
        List<Nation> list = new ArrayList<>(NationManager.getAll());
        int size = Math.min(54, Math.max(9, ((list.size() - 1) / 9 + 1) * 9));
        Holder holder = new Holder();
        Inventory inv = Bukkit.createInventory(holder, size, "국가 목록");
        holder.inv = inv;
        for (int i = 0; i < list.size() && i < size; i++) {
            Nation n = list.get(i);
            ItemStack item = n.getSymbol() == null ? new ItemStack(Material.WHITE_BANNER) : n.getSymbol().clone();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(n.getName());
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }

    static class Holder implements InventoryHolder {
        private Inventory inv;
        @Override
        public Inventory getInventory() { return inv; }
    }
}
