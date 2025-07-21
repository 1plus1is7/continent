package me.continent.kingdom.service;

import me.continent.kingdom.nation;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class nationMemberService {
    public static void openMenu(Player player, nation kingdom) {
        MemberHolder holder = new MemberHolder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, 27, "nation Members");
        holder.setInventory(inv);

        OfflinePlayer king = Bukkit.getOfflinePlayer(kingdom.getLeader());
        ItemStack kingHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta km = (SkullMeta) kingHead.getItemMeta();
        km.setOwningPlayer(king);
        km.setDisplayName("§e국왕: " + (king.getName() != null ? king.getName() : king.getUniqueId()));
        kingHead.setItemMeta(km);
        inv.setItem(4, kingHead);

        Set<UUID> memberSet = new LinkedHashSet<>();
        for (String vName : kingdom.getVillages()) {
            Village v = VillageManager.getByName(vName);
            if (v != null) memberSet.addAll(v.getMembers());
        }
        memberSet.remove(king.getUniqueId());
        int idx = 9;
        for (UUID uuid : memberSet) {
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
        private final nation kingdom;
        private Inventory inv;
        MemberHolder(nation k) { this.kingdom = k; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public nation getnation() { return kingdom; }
    }
}
