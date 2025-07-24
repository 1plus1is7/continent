package me.continent.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.bukkit.util.io.BukkitObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemSerialization {
    public static String serializeItem(ItemStack item) {
        if (item == null) return "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ItemStack deserializeItem(String data) {
        if (data == null || data.isEmpty()) return null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
