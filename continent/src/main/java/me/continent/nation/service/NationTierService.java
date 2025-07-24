package me.continent.nation.service;

import me.continent.nation.Nation;
import me.continent.storage.NationStorage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

public class NationTierService {
    private static final long THREE_DAYS = 3L * 24 * 60 * 60 * 1000;

    /**
     * Get display name for a tier.
     */
    public static String getTierName(int tier) {
        return switch (tier) {
            case 2 -> "중급";
            case 3 -> "상급";
            default -> "하급";
        };
    }

    /**
     * Count members that have logged in within the last three days.
     */
    private static int countRecentMembers(Nation nation) {
        int cnt = 0;
        long now = System.currentTimeMillis();
        for (java.util.UUID uuid : nation.getMembers()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            if (op.isOnline() || now - op.getLastPlayed() <= THREE_DAYS) {
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * Check whether the nation satisfies the requirements for the next tier.
     * @return null if upgrade is possible, otherwise an error message
     */
    public static String checkRequirements(Nation nation) {
        int tier = nation.getTier();
        if (tier >= 3) {
            return "이미 최고 등급입니다.";
        }
        if (tier == 1) {
            if (nation.getVault() < 700) return "금고가 부족합니다.";
            if (countRecentMembers(nation) < 4) return "최근 접속 구성원이 부족합니다.";
            if (nation.getClaimedChunks().size() < 6) return "영토가 부족합니다.";
            if (nation.getMembers().size() < 2) return "마을 수가 부족합니다.";
        } else if (tier == 2) {
            if (nation.getVault() < 1800) return "금고가 부족합니다.";
            if (countRecentMembers(nation) < 8) return "최근 접속 구성원이 부족합니다.";
            if (nation.getClaimedChunks().size() < 15) return "영토가 부족합니다.";
            // 추가 조건(기업, 전쟁)은 아직 구현되지 않았으므로 생략
        }
        return null;
    }

    /**
     * Upgrade the nation if possible.
     * @return true if upgraded
     */
    public static boolean upgrade(Nation nation) {
        String fail = checkRequirements(nation);
        if (fail != null) {
            return false;
        }
        nation.setTier(nation.getTier() + 1);
        NationStorage.save(nation);
        String msg = "§e[알림] 국가 §b" + nation.getName() + "§e가 §a" + getTierName(nation.getTier()) + " 국가§e로 승격하였습니다!";
        Bukkit.broadcastMessage(msg);
        for (java.util.UUID uuid : nation.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
            }
        }
        return true;
    }

    /**
     * Simple GUI showing tier info.
     */
    public static void openInfo(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "Nation Tier Info");
        inv.setItem(1, createItem(Material.BOOK, "★ - 하급"));
        inv.setItem(4, createItem(Material.BOOK, "★★ - 중급"));
        inv.setItem(7, createItem(Material.BOOK, "★★★ - 상급"));
        player.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
