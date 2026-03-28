package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class PingCommand implements CoreCommand {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Prueft, ob das Plugin antwortet.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_PING;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Pong from RuinsCore.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("silent", "verbose");
        }

        return List.of();
    }
}

