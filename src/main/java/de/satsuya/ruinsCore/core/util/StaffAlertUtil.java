package de.satsuya.ruinsCore.core.util;

import de.satsuya.ruinsCore.core.permission.PermissionManager;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class StaffAlertUtil {

    private final Plugin plugin;
    private final PermissionManager permissionManager;
    private final LoggerUtil loggerUtil;

    public StaffAlertUtil(Plugin plugin, PermissionManager permissionManager, LoggerUtil loggerUtil) {
        this.plugin = plugin;
        this.permissionManager = permissionManager;
        this.loggerUtil = loggerUtil;
    }

    public void alert(String message) {
        String formatted = "[StaffAlert] " + message;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (permissionManager.has(player, PermissionNode.STAFF_ALERTS_RECEIVE)) {
                player.sendMessage(formatted);
            }
        }

        plugin.getServer().getConsoleSender().sendMessage(formatted);
        loggerUtil.info("Staff-Alert gesendet: " + message);
    }

    public void alert(CommandSender sender, String message) {
        String source = sender.getName();
        alert(source + ": " + message);
    }
}

