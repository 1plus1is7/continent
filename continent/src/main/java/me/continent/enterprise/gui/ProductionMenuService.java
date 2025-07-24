package me.continent.enterprise.gui;

import me.continent.enterprise.Enterprise;
import me.continent.enterprise.EnterpriseManager;
import me.continent.enterprise.production.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** GUI service for enterprise production. */
public class ProductionMenuService {
    public static class Holder implements InventoryHolder {
        private final Enterprise enterprise;
        private Inventory inv;
        Holder(Enterprise ent) { this.enterprise = ent; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Enterprise getEnterprise() { return enterprise; }
    }

    public static void open(Player player, Enterprise enterprise) {
        Holder holder = new Holder(enterprise);
        Inventory inv = Bukkit.createInventory(holder, 27, "생산 관리");
        holder.setInventory(inv);
        render(inv, enterprise);
        player.openInventory(inv);
    }

    private static void render(Inventory inv, Enterprise enterprise) {
        fill(inv);
        ProductionJob job = ProductionManager.getJob(enterprise.getId());
        if (job != null) {
            ItemStack item = job.getRecipe().getOutput();
            item.setAmount(job.getRecipe().getAmount());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a진행 중: " + job.getRecipe().getName());
            List<String> lore = new ArrayList<>();
            long remain = job.getFinishTime() - System.currentTimeMillis();
            if (remain < 0) remain = 0;
            lore.add("남은 시간: " + (remain / 1000) + "s");
            if (job.isComplete()) lore.set(0, "완료됨 - 수령 대기");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(13, item);
        } else {
            int idx = 0;
            for (ProductionRecipe r : ProductionManager.getRecipes(enterprise.getType())) {
                ItemStack item = r.getOutput();
                item.setAmount(r.getAmount());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(r.getName());
                List<String> lore = new ArrayList<>();
                for (Map.Entry<Material,Integer> e : r.getResources().entrySet()) {
                    lore.add(e.getKey().name()+" x"+e.getValue());
                }
                lore.add("시간: "+r.getTime()+"s");
                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(idx++, item);
                if (idx >= 26) break;
            }
        }
        inv.setItem(26, button(Material.ARROW, "뒤로", null));
    }

    private static ItemStack button(Material m, String name, List<String> lore) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
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
}
