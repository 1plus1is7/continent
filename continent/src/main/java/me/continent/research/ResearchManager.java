package me.continent.research;

import me.continent.ContinentPlugin;
import me.continent.kingdom.nation;
import me.continent.kingdom.nationManager;
import me.continent.kingdom.nationStorage;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.Duration;
import java.util.*;

/**
 * Manages research data and GUI for kingdoms.
 */
public class ResearchManager {
    private static final Map<String, ResearchNode> nodes = new HashMap<>();
    private static final Map<String, ResearchTask> tasks = new HashMap<>();

    /** Load research node definitions from YAML. */
    public static void loadNodes(ContinentPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "research_nodes.yml");
        if (!file.exists()) {
            plugin.saveResource("research_nodes.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String tree : config.getKeys(false)) {
            for (String tierKey : Objects.requireNonNull(config.getConfigurationSection(tree)).getKeys(false)) {
                int tier = Integer.parseInt(tierKey.substring(1));
                for (Map<?, ?> map : config.getMapList(tree + "." + tierKey)) {
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

    // ------------------------------------------------------- GUI

    /** Open tree selection GUI. */
    public static void openTreeSelect(Player player) {
        nation kingdom = getnation(player);
        if (kingdom == null) {
            player.sendMessage("§c소속된 국가가 없습니다.");
            return;
        }
        Inventory inv = Bukkit.createInventory(new TreeHolder(kingdom), 9, "연구 트리 선택");
        TreeHolder holder = (TreeHolder) inv.getHolder();
        holder.setInventory(inv);

        setTreeItem(inv, 1, "MILITARY", kingdom);
        setTreeItem(inv, 2, "NAVAL", kingdom);
        setTreeItem(inv, 4, "INFRA", kingdom);
        setTreeItem(inv, 6, "CIVIL", kingdom);
        setTreeItem(inv, 7, "INDUSTRY", kingdom);

        player.openInventory(inv);
    }

    /** Update or create a tree item based on selection state. */
    private static void setTreeItem(Inventory inv, int slot, String tree, nation k) {
        boolean selected = k.getSelectedResearchTrees().contains(tree) || tree.equals("INFRA");
        if (tree.equals("INFRA")) k.getSelectedResearchTrees().add("INFRA");
        boolean disabled = !selected && k.getSelectedResearchTrees().size() >= k.getResearchSlots();
        String prefix = selected ? "✅ " : disabled ? "⛔ " : "📂 ";

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(prefix + tree);
        List<String> lore = new ArrayList<>();
        if (tree.equals("INFRA")) {
            lore.add("§7항상 선택됨");
        } else if (disabled) {
            lore.add("§7연구 슬롯 부족");
        } else if (selected) {
            lore.add("§7더블클릭으로 선택 해제");
        } else {
            lore.add("§7더블클릭으로 선택");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    /** Open detailed node GUI for the given tree. */
    public static void openNodeMenu(Player player, String tree) {
        nation kingdom = getnation(player);
        if (kingdom == null) {
            player.sendMessage("§c소속된 국가가 없습니다.");
            return;
        }
        Inventory inv = Bukkit.createInventory(new NodeHolder(kingdom, tree), 54, tree + " 연구");
        NodeHolder holder = (NodeHolder) inv.getHolder();
        holder.setInventory(inv);

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta m = info.getItemMeta();
        m.setDisplayName(tree);
        m.setLore(List.of("§7선택된 트리"));
        info.setItemMeta(m);
        inv.setItem(4, info);

        List<ResearchNode> list = nodes.values().stream()
                .filter(n -> n.getTree().equalsIgnoreCase(tree))
                .sorted(Comparator.comparingInt(ResearchNode::getTier))
                .toList();
        for (ResearchNode node : list) {
            int slot = getSlotForNode(node);
            if (slot >= 0) inv.setItem(slot, createNodeItem(kingdom, node));
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta bm = back.getItemMeta();
        bm.setDisplayName("§c돌아가기");
        back.setItemMeta(bm);
        inv.setItem(53, back);

        player.openInventory(inv);
    }

    /** Determine slot index for node in GUI. */
    private static int getSlotForNode(ResearchNode node) {
        return switch (node.getTier()) {
            case 1 -> node.getId().endsWith("1") ? 19 : 28;
            case 2 -> node.getId().endsWith("1") ? 21 : 30;
            case 3 -> node.getId().endsWith("1") ? 23 : 32;
            case 4 -> {
                List<ResearchNode> t4 = nodes.values().stream()
                        .filter(n -> n.getTree().equals(node.getTree()) && n.getTier() == 4)
                        .sorted(Comparator.comparing(ResearchNode::getId))
                        .toList();
                int idx = t4.indexOf(node);
                yield 16 + idx * 9;
            }
            default -> -1;
        };
    }

    /** Create node item with state-specific display. */
    private static ItemStack createNodeItem(nation k, ResearchNode node) {
        ResearchState state = getState(k, node);
        ItemStack item = switch (state) {
            case LOCKED -> new ItemStack(Material.BARRIER);
            case AVAILABLE, IN_PROGRESS -> new ItemStack(Material.BOOK);
            case COMPLETED -> new ItemStack(Material.ENCHANTED_BOOK);
        };
        ItemMeta meta = item.getItemMeta();
        String color = switch (state) {
            case LOCKED -> "§7";
            case AVAILABLE -> "§f";
            case IN_PROGRESS -> "§e";
            case COMPLETED -> "§a";
        };
        meta.setDisplayName(color + node.getId());
        List<String> lore = new ArrayList<>();
        lore.add("§7필요 골드: " + node.getCost());
        lore.add("§7필요 시간: " + node.getTime());
        String stateText = switch (state) {
            case LOCKED -> "🔒 잠김";
            case AVAILABLE -> "☑ 연구 가능";
            case IN_PROGRESS -> "🕓 진행 중";
            case COMPLETED -> "✅ 완료됨";
        };
        lore.add("§7상태: " + stateText);
        lore.add("§8효과: " + node.getEffect());
        meta.setLore(lore);
        if (state == ResearchState.IN_PROGRESS || state == ResearchState.COMPLETED) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }

    // ------------------------------------------------------- Actions

    /** Toggle tree selection on double click. */
    public static void toggleTreeSelect(Player player, String tree) {
        nation k = getnation(player);
        if (k == null || tree.equals("INFRA")) return;
        Set<String> set = k.getSelectedResearchTrees();
        if (set.contains(tree)) {
            set.remove(tree);
        } else {
            if (set.size() >= k.getResearchSlots()) {
                player.sendMessage("§c연구 슬롯이 부족합니다.");
                return;
            }
            set.add(tree);
        }
        nationStorage.save(k);
        openTreeSelect(player);
    }

    /** Start research on a node if possible. */
    public static void startResearch(Player player, ResearchNode node) {
        nation kingdom = getnation(player);
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
        if (node.getTier() == 4 && !kingdom.getSelectedT4Nodes().contains(node.getId())) {
            if (kingdom.getSelectedT4Nodes().size() >= 2) {
                player.sendMessage("§cT4 노드는 최대 두 개만 선택 가능합니다.");
                return;
            }
            kingdom.getSelectedT4Nodes().add(node.getId());
        }

        double cost = node.getGoldCost();
        if (kingdom.getTreasury() < cost) {
            player.sendMessage("§c국고가 부족합니다.");
            return;
        }

        kingdom.removeGold(cost);
        nationStorage.save(kingdom);

        player.sendMessage("§a연구를 시작합니다: " + node.getId());
        player.sendMessage("§e연구 비용 " + cost + "G 차감");

        long duration = parseDuration(node.getTime());
        ResearchTask task = new ResearchTask(kingdom, node, duration);
        tasks.put(kingdom.getName() + ":" + node.getId(), task);
        task.start();
        openNodeMenu(player, node.getTree());
    }

    /** Determine node state for the given kingdom. */
    private static ResearchState getState(nation k, ResearchNode node) {
        if (k.getResearchedNodes().contains(node.getId())) return ResearchState.COMPLETED;
        if (tasks.containsKey(k.getName() + ":" + node.getId())) return ResearchState.IN_PROGRESS;
        for (String pre : node.getPrereq()) {
            if (!k.getResearchedNodes().contains(pre)) return ResearchState.LOCKED;
        }
        if (node.getTier() == 4 && !k.getSelectedT4Nodes().contains(node.getId()) && k.getSelectedT4Nodes().size() >= 2) {
            return ResearchState.LOCKED;
        }
        return ResearchState.AVAILABLE;
    }

    /** Parse duration string like "1시간30분" to ticks. */
    private static long parseDuration(String time) {
        int hours = 0;
        int minutes = 0;
        if (time.contains("시간")) {
            try { hours = Integer.parseInt(time.split("시간")[0].trim()); } catch (Exception ignored) {}
            time = time.substring(time.indexOf("시간") + 2);
        }
        if (time.contains("분")) {
            try { minutes = Integer.parseInt(time.split("분")[0].trim()); } catch (Exception ignored) {}
        }
        Duration d = Duration.ofHours(hours).plusMinutes(minutes);
        return d.toSeconds() * 20L;
    }

    /** Resolve player's kingdom. */
    private static nation getnation(Player player) {
        Village v = VillageManager.getByPlayer(player.getUniqueId());
        if (v == null) return null;
        String kName = v.getnation();
        if (kName == null) return null;
        return nationManager.getByName(kName);
    }

    // ------------------------------------------------------- Holders

    static class TreeHolder implements InventoryHolder {
        private final nation kingdom;
        private Inventory inv;
        TreeHolder(nation kingdom) { this.kingdom = kingdom; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public nation getnation() { return kingdom; }
    }

    static class NodeHolder implements InventoryHolder {
        private final nation kingdom;
        private final String tree;
        private Inventory inv;
        NodeHolder(nation kingdom, String tree) { this.kingdom = kingdom; this.tree = tree; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public nation getnation() { return kingdom; }
        public String getTree() { return tree; }
    }

    // ------------------------------------------------------- Task

    /** Represents an active research task. */
    private static class ResearchTask extends BukkitRunnable {
        private final nation kingdom;
        private final ResearchNode node;
        private final long maxTicks;
        private long tick;
        private BossBar bar;

        ResearchTask(nation kingdom, ResearchNode node, long maxTicks) {
            this.kingdom = kingdom;
            this.node = node;
            this.maxTicks = maxTicks == 0 ? 20L : maxTicks;
        }

        void start() {
            bar = Bukkit.createBossBar("연구 진행: " + node.getId(), BarColor.BLUE, BarStyle.SEGMENTED_10);
            addPlayers(bar, kingdom);
            runTaskTimer(ContinentPlugin.getInstance(), 0L, 20L);
        }

        @Override
        public void run() {
            tick += 20L;
            if (bar != null) {
                bar.setProgress(Math.min(1.0, tick / (double) maxTicks));
            }
            if (tick >= maxTicks) {
                complete();
                cancel();
            }
        }

        private void complete() {
            if (bar != null) bar.removeAll();
            kingdom.getResearchedNodes().add(node.getId());
            if (node.getId().equals("INF_1_RESEARCH_SLOT_1") || node.getId().equals("INF_2_RESEARCH_SLOT_2")) {
                kingdom.setResearchSlots(kingdom.getResearchSlots() + 1);
            }
            nationStorage.save(kingdom);
            tasks.remove(kingdom.getName() + ":" + node.getId());
        }
    }

    /** Add all online players of a kingdom to a boss bar. */
    private static void addPlayers(BossBar bar, nation kingdom) {
        for (String vName : kingdom.getVillages()) {
            Village v = VillageManager.getByName(vName);
            if (v == null) continue;
            for (UUID uuid : v.getMembers()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null && p.isOnline()) {
                    bar.addPlayer(p);
                }
            }
        }
    }
}

