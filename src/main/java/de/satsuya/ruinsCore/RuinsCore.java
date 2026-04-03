package de.satsuya.ruinsCore;

import de.satsuya.ruinsCore.core.command.CommandManager;
import de.satsuya.ruinsCore.core.chat.ChatRadiusService;
import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.economy.EconomyService;
import de.satsuya.ruinsCore.core.economy.MoneyTransactionService;
import de.satsuya.ruinsCore.core.endersee.EnderseeService;
import de.satsuya.ruinsCore.core.event.EventManager;
import de.satsuya.ruinsCore.core.freeze.FreezeService;
import de.satsuya.ruinsCore.core.invsee.InvseeService;
import de.satsuya.ruinsCore.core.jobs.JobHealthService;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.size.SizeService;
import de.satsuya.ruinsCore.core.support.SupportModeService;
import de.satsuya.ruinsCore.core.vanish.VanishService;
import de.satsuya.ruinsCore.core.wache.WacheRestrainManager;
import de.satsuya.ruinsCore.core.warning.WarningService;
import de.satsuya.ruinsCore.core.module.ModuleManager;
import de.satsuya.ruinsCore.core.module.impl.CommandModule;
import de.satsuya.ruinsCore.core.module.impl.DatabaseModule;
import de.satsuya.ruinsCore.core.module.impl.EventModule;
import de.satsuya.ruinsCore.core.permission.PermissionManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import de.satsuya.ruinsCore.core.util.StaffAlertUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class RuinsCore extends JavaPlugin {

    private LoggerUtil loggerUtil;
    private PermissionManager permissionManager;
    private StaffAlertUtil staffAlertUtil;
    private DatabaseManager databaseManager;
    private CommandManager commandManager;
    private EventManager eventManager;
    private JobService jobService;
    private JobHealthService jobHealthService;
    private WacheRestrainManager wacheRestrainManager;
    private EconomyService economyService;
    private MoneyTransactionService moneyTransactionService;
    private VanishService vanishService;
    private SupportModeService supportModeService;
    private FreezeService freezeService;
    private InvseeService invseeService;
    private EnderseeService enderseeService;
    private ChatRadiusService chatRadiusService;
    private WarningService warningService;
    private SizeService sizeService;
    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.loggerUtil = new LoggerUtil(this);
        this.permissionManager = new PermissionManager(this, loggerUtil);
        this.staffAlertUtil = new StaffAlertUtil(this, permissionManager, loggerUtil);
        this.databaseManager = new DatabaseManager(this, loggerUtil);
        this.commandManager = new CommandManager(this, loggerUtil);
        this.eventManager = new EventManager(this, loggerUtil);
        this.jobService = new JobService(databaseManager, loggerUtil);
        this.jobHealthService = new JobHealthService(jobService);
        this.wacheRestrainManager = new WacheRestrainManager();
        this.economyService = new EconomyService(databaseManager, loggerUtil, getServer());
        this.moneyTransactionService = new MoneyTransactionService(databaseManager, loggerUtil, economyService);
        this.vanishService = new VanishService(this);
        this.supportModeService = new SupportModeService();
        this.freezeService = new FreezeService();
        this.invseeService = new InvseeService();
        this.enderseeService = new EnderseeService();
        this.chatRadiusService = new ChatRadiusService(this);
        this.warningService = new WarningService(databaseManager, loggerUtil);
        this.sizeService = new SizeService(databaseManager, loggerUtil);
        this.moduleManager = new ModuleManager(loggerUtil);

        String commandPackage = getConfig().getString("loader.command-package", "de.satsuya.ruinsCore.commands");
        String listenerPackage = getConfig().getString("loader.listener-package", "de.satsuya.ruinsCore.listeners");

        moduleManager.registerModule(new DatabaseModule(databaseManager));
        moduleManager.registerModule(new CommandModule(commandManager, commandPackage));
        moduleManager.registerModule(new EventModule(eventManager, listenerPackage));

        moduleManager.enableAll();
        jobHealthService.syncAllOnline();
        loggerUtil.info("RuinsCore wurde erfolgreich gestartet.");
    }

    @Override
    public void onDisable() {
        // Cleanup: Entferne alle Support Modes
        if (supportModeService != null) {
            supportModeService.disableAllSupportModes();
        }

        // Cleanup: Taue alle gefrorenen Spieler auf
        if (freezeService != null) {
            freezeService.unfreezeAll();
        }

        // Cleanup: Schließe alle Invsee-Sessions
        if (invseeService != null) {
            invseeService.closeAllSessions();
        }

        // Cleanup: Schließe alle Endersee-Sessions
        if (enderseeService != null) {
            enderseeService.closeAllSessions();
        }

        if (moduleManager != null) {
            moduleManager.disableAll();
        }

        if (loggerUtil != null) {
            loggerUtil.info("RuinsCore wurde sauber beendet.");
        }
    }

    public LoggerUtil getLoggerUtil() {
        return loggerUtil;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public StaffAlertUtil getStaffAlertUtil() {
        return staffAlertUtil;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public JobService getJobService() {
        return jobService;
    }

    public JobHealthService getJobHealthService() {
        return jobHealthService;
    }

    public WacheRestrainManager getWacheRestrainManager() {
        return wacheRestrainManager;
    }

    public EconomyService getEconomyService() {
        return economyService;
    }

    public MoneyTransactionService getMoneyTransactionService() {
        return moneyTransactionService;
    }

    public VanishService getVanishService() {
        return vanishService;
    }

    public SupportModeService getSupportModeService() {
        return supportModeService;
    }

    public FreezeService getFreezeService() {
        return freezeService;
    }

    public InvseeService getInvseeService() {
        return invseeService;
    }

    public EnderseeService getEnderseeService() {
        return enderseeService;
    }

    public ChatRadiusService getChatRadiusService() {
        return chatRadiusService;
    }

    public WarningService getWarningService() {
        return warningService;
    }

    public SizeService getSizeService() {
        return sizeService;
    }
}
