package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Sudo Command - Führt einen Command als anderer Spieler aus
 */
public final class SudoCommand implements CoreCommand {

    @Override
    public String getName() {
        return "sudo";
    }

    @Override
    public String getDescription() {
        return "Führe einen Command als anderer Spieler aus.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_SUDO;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/sudo <Spieler> <Command...>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            commandBuilder.append(args[i]);
            if (i < args.length - 1) {
                commandBuilder.append(" ");
            }
        }

        String commandToExecute = commandBuilder.toString();
        boolean success = Bukkit.dispatchCommand(target, commandToExecute);

        if (success) {
            sender.sendMessage("§a✓ Command ausgeführt als §b" + target.getName());
        } else {
            sender.sendMessage("§c✗ Command konnte nicht ausgeführt werden.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }

        return completions;
    }
}

