package me.continent.season;

import me.continent.ContinentPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.*;
import java.util.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class SeasonManager implements TabExecutor {
    private static ContinentPlugin plugin;
    private static Season currentSeason = Season.SPRING;
    private static final EnumMap<Season, Integer> seasonDurations = new EnumMap<>(Season.class);
    private static int daysLeft = 0;
    private static final Set<ChunkCoord> processedChunks = ConcurrentHashMap.newKeySet();
    private static File dataFile;
    private static int taskId = -1;
    private static boolean enabled = true;
    private static boolean rainySeason = false;
    private static int rainyStart = -1;
    private static int rainyDays = 0;

    public static void init(ContinentPlugin pl) {
        plugin = pl;
        dataFile = new File(plugin.getDataFolder(), "season.yml");
        loadData();
        SeasonLeafManager.init(plugin);
        LeafPileManager.init(plugin);
        if (enabled) {
            startScheduler();
            SeasonVisuals.start();
        }
    }

    public static void shutdown() {
        SeasonLeafManager.save();
        LeafPileManager.save();
        saveData();
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
        SeasonVisuals.stop();
    }

    private static void startScheduler() {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
        long dayTicks = 24000L;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, SeasonManager::tick, dayTicks, dayTicks);
    }

    private static void tick() {
        daysLeft--;
        if (currentSeason == Season.SUMMER) {
            if (!rainySeason && rainyStart != -1 && daysLeft == rainyStart) {
                startRainySeason();
            } else if (rainySeason) {
                if (rainyDays <= 0) {
                    stopRainySeason();
                } else {
                    rainyDays--;
                    for (World world : Bukkit.getWorlds()) {
                        world.setStorm(true);
                        world.setWeatherDuration(24000);
                    }
                }
            }
        }
        if (daysLeft <= 0) {
            skipSeason();
        }
    }

    public static Season getCurrentSeason() {
        return currentSeason;
    }

    public static int getParticleCount(Season s) {
        String key = "season.particles." + s.name().toLowerCase();
        return plugin.getConfig().getInt(key, 10);
    }

    public static int getDaysLeft() {
        return daysLeft;
    }

    public static int getProcessedChunkCount() {
        return processedChunks.size();
    }

    public static boolean isRainySeason() {
        return rainySeason;
    }

    public static void skipSeason() {
        setSeason(currentSeason.next());
    }

    public static void setSeason(Season season) {
        currentSeason = season;
        daysLeft = seasonDurations.getOrDefault(season, 1);
        processedChunks.clear();
        switch (season) {
            case AUTUMN -> {
                for (World world : Bukkit.getWorlds()) {
                    SeasonLeafManager.generateLeavesAsync(Arrays.asList(world.getLoadedChunks()), processedChunks);
                }
                LeafPileManager.start();
                stopRainySeason();
            }
            case WINTER -> {
                SeasonLeafManager.removeLeafPiles();
                LeafPileManager.removeAll();
                stopRainySeason();
            }
            case SPRING -> {
                SeasonTreeController.onSpring();
                LeafPileManager.stop();
                stopRainySeason();
            }
            case SUMMER -> {
                SeasonTreeController.onSummer();
                scheduleRainySeason();
                LeafPileManager.stop();
            }
        }
        SeasonVisuals.start();
    }

    public static void reloadConfig() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();
        enabled = cfg.getBoolean("season.enabled", true);
        for (Season s : Season.values()) {
            int days = cfg.getInt("season.durations." + s.name().toLowerCase(), 1);
            seasonDurations.put(s, days);
        }
        if (enabled) {
            if (taskId == -1) {
                startScheduler();
                SeasonVisuals.start();
            }
        } else {
            if (taskId != -1) {
                Bukkit.getScheduler().cancelTask(taskId);
                taskId = -1;
                SeasonVisuals.stop();
            }
        }
    }

    private static void loadData() {
        reloadConfig();
        if (dataFile.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
            try {
                currentSeason = Season.valueOf(cfg.getString("season", "SPRING"));
            } catch (IllegalArgumentException e) {
                currentSeason = Season.SPRING;
            }
            daysLeft = cfg.getInt("daysLeft", seasonDurations.getOrDefault(currentSeason, 1));
        } else {
            currentSeason = Season.SPRING;
            daysLeft = seasonDurations.getOrDefault(currentSeason, 1);
        }
    }

    private static void saveData() {
        FileConfiguration cfg = new YamlConfiguration();
        cfg.set("season", currentSeason.name());
        cfg.set("daysLeft", daysLeft);
        try {
            cfg.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scheduleRainySeason() {
        int total = seasonDurations.getOrDefault(Season.SUMMER, daysLeft);
        rainyDays = 3 + new Random().nextInt(3); // 3-5
        int maxStart = Math.max(0, total - rainyDays);
        rainyStart = total - new Random().nextInt(maxStart + 1);
        rainySeason = false;
    }

    private static void startRainySeason() {
        rainySeason = true;
        rainyDays--; // current day counts as first
        for (World world : Bukkit.getWorlds()) {
            world.setStorm(true);
            world.setWeatherDuration(24000);
        }
    }

    private static void stopRainySeason() {
        if (!rainySeason && rainyStart == -1) return;
        rainySeason = false;
        rainyStart = -1;
        rainyDays = 0;
        for (World world : Bukkit.getWorlds()) {
            world.setStorm(false);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6[Season Command]");
            sender.sendMessage("§e/season info §7- 현재 시즌 정보");
            sender.sendMessage("§e/season set <season> §7- 시즌 설정");
            sender.sendMessage("§e/season skip §7- 다음 시즌으로 이동");
            sender.sendMessage("§e/season reload §7- 설정 리로드");
            sender.sendMessage("§e/season rain §7- 우기 토글");
            sender.sendMessage("§e/season toggle <on|off> §7- 계절 시스템 토글");
            return true;
        }
        if (args[0].equalsIgnoreCase("info")) {
            sender.sendMessage("§6[Season] §f현재 시즌: §e" + currentSeason);
            sender.sendMessage("§6[Season] §f남은 일수: §e" + daysLeft);
            sender.sendMessage("§6[Season] §f처리된 청크: §e" + processedChunks.size());
            return true;
        }
        if (args[0].equalsIgnoreCase("set") && args.length >= 2) {
            try {
                Season season = Season.valueOf(args[1].toUpperCase());
                setSeason(season);
                sender.sendMessage("§a시즌을 " + season + " 로 설정했습니다.");
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c잘못된 시즌입니다.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("skip")) {
            skipSeason();
            sender.sendMessage("§a다음 시즌으로 이동했습니다: " + currentSeason);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage("§a시즌 설정을 리로드했습니다.");
            return true;
        }
        if (args[0].equalsIgnoreCase("rain")) {
            if (rainySeason) {
                stopRainySeason();
                sender.sendMessage("§a우기를 종료했습니다.");
            } else {
                rainyDays = 3;
                rainyStart = 0;
                startRainySeason();
                sender.sendMessage("§a우기를 시작했습니다.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("toggle")) {
            boolean enable;
            if (args.length >= 2) {
                enable = args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("true");
            } else {
                enable = !enabled;
            }
            if (enable) {
                enabled = true;
                startScheduler();
                SeasonVisuals.start();
                sender.sendMessage("§a계절 시스템을 활성화했습니다.");
            } else {
                enabled = false;
                if (taskId != -1) {
                    Bukkit.getScheduler().cancelTask(taskId);
                    taskId = -1;
                }
                SeasonVisuals.stop();
                sender.sendMessage("§c계절 시스템을 비활성화했습니다.");
            }
            return true;
        }
        sender.sendMessage("§c알 수 없는 하위 명령어입니다.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subs = Arrays.asList("info", "set", "skip", "reload", "rain", "toggle");

        if (args.length == 1) {
            return subs.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return Arrays.stream(Season.values())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .filter(n -> n.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            return Arrays.asList("on", "off").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
