package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Msg Command - Private Nachrichten mit Spy-Feature für Admins
 */
public final class MsgCommand implements CoreCommand {

    private final RuinsCore plugin;
    private final Map<UUID, UUID> lastMessagedPlayer = new HashMap<>();

    public MsgCommand(RuinsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "msg";
    }

    @Override
    public String getDescription() {
        return "Sende eine private Nachricht an einen Spieler.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_MSG;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§c/msg <Spieler> <Nachricht...>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cDu kannst dir selbst keine Nachricht senden.");
            return true;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]);
            if (i < args.length - 1) {
                messageBuilder.append(" ");
            }
        }

        String message = messageBuilder.toString();

        // Speichere letzten Nachrichtenpartner für Reply
        lastMessagedPlayer.put(player.getUniqueId(), target.getUniqueId());
        lastMessagedPlayer.put(target.getUniqueId(), player.getUniqueId());

        // Sende Nachricht an Ziel
        target.sendMessage("§6[" + player.getName() + " → dir§6] §b" + message);

        // Sende Bestätigung an Sender
        player.sendMessage("§6[du → " + target.getName() + "§6] §b" + message);

        // Spy-Feature: Zeige Admins mit Permission
        if (plugin.getPermissionManager().has(player, PermissionNode.COMMAND_MSG_SPY) || 
            plugin.getPermissionManager().has(target, PermissionNode.COMMAND_MSG_SPY)) {
            // Der aktuelle Sender/Empfänger sollen nicht das Spy-Message sehen
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.equals(player) && !onlinePlayer.equals(target)) {
                    if (plugin.getPermissionManager().has(onlinePlayer, PermissionNode.COMMAND_MSG_SPY)) {
                        onlinePlayer.sendMessage("§7[SPY] §6" + player.getName() + " → " + target.getName() + "§7: §b" + message);
                    }
                }
            }
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

