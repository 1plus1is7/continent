package me.continent.market;

import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.UUID;

public class MarketItem {
    private UUID id;
    private UUID seller;
    private ItemStack item;
    private int pricePerUnit;
    private int stock;
    private LocalDateTime listedAt;

    public MarketItem(UUID id, UUID seller, ItemStack item, int pricePerUnit, int stock, LocalDateTime listedAt) {
        this.id = id;
        this.seller = seller;
        this.item = item;
        this.pricePerUnit = pricePerUnit;
        this.stock = stock;
        this.listedAt = listedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSeller() {
        return seller;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(int pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDateTime getListedAt() {
        return listedAt;
    }
}
