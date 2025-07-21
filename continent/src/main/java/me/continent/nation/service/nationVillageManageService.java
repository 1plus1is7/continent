package me.continent.nation.service;

import me.continent.nation.nation;
import me.continent.nation.nationManager;
import me.continent.nation.nationStorage;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.storage.VillageStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class nationVillageManageService {
    public static void openMenu(Player player, nation kingdom) {
        int size = ((kingdom.getVillages().size() + 8) / 9) * 9;
        VillageHolder holder = new VillageHolder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, Math.max(9, size), "Village Manage");
        holder.setInventory(inv);
        int slot = 0;
        for (String vName : kingdom.getVillages()) {
            Village v = VillageManager.getByName(vName);
            ItemStack item = (v != null && v.getSymbol() != null) ? v.getSymbol().clone() : new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(vName);
            List<String> lore = new ArrayList<>();
            if (vName.equalsIgnoreCase(kingdom.getCapital())) {
                lore.add("§e수도");
            } else {
                lore.add("§7왼클릭: 수도 지정");
                lore.add("§7우클릭: 제외");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }
        player.openInventory(inv);
    }

    static class VillageHolder implements InventoryHolder {
        private final nation kingdom;
        private Inventory inv;
        VillageHolder(nation k) { this.kingdom = k; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public nation getnation() { return kingdom; }
    }
}
