package me.continent.nation.service;

import me.continent.nation.Nation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class NationMemberService {
    public static void openMenu(Player player, Nation nation) {
        MemberHolder holder = new MemberHolder(nation);
        Inventory inv = Bukkit.createInventory(holder, 27, "Nation Members");
        holder.setInventory(inv);

        OfflinePlayer king = Bukkit.getOfflinePlayer(nation.getKing());
        ItemStack kingHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta km = (SkullMeta) kingHead.getItemMeta();
        km.setOwningPlayer(king);
        km.setDisplayName("§e촌장: " + (king.getName() != null ? king.getName() : king.getUniqueId()));
        kingHead.setItemMeta(km);
        inv.setItem(4, kingHead);

        int idx = 9;
        for (UUID uuid : nation.getMembers()) {
            if (uuid.equals(nation.getKing())) continue;
            if (idx >= inv.getSize()) break;
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sm = (SkullMeta) head.getItemMeta();
            sm.setOwningPlayer(op);
            sm.setDisplayName(op.getName() != null ? op.getName() : uuid.toString());
            head.setItemMeta(sm);
            inv.setItem(idx++, head);
        }

        inv.setItem(26, createArrow());

        player.openInventory(inv);
    }

    private static ItemStack createArrow() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("메인 메뉴");
        item.setItemMeta(meta);
        return item;
    }

    static class MemberHolder implements InventoryHolder {
        private final Nation nation;
        private Inventory inv;
        MemberHolder(Nation nation) { this.nation = nation; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Nation getNation() { return nation; }
    }
}
