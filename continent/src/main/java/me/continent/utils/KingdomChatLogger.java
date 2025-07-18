package me.continent.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KingdomChatLogger {

    public static void logMessage(String kingdomName, String playerName, String message) {
        try {
            File folder = new File("plugins/Continent/kingdom-logs");
            if (!folder.exists()) folder.mkdirs();

            File logFile = new File(folder, kingdomName + ".log");
            FileWriter writer = new FileWriter(logFile, true);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] " + playerName + ": " + message + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
