package me.continent.command;

import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.stat.PlayerStats;
import me.continent.stat.StatType;
import me.continent.stat.StatsEffectManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }
        PlayerData data = PlayerDataManager.get(player.getUniqueId());
        PlayerStats stats = data.getStats();

        if (args.length == 0) {
            player.sendMessage("§6[Stat] 현재 스탯");
            for (StatType type : StatType.values()) {
                player.sendMessage("§e" + type.name() + ": §f" + stats.get(type));
            }
            player.sendMessage("남은 포인트: " + stats.getUnusedPoints());
            return true;
        }
        if (args[0].equalsIgnoreCase("add") && args.length >= 2) {
            if (stats.getUnusedPoints() <= 0) {
                player.sendMessage("§c사용 가능한 포인트가 없습니다.");
                return true;
            }
            try {
                StatType type = StatType.valueOf(args[1].toUpperCase(Locale.ROOT));
                int current = stats.get(type);
                int limit = 10;
                if (stats.getMastery() == type) limit = 15;
                if (current >= limit) {
                    player.sendMessage("§c이미 최대 수치입니다.");
                    return true;
                }
                if (current + 1 > 10 && stats.getMastery() == null) {
                    stats.setMastery(type);
                } else if (current + 1 > 10 && stats.getMastery() != type) {
                    player.sendMessage("§c이미 다른 스탯의 마스터리를 해금했습니다.");
                    return true;
                }
                stats.set(type, current + 1);
                stats.usePoint();
                PlayerDataManager.save(player.getUniqueId());
                StatsEffectManager.apply(player);
                player.sendMessage("§a" + type.name() + " +1 (" + stats.get(type) + ")");
            } catch (IllegalArgumentException e) {
                player.sendMessage("§c잘못된 스탯입니다.");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return List.of("add");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            List<String> list = new ArrayList<>();
            for (StatType type : StatType.values()) list.add(type.name().toLowerCase(Locale.ROOT));
            return list;
        }
        return List.of();
    }
}
