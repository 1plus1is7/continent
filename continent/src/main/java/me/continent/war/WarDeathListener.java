package me.continent.war;

import me.continent.ContinentPlugin;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.entity.Player;


public class WarDeathListener implements Listener {

    private World getLobbyWorld() {
        String name = ContinentPlugin.getInstance().getConfig().getString("lobby-world", "lobby");
        return Bukkit.getWorld(name);
    }

    private World getLandWorld() {
        String name = ContinentPlugin.getInstance().getConfig().getString("land-world", "world");
        return Bukkit.getWorld(name);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Village village = VillageManager.getByPlayer(player.getUniqueId());
        if (village == null || village.getnation() == null) return;
        War war = WarManager.getWar(village.getnation());
        if (war == null) return;
        if (!war.isVillageDestroyed(village.getName())) return;

        war.banPlayer(player.getUniqueId());
        player.sendMessage("전쟁 중 코어가 파괴되어 로비로 이동합니다.");
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!WarManager.isPlayerBanned(player.getUniqueId())) return;
        World lobby = getLobbyWorld();
        if (lobby != null) {
            event.setRespawnLocation(lobby.getSpawnLocation());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!WarManager.isPlayerBanned(player.getUniqueId())) return;
        World lobby = getLobbyWorld();
        if (lobby != null && !player.getWorld().equals(lobby)) {
            Bukkit.getScheduler().runTask(ContinentPlugin.getInstance(), () -> player.teleport(lobby.getSpawnLocation()));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!WarManager.isPlayerBanned(player.getUniqueId())) return;
        World land = getLandWorld();
        if (land != null && event.getTo() != null && event.getTo().getWorld().equals(land)) {
            event.setCancelled(true);
            World lobby = getLobbyWorld();
            if (lobby != null) {
                Bukkit.getScheduler().runTask(ContinentPlugin.getInstance(), () -> player.teleport(lobby.getSpawnLocation()));
            }
            player.sendMessage("전쟁 종료 전에는 메인 월드로 돌아갈 수 없습니다.");
        }
    }
}
