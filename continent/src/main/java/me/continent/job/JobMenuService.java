package me.continent.job;

import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/** GUI service for job selection. */
public class JobMenuService {
    static class SelectHolder implements InventoryHolder {
        private Inventory inv;
        @Override public Inventory getInventory() { return inv; }
        void setInventory(Inventory inv) { this.inv = inv; }
    }

    public static void openSelect(Player player) {
        Inventory inv = Bukkit.createInventory(new SelectHolder(), 27, "직업 선택");
        ((SelectHolder) inv.getHolder()).setInventory(inv);
        render(inv);
        player.openInventory(inv);
    }

    static void render(Inventory inv) {
        int slot = 0;
        for (Job job : JobManager.getAll()) {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(job.getName());
            meta.setLore(List.of(job.getDescription()));
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }
    }

    static void choose(Player player, int slot) {
        Job job = JobManager.getAll().stream().skip(slot).findFirst().orElse(null);
        if (job == null) return;
        PlayerData data = PlayerDataManager.get(player.getUniqueId());
        data.setJobId(job.getId());
        PlayerDataManager.save(player.getUniqueId());
        player.sendMessage("§a직업이 설정되었습니다: " + job.getName());
    }
}
