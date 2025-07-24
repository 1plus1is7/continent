package me.continent.enterprise.gui;

import me.continent.enterprise.Enterprise;
import me.continent.enterprise.production.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/** Listener for production menu interactions. */
public class ProductionMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof ProductionMenuService.Holder holder)) return;
        event.setCancelled(true);
        int slot = event.getRawSlot();
        Enterprise ent = holder.getEnterprise();
        ProductionJob job = ProductionManager.getJob(ent.getId());
        if (slot == 26) {
            me.continent.enterprise.gui.EnterpriseMenuService.openMain(player, ent);
            return;
        }
        if (job != null) {
            if (slot == 13 && job.isComplete()) {
                if (ProductionManager.collect(player, ent.getId())) {
                    player.sendMessage("§a제품이 창고에 보관되었습니다.");
                }
                ProductionMenuService.open(player, ent);
            }
            return;
        }
        var list = ProductionManager.getRecipes(ent.getType());
        if (slot >= 0 && slot < list.size()) {
            ProductionRecipe recipe = list.get(slot);
            boolean ok = ProductionManager.startJob(player, ent.getId(), recipe);
            if (!ok) player.sendMessage("§c자원이 부족하거나 슬롯이 가득 찼습니다.");
            ProductionMenuService.open(player, ent);
        }
    }
}
