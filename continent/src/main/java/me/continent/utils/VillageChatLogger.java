package me.continent.utils;

import java.io.File;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VillageChatLogger {

    // 로그 파일이 저장될 폴더를 미리 생성해 둔다
    private static final File folder = new File("plugins/Continent/village-logs");

    static {
        if (!folder.exists()) folder.mkdirs();
    }

    // 마을 채팅 로그 기록
    public static void logMessage(String villageName, String playerName, String message) {
        Path logFile = new File(folder, villageName + ".log").toPath();
        try (BufferedWriter writer = Files.newBufferedWriter(logFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] " + playerName + ": " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
