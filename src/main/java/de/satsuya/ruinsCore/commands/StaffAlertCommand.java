package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionManager;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import de.satsuya.ruinsCore.core.util.StaffAlertUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class StaffAlertCommand implements CoreCommand {

    private final PermissionManager permissionManager;
    private final StaffAlertUtil staffAlertUtil;

    public StaffAlertCommand(RuinsCore plugin) {
        this.permissionManager = plugin.getPermissionManager();
        this.staffAlertUtil = plugin.getStaffAlertUtil();
    }

    @Override
    public String getName() {
        return "staffalert";
    }

    @Override
    public String getDescription() {
        return "Sendet eine Nachricht an alle Staff-Mitglieder.";
    }

    @Override
    public String getUsage() {
        return "/staffalert <nachricht>";
    }

    @Override
    public List<String> getAliases() {
        return List.of("sa");
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_STAFF_ALERT;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!permissionManager.require(sender, PermissionNode.STAFF_ALERTS_SEND)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Nutze: " + getUsage());
            return true;
        }

        String message = String.join(" ", args);
        staffAlertUtil.alert(sender, message);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("Serverneustart", "Ticket", "Cheaterverdacht");
        }

        return List.of();
    }
}


