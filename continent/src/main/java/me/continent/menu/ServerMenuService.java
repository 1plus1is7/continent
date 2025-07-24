package me.continent.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;


public class ServerMenuService {
    public static void openMenu(Player player) {
        MenuHolder holder = new MenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 54, "Server Menu");
        holder.setInventory(inv);
        fill(inv);

        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta sMeta = sword.getItemMeta();
        sMeta.setDisplayName("§a국가 메뉴 열기");
        sMeta.setCustomModelData(0);
        sword.setItemMeta(sMeta);
        inv.setItem(11, sword);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta hMeta = (SkullMeta) head.getItemMeta();
        hMeta.setOwningPlayer(player);
        hMeta.setDisplayName("§a플레이어 정보");
        hMeta.setCustomModelData(0);
        head.setItemMeta(hMeta);
        inv.setItem(14, head);

        ItemStack rawGold = new ItemStack(Material.RAW_GOLD);
        ItemMeta gMeta = rawGold.getItemMeta();
        gMeta.setDisplayName("§aMarket 메뉴 열기");
        gMeta.setCustomModelData(0);
        rawGold.setItemMeta(gMeta);
        inv.setItem(17, rawGold);

        ItemStack bundle = new ItemStack(Material.BUNDLE);
        ItemMeta bMeta = bundle.getItemMeta();
        bMeta.setDisplayName("§a번들");
        bMeta.setCustomModelData(0);
        bundle.setItemMeta(bMeta);
        inv.setItem(38, bundle);

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta cMeta = compass.getItemMeta();
        cMeta.setDisplayName("§a워프 기능");
        cMeta.setCustomModelData(0);
        compass.setItemMeta(cMeta);
        inv.setItem(41, compass);

        ItemStack cart = new ItemStack(Material.MINECART);
        ItemMeta cartMeta = cart.getItemMeta();
        cartMeta.setDisplayName("§a직업 기능");
        cartMeta.setCustomModelData(0);
        cart.setItemMeta(cartMeta);
        inv.setItem(44, cart);

        player.openInventory(inv);
    }

    private static void fill(Inventory inv) {
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, pane);
        }
    }

    static class MenuHolder implements InventoryHolder {
        private Inventory inv;
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
    }
}
