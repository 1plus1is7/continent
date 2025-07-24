package me.continent.enterprise.gui;

import me.continent.enterprise.Enterprise;
import me.continent.enterprise.EnterpriseManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EnterpriseListGUI {
    public static void open(Player player) {
        List<Enterprise> list = new ArrayList<>(EnterpriseManager.getAll());
        int size = Math.min(54, Math.max(9, ((list.size() - 1) / 9 + 1) * 9));
        Holder holder = new Holder();
        Inventory inv = Bukkit.createInventory(holder, size, "기업 목록");
        holder.inv = inv;
        for (int i = 0; i < list.size() && i < size; i++) {
            Enterprise e = list.get(i);
            ItemStack item = e.getSymbol() == null ? new ItemStack(Material.WHITE_BANNER) : e.getSymbol().clone();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(e.getName());
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
