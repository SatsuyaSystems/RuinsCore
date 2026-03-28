package de.satsuya.ruinsCore.core.module;

import de.satsuya.ruinsCore.core.util.LoggerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {

    private final LoggerUtil loggerUtil;
    private final List<PluginModule> modules = new ArrayList<>();

    public ModuleManager(LoggerUtil loggerUtil) {
        this.loggerUtil = loggerUtil;
    }

    public void registerModule(PluginModule module) {
        modules.add(module);
    }

    public void enableAll() {
        for (PluginModule module : modules) {
            try {
                module.onEnable();
                loggerUtil.info("Modul aktiviert: " + module.getName());
            } catch (Exception exception) {
                loggerUtil.severe("Modul konnte nicht aktiviert werden: " + module.getName(), exception);
            }
        }
    }

    public void disableAll() {
        List<PluginModule> reverseOrder = new ArrayList<>(modules);
        Collections.reverse(reverseOrder);

        for (PluginModule module : reverseOrder) {
            try {
                module.onDisable();
                loggerUtil.info("Modul deaktiviert: " + module.getName());
            } catch (Exception exception) {
                loggerUtil.severe("Modul konnte nicht deaktiviert werden: " + module.getName(), exception);
            }
        }
    }
}

