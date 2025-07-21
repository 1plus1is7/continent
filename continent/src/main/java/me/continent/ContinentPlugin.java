package me.continent;

import me.continent.chat.VillageChatListener;
import me.continent.chat.GlobalChatListener;
import me.continent.chat.nationChatListener;
import me.continent.command.GoldCommand;
import me.continent.command.VillageCommand;
import me.continent.command.nationCommand;
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
import me.continent.nation.service.nationSpecialtyListener;

public class ContinentPlugin extends JavaPlugin {
    private static ContinentPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        MaintenanceService.init(getConfig());
        MaintenanceService.schedule();

        // 명령어 등록
        getCommand("gold").setExecutor(new GoldCommand());
        getCommand("village").setExecutor(new VillageCommand());
        getCommand("nation").setExecutor(new nationCommand());
        getCommand("war").setExecutor(new WarCommand());
        getCommand("guide").setExecutor(new GuideCommand());
        getCommand("specialty").setExecutor(new SpecialtyCommand());
        getCommand("market").setExecutor(new MarketCommand());
        getCommand("admin").setExecutor(new AdminCommand());

        // 중앙은행 데이터 로딩
        CentralBankDataManager.load();
        VillageStorage.loadAll(); // 저장된 모든 마을 불러오기
        me.continent.nation.nationStorage.loadAll();
        PlayerDataManager.loadAll();

        ResearchManager.loadNodes(this);
        me.continent.specialty.SpecialtyManager.load(this);

        CropGrowthManager.init(this);

        ScoreboardService.schedule();

        MarketManager.load(this);
        getServer().getPluginManager().registerEvents(new TerritoryListener(), this);
        getServer().getPluginManager().registerEvents(new VillageChatListener(), this);
        getServer().getPluginManager().registerEvents(new GlobalChatListener(), this);
        getServer().getPluginManager().registerEvents(new nationChatListener(), this);
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
        getServer().getPluginManager().registerEvents(new nationSpecialtyListener(), this);
        getServer().getPluginManager().registerEvents(new me.continent.nation.service.nationMenuListener(), this);
        getServer().getPluginManager().registerEvents(new me.continent.nation.service.nationChestListener(), this);
        getServer().getPluginManager().registerEvents(new me.continent.nation.service.nationTreasuryListener(), this);
        getServer().getPluginManager().registerEvents(new me.continent.nation.service.nationManageListener(), this);
        getServer().getPluginManager().registerEvents(new me.continent.nation.service.nationVillageManageListener(), this);


        getServer().getPluginManager().registerEvents(new MarketListener(), this);

        getLogger().info("Continent 플러그인 활성화됨");
    }

    @Override
    public void onDisable() {
        // 중앙은행 데이터 저장
        CentralBankDataManager.save();
        me.continent.nation.nationStorage.saveAll();
        MarketManager.save();
        PlayerDataManager.saveAll();

        CropGrowthManager.shutdown();

        getLogger().info("Continent 플러그인 비활성화됨");
    }

    public static ContinentPlugin getInstance() {
        return instance;
    }
}
