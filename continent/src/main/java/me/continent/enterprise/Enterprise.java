package me.continent.enterprise;

import java.util.UUID;

/** Basic enterprise data model. */
public class Enterprise {
    private final String id;
    private String name;
    private final EnterpriseType type;
    private final UUID owner;
    private final long registeredAt;

    public Enterprise(String id, String name, EnterpriseType type, UUID owner, long registeredAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.registeredAt = registeredAt;
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
}
