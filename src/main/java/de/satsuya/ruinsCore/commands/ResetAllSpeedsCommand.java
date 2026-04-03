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
 * ResetAllSpeeds Command - Setze alle Geschwindigkeiten zurück
 */
public final class ResetAllSpeedsCommand implements CoreCommand {

    @Override
    public String getName() {
        return "resetallspeeds";
    }

    @Override
    public String getDescription() {
        return "Setze alle Spieler-Geschwindigkeiten auf Standard zurück.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_RESET_SPEEDS;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int resetCount = 0;

        if (args.length == 0) {
            // Alle Spieler zurücksetzen
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setWalkSpeed(0.2f);
                player.setFlySpeed(0.1f);
                resetCount++;
            }

            sender.sendMessage("§a✓ §b" + resetCount + " §aSpieler-Geschwindigkeiten zurückgesetzt.");
            
            // Sende Nachricht an alle Online-Spieler mit Admin-Namen
            String message = "§6" + sender.getName() + " §ehat alle Geschwindigkeiten zurückgesetzt.";
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(message);
            }

            return true;
        }

        // Bestimmten Spieler zurücksetzen
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        target.setWalkSpeed(0.2f);
        target.setFlySpeed(0.1f);

        sender.sendMessage("§a✓ Geschwindigkeiten von §b" + target.getName() + " §azurückgesetzt.");
        target.sendMessage("§b" + sender.getName() + " §ehat deine Geschwindigkeiten zurückgesetzt.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("[Alle]");
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }

        return completions;
    }
}

