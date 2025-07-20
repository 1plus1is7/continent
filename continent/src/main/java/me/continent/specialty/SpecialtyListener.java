package me.continent.specialty;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class SpecialtyListener implements Listener {
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem() == null || !event.getItem().hasItemMeta()) return;
        if (!event.getItem().getItemMeta().hasCustomModelData()) return;
        int cmd = event.getItem().getItemMeta().getCustomModelData();
        SpecialtyGood good = null;
        for (SpecialtyGood g : SpecialtyManager.getAll()) {
            if (g.getModelData() == cmd) {
                good = g;
                break;
            }
        }
        if (good == null) return;
        if (good.getHunger() > 0) {
            event.getPlayer().setFoodLevel(Math.min(20, event.getPlayer().getFoodLevel() + good.getHunger()));
        }
    }
}
