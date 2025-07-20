package me.continent.crop;

import org.bukkit.Material;

public enum CropType {
    POTATO(Material.POTATOES, 3.0, 2.0),
    WHEAT(Material.WHEAT, 2.0, 1.5),
    BEET(Material.BEETROOTS, 4.0, 3.0),
    CARROT(Material.CARROTS, 3.0, 2.0);

    private final Material material;
    private final double baseDays;
    private final double rainyDays;

    CropType(Material material, double baseDays, double rainyDays) {
        this.material = material;
        this.baseDays = baseDays;
        this.rainyDays = rainyDays;
    }

    public Material getMaterial() {
        return material;
    }

    public double getBaseDays() {
        return baseDays;
    }

    public double getRainyDays() {
        return rainyDays;
    }

    public static CropType fromMaterial(Material mat) {
        for (CropType type : values()) {
            if (type.material == mat) return type;
        }
        return null;
    }
}
