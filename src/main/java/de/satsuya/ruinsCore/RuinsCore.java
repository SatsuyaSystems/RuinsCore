package de.satsuya.ruinsCore;

import de.satsuya.ruinsCore.core.command.CommandManager;
import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.event.EventManager;
import de.satsuya.ruinsCore.core.jobs.JobService;
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
        this.moduleManager = new ModuleManager(loggerUtil);

        String commandPackage = getConfig().getString("loader.command-package", "de.satsuya.ruinsCore.commands");
        String listenerPackage = getConfig().getString("loader.listener-package", "de.satsuya.ruinsCore.listeners");

        moduleManager.registerModule(new DatabaseModule(databaseManager));
        moduleManager.registerModule(new CommandModule(commandManager, commandPackage));
        moduleManager.registerModule(new EventModule(eventManager, listenerPackage));

        moduleManager.enableAll();
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
}
