package me.continent.enterprise;

/** Holds configurable data for an enterprise type. */
public class EnterpriseTypeInfo {
    private final EnterpriseType type;
    private final String name;
    private final double cost;

    public EnterpriseTypeInfo(EnterpriseType type, String name, double cost) {
        this.type = type;
        this.name = name;
        this.cost = cost;
    }

    public EnterpriseType getType() { return type; }
    public String getName() { return name; }
    public double getCost() { return cost; }
}
