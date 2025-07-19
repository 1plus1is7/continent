package me.continent.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfirmationManager {
    private static class Confirmation {
        private final Runnable action;
        private final long expire;

        Confirmation(Runnable action, long expire) {
            this.action = action;
            this.expire = expire;
        }
    }

    private static final Map<UUID, Confirmation> pending = new HashMap<>();
    private static final long TIMEOUT = 10000; // 10 seconds

    public static void request(Player player, Runnable action) {
        pending.put(player.getUniqueId(), new Confirmation(action, System.currentTimeMillis() + TIMEOUT));
        player.sendMessage("§c정말로 실행하시겠습니까? §e/village confirm§c을 입력하세요.");
    }

    public static boolean confirm(Player player) {
        UUID uuid = player.getUniqueId();
        Confirmation c = pending.remove(uuid);
        if (c == null) {
            return false;
        }
        if (System.currentTimeMillis() > c.expire) {
            player.sendMessage("§c확인 시간이 초과되었습니다.");
            return true;
        }
        c.action.run();
        return true;
    }
}
