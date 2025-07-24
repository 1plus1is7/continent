package me.continent.enterprise.contract;

import me.continent.ContinentPlugin;
import org.bukkit.Bukkit;

import java.util.*;

/** Manages creation and tracking of contracts. */
public class ContractManager {
    private static final List<Contract> available = new ArrayList<>();
    private static final Map<String, List<Contract>> byEnterprise = new HashMap<>();

    private static List<Contract> internalList(String id) {
        return byEnterprise.computeIfAbsent(id, k -> new ArrayList<>());
    }

    static void clearAll() {
        available.clear();
        byEnterprise.clear();
    }

    static void addToEnterprise(String id, Contract c) {
        internalList(id).add(c);
    }

    public static List<Contract> getAvailable() {
        return new ArrayList<>(available);
    }

    public static List<Contract> getForEnterprise(String id) {
        return new ArrayList<>(byEnterprise.getOrDefault(id, Collections.emptyList()));
    }

    public static Contract get(UUID id) {
        for (Contract c : available) {
            if (c.getId().equals(id)) return c;
        }
        for (List<Contract> list : byEnterprise.values()) {
            for (Contract c : list) {
                if (c.getId().equals(id)) return c;
            }
        }
        return null;
    }

    public static void addAvailable(Contract c) {
        available.add(c);
    }

    /** Accept available contract for an enterprise. */
    public static boolean accept(UUID id, String enterpriseId) {
        Iterator<Contract> it = available.iterator();
        while (it.hasNext()) {
            Contract c = it.next();
            if (c.getId().equals(id)) {
                it.remove();
                c.setState(ContractState.ACCEPTED);
                c.setEnterpriseId(enterpriseId);
                internalList(enterpriseId).add(c);
                ContractStorage.save(c);
                return true;
            }
        }
        return false;
    }

    public static void complete(UUID id) {
        Contract c = get(id);
        if (c != null) {
            c.setState(ContractState.COMPLETED);
            ContractStorage.save(c);
        }
    }

    public static void loadAll() {
        ContractStorage.loadAll();
    }

    public static void saveAll() {
        for (Contract c : available) ContractStorage.save(c);
        for (List<Contract> list : byEnterprise.values())
            for (Contract c : list) ContractStorage.save(c);
    }

    /** Generate some random daily contracts. */
    public static void generateDaily() {
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            ContractGrade grade = ContractGrade.values()[r.nextInt(ContractGrade.values().length)];
            int reward = switch (grade) {
                case S -> 500;
                case A -> 300;
                case B -> 150;
                case C -> 80;
                case D -> 30;
            };
            long due = System.currentTimeMillis() + (2 + r.nextInt(5)) * 86400000L;
            Contract c = new Contract(UUID.randomUUID(), "SYSTEM",
                    "자동 계약", "ITEM", 1 + r.nextInt(100), reward, due, grade);
            addAvailable(c);
        }
    }

    public static void scheduleGeneration() {
        long dayTicks = 24L * 60 * 60 * 20;
        Bukkit.getScheduler().runTaskTimer(ContinentPlugin.getInstance(), ContractManager::generateDaily, dayTicks, dayTicks);
    }
}
