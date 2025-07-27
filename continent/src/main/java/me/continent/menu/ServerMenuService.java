package me.continent.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;


public class ServerMenuService {
    public static void openMenu(Player player) {
        MenuHolder holder = new MenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 54, "\uE000§f\uE001");
        holder.setInventory(inv);

        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta sMeta = sword.getItemMeta();
        sMeta.setDisplayName("§a국가 메뉴 열기");
        CustomModelDataComponent swordCmd = sMeta.getCustomModelDataComponent();
        swordCmd.setStrings(java.util.List.of("0"));
        sMeta.setCustomModelDataComponent(swordCmd);
        sword.setItemMeta(sMeta);
        inv.setItem(10, sword);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta hMeta = (SkullMeta) head.getItemMeta();
        hMeta.setOwningPlayer(player);
        hMeta.setDisplayName("§a플레이어 정보");
        CustomModelDataComponent headCmd = hMeta.getCustomModelDataComponent();
        headCmd.setStrings(java.util.List.of("0"));
        hMeta.setCustomModelDataComponent(headCmd);
        head.setItemMeta(hMeta);
        inv.setItem(13, head);

        ItemStack rawGold = new ItemStack(Material.RAW_GOLD);
        ItemMeta gMeta = rawGold.getItemMeta();
        gMeta.setDisplayName("§a크라운 메뉴 열기");
        CustomModelDataComponent goldCmd = gMeta.getCustomModelDataComponent();
        goldCmd.setStrings(java.util.List.of("0"));
        gMeta.setCustomModelDataComponent(goldCmd);
        rawGold.setItemMeta(gMeta);
        inv.setItem(16, rawGold);

        ItemStack bundle = new ItemStack(Material.BUNDLE);
        ItemMeta bMeta = bundle.getItemMeta();
        bMeta.setDisplayName("§aMarket 메뉴 열기");
        CustomModelDataComponent bundleCmd = bMeta.getCustomModelDataComponent();
        bundleCmd.setStrings(java.util.List.of("0"));
        bMeta.setCustomModelDataComponent(bundleCmd);
        bundle.setItemMeta(bMeta);
        inv.setItem(37, bundle);

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta cMeta = compass.getItemMeta();
        cMeta.setDisplayName("§a워프 기능");
        CustomModelDataComponent compassCmd = cMeta.getCustomModelDataComponent();
        compassCmd.setStrings(java.util.List.of("0"));
        cMeta.setCustomModelDataComponent(compassCmd);
        compass.setItemMeta(cMeta);
        inv.setItem(40, compass);

        ItemStack cart = new ItemStack(Material.MINECART);
        ItemMeta cartMeta = cart.getItemMeta();
        cartMeta.setDisplayName("§a직업 기능");
        CustomModelDataComponent cartCmd = cartMeta.getCustomModelDataComponent();
        cartCmd.setStrings(java.util.List.of("0"));
        cartMeta.setCustomModelDataComponent(cartCmd);
        cart.setItemMeta(cartMeta);
        inv.setItem(43, cart);

        player.openInventory(inv);
    }


    static class MenuHolder implements InventoryHolder {
        private Inventory inv;
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
    }
}
