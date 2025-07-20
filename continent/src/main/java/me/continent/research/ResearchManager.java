package me.continent.research;

import me.continent.ContinentPlugin;
import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class ResearchManager {
    private static final Map<String, ResearchNode> nodes = new HashMap<>();

    public static void loadNodes(ContinentPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "research_nodes.yml");
        if (!file.exists()) {
            plugin.saveResource("research_nodes.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String tree : config.getKeys(false)) {
            for (String tierKey : Objects.requireNonNull(config.getConfigurationSection(tree)).getKeys(false)) {
                int tier = Integer.parseInt(tierKey.substring(1));
                List<Map<?, ?>> list = config.getMapList(tree + "." + tierKey);
                for (Map<?, ?> map : list) {
                    String id = Objects.toString(map.get("id"));
                    String effect = Objects.toString(map.get("effect"));
                    String cost = Objects.toString(map.get("cost"));
                    String time = Objects.toString(map.get("time"));
                    List<String> prereq = (List<String>) map.get("prereq");
                    if (prereq == null) prereq = new ArrayList<>();
                    ResearchNode node = new ResearchNode(id, effect, cost, time, prereq, tree, tier);
                    nodes.put(id, node);
                }
            }
        }
    }

    public static Collection<ResearchNode> getAllNodes() {
        return nodes.values();
    }

    public static void openMenu(Player player) {
        Kingdom kingdom = getKingdom(player);
        if (kingdom == null) {
            player.sendMessage("§c소속된 국가가 없습니다.");
            return;
        }
        Inventory inv = Bukkit.createInventory(new ResearchHolder(kingdom), 54, "Research");
        ResearchHolder holder = (ResearchHolder) inv.getHolder();
        holder.setInventory(inv);
        int slot = 0;
        for (ResearchNode node : nodes.values()) {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(node.getId());
            List<String> lore = new ArrayList<>();
            lore.add(node.getEffect());
            lore.add("Cost: " + node.getCost());
            lore.add("Time: " + node.getTime());
            if (kingdom.getResearchedNodes().contains(node.getId())) {
                lore.add("§a연구 완료");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }
        player.openInventory(inv);
    }

    public static void startResearch(Player player, ResearchNode node) {
        Kingdom kingdom = getKingdom(player);
        if (kingdom == null) {
            player.sendMessage("§c소속된 국가가 없습니다.");
            return;
        }
        if (kingdom.getResearchedNodes().contains(node.getId())) {
            player.sendMessage("§e이미 연구 완료된 노드입니다.");
            return;
        }
        for (String pre : node.getPrereq()) {
            if (!kingdom.getResearchedNodes().contains(pre)) {
                player.sendMessage("§c선행 연구가 완료되지 않았습니다.");
                return;
            }
        }
        player.sendMessage("§a연구를 시작합니다: " + node.getId());
        new BukkitRunnable() {
            @Override
            public void run() {
                kingdom.getResearchedNodes().add(node.getId());
                player.sendMessage("§e연구 완료: " + node.getId());
            }
        }.runTaskLater(ContinentPlugin.getInstance(), 20L * 5); // 5초 테스트용
    }

    private static Kingdom getKingdom(Player player) {
        me.continent.village.Village v = me.continent.village.VillageManager.getByPlayer(player.getUniqueId());
        if (v == null) return null;
        String kName = v.getKingdom();
        if (kName == null) return null;
        return KingdomManager.getByName(kName);
    }

    static class ResearchHolder implements org.bukkit.inventory.InventoryHolder {
        private final Kingdom kingdom;
        private Inventory inventory;
        ResearchHolder(Kingdom kingdom) { this.kingdom = kingdom; }
        void setInventory(Inventory inv) { this.inventory = inv; }
        @Override
        public Inventory getInventory() { return inventory; }
        public Kingdom getKingdom() { return kingdom; }
    }
}
