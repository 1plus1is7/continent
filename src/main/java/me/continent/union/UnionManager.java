package me.continent.union;

import java.util.*;

/**
 * Manages all unions in memory.
 */
public class UnionManager {
    private static final Map<String, Union> unionsByName = new HashMap<>();
    private static final Map<String, Union> unionsByNation = new HashMap<>();

    public static Collection<Union> getAll() {
        return unionsByName.values();
    }

    public static Union getByName(String name) {
        return unionsByName.get(name.toLowerCase());
    }

    public static Union getByNation(String nation) {
        return unionsByNation.get(nation.toLowerCase());
    }

    public static boolean exists(String name) {
        return unionsByName.containsKey(name.toLowerCase());
    }

    public static Union createUnion(String name, String leader) {
        if (exists(name)) return null;
        if (unionsByNation.containsKey(leader.toLowerCase())) return null;
        Union u = new Union(name, leader);
        register(u);
        return u;
    }

    public static void register(Union union) {
        unionsByName.put(union.getName().toLowerCase(), union);
        for (String n : union.getNations()) {
            unionsByNation.put(n.toLowerCase(), union);
        }
    }

    public static void unregister(Union union) {
        unionsByName.remove(union.getName().toLowerCase());
        for (String n : union.getNations()) {
            unionsByNation.remove(n.toLowerCase());
        }
    }

    public static void addNation(Union union, String nation) {
        union.addNation(nation);
        unionsByNation.put(nation.toLowerCase(), union);
    }

    public static void removeNation(Union union, String nation) {
        union.removeNation(nation);
        unionsByNation.remove(nation.toLowerCase());
    }
}
