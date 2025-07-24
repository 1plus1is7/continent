package me.continent.command;

import me.continent.job.Job;
import me.continent.job.JobManager;
import me.continent.job.JobMenuService;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

/** Command for managing player jobs. */
public class JobCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        PlayerData data = PlayerDataManager.get(player.getUniqueId());

        if (args.length == 0) {
            if (data.getJobId() == null) {
                player.sendMessage("§e현재 설정된 직업이 없습니다.");
            } else {
                Job job = JobManager.get(data.getJobId());
                if (job != null) {
                    player.sendMessage("§e현재 직업: " + job.getName());
                } else {
                    player.sendMessage("§e현재 직업: " + data.getJobId());
                }
            }
            JobMenuService.openSelect(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            String list = JobManager.getAll().stream().map(Job::getId).collect(Collectors.joining(", "));
            player.sendMessage("§e직업 목록: " + list);
            return true;
        }

        if (args[0].equalsIgnoreCase("select") && args.length >= 2) {
            Job job = JobManager.get(args[1]);
            if (job == null) {
                player.sendMessage("§c존재하지 않는 직업입니다.");
                return true;
            }
            data.setJobId(job.getId());
            PlayerDataManager.save(player.getUniqueId());
            player.sendMessage("§a직업이 설정되었습니다: " + job.getName());
            return true;
        }

        player.sendMessage("§c사용법: /job [list|select <id>]");
        return true;
    }
}
