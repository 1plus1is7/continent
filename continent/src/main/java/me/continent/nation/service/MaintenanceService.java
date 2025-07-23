package me.continent.nation.service;

import me.continent.ContinentPlugin;
import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.storage.NationStorage;
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

    public static double getWeeklyCost(Nation nation) {
        int extra = Math.max(0, nation.getClaimedChunks().size() - 16);
        return cost + extra * perChunkCost;
    }

    public static double getPerChunkCost() {
        return perChunkCost;
    }

    public static int getUnpaidLimit() {
        return unpaidLimit;
    }

    public static void setCost(double value) {
        cost = Math.max(0, value);
    }

    public static void setPerChunkCost(double value) {
        perChunkCost = Math.max(0, value);
    }

    public static void setUnpaidLimit(int value) {
        unpaidLimit = Math.max(0, value);
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
        for (Nation nation : NationManager.getAll()) {
            charge(nation);
        }
    }

    private static void charge(Nation nation) {
        double chargeAmount = getWeeklyCost(nation);
        if (nation.getVault() >= chargeAmount) {
            nation.removeGold(chargeAmount);
            nation.setUnpaidWeeks(0);
        } else {
            nation.setUnpaidWeeks(nation.getUnpaidWeeks() + 1);
            if (nation.getUnpaidWeeks() >= unpaidLimit) {
                MembershipService.disband(nation);
                Bukkit.broadcastMessage("§c국가 " + nation.getName() + "이(가) 유지비 미납으로 해산되었습니다.");
                return;
            }
        }
        nation.setMaintenanceCount(nation.getMaintenanceCount() + 1);
        nation.setLastMaintenance(System.currentTimeMillis());
        NationStorage.save(nation);

        Player king = Bukkit.getPlayer(nation.getKing());
        if (king != null) {
            king.sendMessage("§a국가 유지비 " + chargeAmount + "G가 차감되었습니다.");
        }
    }

}
