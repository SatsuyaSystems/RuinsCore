package de.satsuya.ruinsCore.core.module.impl;

import de.satsuya.ruinsCore.core.command.CommandManager;
import de.satsuya.ruinsCore.core.module.PluginModule;

public final class CommandModule implements PluginModule {

    private final CommandManager commandManager;
    private final String commandPackage;

    public CommandModule(CommandManager commandManager, String commandPackage) {
        this.commandManager = commandManager;
        this.commandPackage = commandPackage;
    }

    @Override
    public String getName() {
        return "CommandModule";
    }

    @Override
    public void onEnable() {
        commandManager.loadFromPackage(commandPackage);
    }

    @Override
    public void onDisable() {
        commandManager.unregisterAll();
    }
}

