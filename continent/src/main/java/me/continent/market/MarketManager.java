package me.continent.market;

import me.continent.ContinentPlugin;
import me.continent.utils.ItemSerialization;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class MarketManager {
    private static final List<MarketItem> items = new ArrayList<>();

    private static File file;
    private static YamlConfiguration config;

    public enum SortMode { NEWEST, PRICE }
    public enum FilterMode { ALL, NORMAL, CORPORATE }

    public static void load(ContinentPlugin plugin) {
        file = new File(plugin.getDataFolder(), "market.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        items.clear();
        List<Map<?, ?>> list = config.getMapList("items");
        for (Map<?, ?> m : list) {
            try {
                UUID id = UUID.fromString(String.valueOf(m.get("id")));
                UUID seller = UUID.fromString(String.valueOf(m.get("seller")));
                ItemStack item = ItemSerialization.deserializeItem(String.valueOf(m.get("item")));
                Object priceObj = m.get("price");
                int price = priceObj instanceof Number ? ((Number) priceObj).intValue() : Integer.parseInt(String.valueOf(priceObj));
                Object stockObj = m.get("stock");
                int stock = stockObj instanceof Number ? ((Number) stockObj).intValue() : Integer.parseInt(String.valueOf(stockObj));
                String time = String.valueOf(m.get("time"));
                LocalDateTime listed = LocalDateTime.parse(time);
                String ent = m.containsKey("enterprise") ? String.valueOf(m.get("enterprise")) : null;
                MarketItem mi;
                if (ent == null || ent.equals("null")) mi = new MarketItem(id, seller, item, price, stock, listed);
                else mi = new MarketItem(id, seller, ent, item, price, stock, listed);
                items.add(mi);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to load market item: " + e.getMessage());
            }
        }
    }

    public static void save() {
        if (config == null || file == null) return;
        List<Map<String, Object>> list = new ArrayList<>();
        for (MarketItem item : items) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", item.getId().toString());
            m.put("seller", item.getSeller().toString());
            m.put("item", ItemSerialization.serializeItem(item.getItem()));
            m.put("price", item.getPricePerUnit());
            m.put("stock", item.getStock());
            m.put("time", item.getListedAt().toString());
            if (item.getEnterpriseId() != null)
                m.put("enterprise", item.getEnterpriseId());
            list.add(m);
        }
        config.set("items", list);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addItem(MarketItem item) {
        items.add(item);
        save();
    }

    public static void removeItem(UUID id) {
        items.removeIf(i -> i.getId().equals(id));
        save();
    }

    public static List<MarketItem> getItems() {
        return new ArrayList<>(items);
    }

    public static MarketItem getItem(UUID id) {
        for (MarketItem item : items) {
            if (item.getId().equals(id)) return item;
        }
        return null;
    }

    public static List<MarketItem> getFilteredSorted(SortMode mode, FilterMode filter) {
        List<MarketItem> list = getItems();
        if (filter == FilterMode.NORMAL) {
            list = list.stream().filter(i -> i.getEnterpriseId() == null).toList();
        } else if (filter == FilterMode.CORPORATE) {
            list = list.stream().filter(i -> i.getEnterpriseId() != null).toList();
        }
        if (mode == SortMode.PRICE) {
            list.sort(Comparator.comparingInt(MarketItem::getPricePerUnit));
        } else {
            list.sort(Comparator.comparing(MarketItem::getListedAt).reversed());
        }
        return list;
    }
}
