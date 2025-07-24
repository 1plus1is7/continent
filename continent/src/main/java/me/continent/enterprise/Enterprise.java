package me.continent.enterprise;

import java.util.UUID;

/** Basic enterprise data model. */
public class Enterprise {
    private final String id;
    private String name;
    private final EnterpriseType type;
    private final UUID owner;
    private final long registeredAt;
    // Enterprise symbol item (banner)
    private org.bukkit.inventory.ItemStack symbol;

    public Enterprise(String id, String name, EnterpriseType type, UUID owner, long registeredAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.registeredAt = registeredAt;
        // default symbol is a white banner
        this.symbol = new org.bukkit.inventory.ItemStack(org.bukkit.Material.WHITE_BANNER);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnterpriseType getType() {
        return type;
    }

    public UUID getOwner() {
        return owner;
    }

    public long getRegisteredAt() {
        return registeredAt;
    }

    public org.bukkit.inventory.ItemStack getSymbol() {
        return symbol;
    }

    public void setSymbol(org.bukkit.inventory.ItemStack symbol) {
        if (symbol != null && symbol.getType().name().endsWith("BANNER")) {
            this.symbol = symbol;
        }
    }
}
