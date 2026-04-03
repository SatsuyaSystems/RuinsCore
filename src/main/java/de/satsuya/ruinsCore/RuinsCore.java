package de.satsuya.ruinsCore;

import de.satsuya.ruinsCore.core.command.CommandManager;
import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.economy.EconomyService;
import de.satsuya.ruinsCore.core.economy.MoneyTransactionService;
import de.satsuya.ruinsCore.core.event.EventManager;
import de.satsuya.ruinsCore.core.jobs.JobHealthService;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.vanish.VanishService;
import de.satsuya.ruinsCore.core.wache.WacheRestrainManager;
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
}
