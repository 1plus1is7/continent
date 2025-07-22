package me.continent.village.service;

import me.continent.village.Village;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class VillageMemberService {
    public static void openMenu(Player player, Village village) {
        MemberHolder holder = new MemberHolder(village);
        Inventory inv = Bukkit.createInventory(holder, 27, "Village Members");
        holder.setInventory(inv);

        OfflinePlayer king = Bukkit.getOfflinePlayer(village.getKing());
        ItemStack kingHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta km = (SkullMeta) kingHead.getItemMeta();
        km.setOwningPlayer(king);
        km.setDisplayName("§e촌장: " + (king.getName() != null ? king.getName() : king.getUniqueId()));
        kingHead.setItemMeta(km);
        inv.setItem(4, kingHead);

        int idx = 9;
        for (UUID uuid : village.getMembers()) {
            if (uuid.equals(village.getKing())) continue;
            if (idx >= inv.getSize()) break;
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sm = (SkullMeta) head.getItemMeta();
            sm.setOwningPlayer(op);
            sm.setDisplayName(op.getName() != null ? op.getName() : uuid.toString());
            head.setItemMeta(sm);
            inv.setItem(idx++, head);
        }

        player.openInventory(inv);
    }

    static class MemberHolder implements InventoryHolder {
        private final Village village;
        private Inventory inv;
        MemberHolder(Village village) { this.village = village; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Village getVillage() { return village; }
    }
}
