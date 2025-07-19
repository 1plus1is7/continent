package me.continent.kingdom.service;

import me.continent.ContinentPlugin;
import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.storage.KingdomStorage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

public class MaintenanceService {
    private static double cost;
    private static int unpaidLimit;
    private static double perChunkCost;

    public static void init(FileConfiguration config) {
        cost = config.getDouble("maintenance.cost", 20.0);
        unpaidLimit = config.getInt("maintenance.unpaid-limit", 2);
        perChunkCost = config.getDouble("maintenance.per-chunk-cost", 5.0);
    }

    public static double getCost() {
        return cost;
    }

    public static double getWeeklyCost(Kingdom kingdom) {
        if (!kingdom.isNation()) {
            return cost;
        }
        int extra = Math.max(0, kingdom.getClaimedChunks().size() - 16);
        return cost + extra * perChunkCost;
    }

    public static void schedule() {
        long delay = ticksUntilNext();
        long week = 7L * 24 * 60 * 60 * 20;
        Bukkit.getScheduler().runTaskTimer(ContinentPlugin.getInstance(), MaintenanceService::run, delay, week);
    }

    private static long ticksUntilNext() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime target = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .withHour(21).withMinute(0).withSecond(0).withNano(0);
        if (target.isBefore(now)) {
            target = target.plusWeeks(1);
        }
        long seconds = Duration.between(now, target).getSeconds();
        return seconds * 20;
    }

    private static void run() {
        for (Kingdom kingdom : KingdomManager.getAll()) {
            charge(kingdom);
        }
    }

    private static void charge(Kingdom kingdom) {
        double chargeAmount = getWeeklyCost(kingdom);
        if (kingdom.getTreasury() >= chargeAmount) {
            kingdom.removeGold(chargeAmount);
            kingdom.setUnpaidWeeks(0);
        } else {
            kingdom.setUnpaidWeeks(kingdom.getUnpaidWeeks() + 1);
            if (kingdom.getUnpaidWeeks() >= unpaidLimit) {
                MembershipService.disband(kingdom);
                Bukkit.broadcastMessage("§c마을 " + kingdom.getName() + "이(가) 유지비 미납으로 해산되었습니다.");
                return;
            }
        }
        kingdom.setMaintenanceCount(kingdom.getMaintenanceCount() + 1);
        kingdom.setLastMaintenance(System.currentTimeMillis());
        KingdomStorage.save(kingdom);

        Player king = Bukkit.getPlayer(kingdom.getKing());
        if (king != null) {
            king.sendMessage("§a마을 유지비 " + chargeAmount + "G가 차감되었습니다.");
        }
    }
}
