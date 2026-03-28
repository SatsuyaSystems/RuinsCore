package de.satsuya.ruinsCore.core.permission;

import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class PermissionManager {

    private final Plugin plugin;
    private final LoggerUtil loggerUtil;

    public PermissionManager(Plugin plugin, LoggerUtil loggerUtil) {
        this.plugin = plugin;
        this.loggerUtil = loggerUtil;
    }

    public boolean has(CommandSender sender, PermissionNode node) {
        return sender.hasPermission(node.getNode());
    }

    public boolean require(CommandSender sender, PermissionNode node) {
        if (has(sender, node)) {
            return true;
        }

        sender.sendMessage("§cDafür hast du keine Berechtigung.");
        return false;
    }

    public void logRegisteredPermissions() {
        for (PermissionNode value : PermissionNode.values()) {
            loggerUtil.info("Permission registriert: " + value.getNode());
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }
}

