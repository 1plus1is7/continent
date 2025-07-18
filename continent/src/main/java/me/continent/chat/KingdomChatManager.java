package me.continent.chat;

import me.continent.ContinentPlugin;
import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for kingdom chat operations and logging.
 */
public class KingdomChatManager {
    private static final File folder = new File(ContinentPlugin.getInstance().getDataFolder(), "chatlogs");

    static {
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void sendMessage(Kingdom kingdom, UUID sender, String message) {
        String senderName = Bukkit.getOfflinePlayer(sender).getName();
        if (senderName == null) {
            senderName = sender.toString().substring(0, 8);
        }
        String formatted = "§6[국가채팅] §f" + senderName + " : " + message;

        for (UUID member : kingdom.getMembers()) {
            Player p = Bukkit.getPlayer(member);
            if (p != null && p.isOnline()) {
                p.sendMessage(formatted);
            }
        }
        logMessage(kingdom.getName(), senderName + ": " + message);
    }

    private static void logMessage(String kingdomName, String content) {
        File file = new File(folder, kingdomName.toLowerCase() + ".log");
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write("[" + time + "] " + content + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
