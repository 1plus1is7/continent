package me.continent;

import me.continent.chat.VillageChatListener;
import me.continent.chat.GlobalChatListener;
import me.continent.command.GoldCommand;
import me.continent.command.VillageCommand;
import me.continent.war.WarCommand;
import me.continent.command.GuideCommand;
import me.continent.command.SpecialtyCommand;
import me.continent.command.AdminCommand;
import me.continent.economy.CentralBankDataManager;
import me.continent.listener.TerritoryListener;
import me.continent.listener.MaintenanceJoinListener;
import me.continent.protection.TerritoryProtectionListener;
import me.continent.protection.CoreProtectionListener;
import me.continent.war.CoreAttackListener;
import me.continent.war.WarDeathListener;
import me.continent.protection.ProtectionStateListener;
import me.continent.village.service.ChestListener;
import me.continent.village.service.MaintenanceService;
import me.continent.village.service.VillageMenuListener;
import me.continent.village.service.VillageTreasuryListener;
import org.bukkit.plugin.java.JavaPlugin;
import me.continent.player.PlayerDataManager;
import me.continent.command.MarketCommand;
import me.continent.market.MarketManager;
import me.continent.market.MarketListener;
import me.continent.storage.VillageStorage;
import me.continent.scoreboard.ScoreboardService;
import me.continent.crop.CropGrowthManager;
import me.continent.crop.CropListener;
import me.continent.research.ResearchListener;
import me.continent.research.ResearchManager;
import me.continent.specialty.SpecialtyManager;
import me.continent.specialty.SpecialtyListener;
import me.continent.village.service.VillageSpecialtyListener;

public class ContinentPlugin extends JavaPlugin {
    private static ContinentPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        MaintenanceService.init(getConfig());
        MaintenanceService.schedule();

        // 명령어 등록 (plugin.yml 누락 시 NPE 방지)
        registerCommand("gold", new GoldCommand());
        registerCommand("village", new VillageCommand());
        registerCommand("war", new WarCommand());
        registerCommand("guide", new GuideCommand());
        registerCommand("specialty", new SpecialtyCommand());
        registerCommand("market", new MarketCommand());
        registerCommand("admin", new AdminCommand());

        // 중앙은행 데이터 로딩
        CentralBankDataManager.load();
        VillageStorage.loadAll(); // 저장된 모든 마을 불러오기
        PlayerDataManager.loadAll();

        ResearchManager.loadNodes(this);
        SpecialtyManager.load(this);

        CropGrowthManager.init(this);

        ScoreboardService.schedule();

        MarketManager.load(this);
        getServer().getPluginManager().registerEvents(new TerritoryListener(), this);
        getServer().getPluginManager().registerEvents(new VillageChatListener(), this);
        getServer().getPluginManager().registerEvents(new GlobalChatListener(), this);
        getServer().getPluginManager().registerEvents(new MaintenanceJoinListener(), this);
        getServer().getPluginManager().registerEvents(new TerritoryProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new CoreProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new CoreAttackListener(), this);
        getServer().getPluginManager().registerEvents(new ChestListener(), this);
        getServer().getPluginManager().registerEvents(new ProtectionStateListener(), this);
        getServer().getPluginManager().registerEvents(new WarDeathListener(), this);
        getServer().getPluginManager().registerEvents(new CropListener(), this);
        getServer().getPluginManager().registerEvents(new ResearchListener(), this);
        getServer().getPluginManager().registerEvents(new me.continent.specialty.SpecialtyListener(), this);
        getServer().getPluginManager().registerEvents(new VillageSpecialtyListener(), this);
        getServer().getPluginManager().registerEvents(new VillageMenuListener(), this);
        getServer().getPluginManager().registerEvents(new VillageTreasuryListener(), this);


        getServer().getPluginManager().registerEvents(new MarketListener(), this);

        getLogger().info("Continent 플러그인 활성화됨");
    }

    @Override
    public void onDisable() {
        // 중앙은행 데이터 저장
        CentralBankDataManager.save();
        MarketManager.save();
        PlayerDataManager.saveAll();

        CropGrowthManager.shutdown();

        getLogger().info("Continent 플러그인 비활성화됨");
    }

    public static ContinentPlugin getInstance() {
        return instance;
    }

    private void registerCommand(String name, org.bukkit.command.CommandExecutor exe) {
        var cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(exe);
        } else {
            getLogger().severe("Command '" + name + "' not defined in plugin.yml");
        }
    }
}
