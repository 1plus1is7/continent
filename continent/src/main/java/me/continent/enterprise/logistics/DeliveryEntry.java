package me.continent.enterprise.logistics;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/** Information about an ongoing delivery. */
public class DeliveryEntry {
    private final UUID id;
    private final ItemStack item;
    private final String target;
    private final int amount;
    private DeliveryState state;
    private final long finishTime;
    private final int reward;

    public DeliveryEntry(UUID id, ItemStack item, String target, int amount, DeliveryState state, long finishTime, int reward) {
        this.id = id;
        this.item = item;
        this.target = target;
        this.amount = amount;
        this.state = state;
        this.finishTime = finishTime;
        this.reward = reward;
    }

    public UUID getId() { return id; }
    public ItemStack getItem() { return item; }
    public String getTarget() { return target; }
    public int getAmount() { return amount; }
    public DeliveryState getState() { return state; }
    public void setState(DeliveryState state) { this.state = state; }
    public long getFinishTime() { return finishTime; }
    public int getReward() { return reward; }
}
