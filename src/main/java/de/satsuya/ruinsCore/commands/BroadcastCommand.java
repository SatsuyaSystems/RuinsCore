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
 * Broadcast Command - Sende Nachrichten an alle Spieler
 */
public final class BroadcastCommand implements CoreCommand {

    @Override
    public String getName() {
        return "bc";
    }

    @Override
    public String getDescription() {
        return "Sende Broadcast Nachrichten an alle Spieler.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_BROADCAST;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/bc <chat|display> <Nachricht...>");
            return true;
        }

        String mode = args[0].toLowerCase();
        
        // Sammle die Nachricht
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]);
            if (i < args.length - 1) {
                messageBuilder.append(" ");
            }
        }
        String message = messageBuilder.toString();

        if (mode.equals("chat")) {
            return handleChatBroadcast(sender, message);
        } else if (mode.equals("display")) {
            return handleDisplayBroadcast(sender, message);
        } else {
            sender.sendMessage("§cUnbekannter Modus: " + mode);
            sender.sendMessage("§eVerfügbar: chat, display");
            return true;
        }
    }

    private boolean handleChatBroadcast(CommandSender sender, String message) {
        String broadcastMessage = "§6[BROADCAST] §e" + message;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(broadcastMessage);
        }

        sender.sendMessage("§a✓ Chat-Broadcast gesendet.");
        return true;
    }

    private boolean handleDisplayBroadcast(CommandSender sender, String message) {
        // Rot gefärbte Nachricht für den Titel
        String titleMessage = "§c" + message;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Sende Title an den Spieler
            player.sendTitle(titleMessage, "", 10, 30, 10);
        }

        sender.sendMessage("§a✓ Display-Broadcast gesendet.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("chat");
            completions.add("display");
        } else if (args.length == 2) {
            completions.add("<Nachricht...>");
        }

        return completions;
    }
}

