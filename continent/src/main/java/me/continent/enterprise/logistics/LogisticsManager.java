package me.continent.enterprise.logistics;

import java.util.*;

/** Simple manager for delivery entries. */
public class LogisticsManager {
    private static final Map<String, List<DeliveryEntry>> byEnterprise = new HashMap<>();

    public static void addShipment(String enterpriseId, DeliveryEntry entry) {
        byEnterprise.computeIfAbsent(enterpriseId, k -> new ArrayList<>()).add(entry);
    }

    public static List<DeliveryEntry> getShipments(String enterpriseId) {
        return new ArrayList<>(byEnterprise.getOrDefault(enterpriseId, Collections.emptyList()));
    }
}
