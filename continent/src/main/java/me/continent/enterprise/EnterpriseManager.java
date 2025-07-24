package me.continent.enterprise;

import java.util.*;

/** In-memory registry of all enterprises. */
public class EnterpriseManager {
    private static final Map<String, Enterprise> enterprises = new HashMap<>();
    private static final Map<UUID, Set<Enterprise>> byOwner = new HashMap<>();

    public static void register(Enterprise enterprise) {
        enterprises.put(enterprise.getId(), enterprise);
        byOwner.computeIfAbsent(enterprise.getOwner(), k -> new HashSet<>()).add(enterprise);
    }

    public static Collection<Enterprise> getAll() {
        return enterprises.values();
    }

    public static Set<Enterprise> getByOwner(UUID owner) {
        return byOwner.getOrDefault(owner, Collections.emptySet());
    }

    public static Enterprise get(String id) {
        return enterprises.get(id);
    }

    public static boolean nameExists(String name) {
        for (Enterprise e : enterprises.values()) {
            if (e.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    /** Check if the player owns at least one enterprise. */
    public static boolean hasEnterprise(UUID owner) {
        Set<Enterprise> set = byOwner.get(owner);
        return set != null && !set.isEmpty();
    }

    public static void clear() {
        enterprises.clear();
        byOwner.clear();
    }
}
