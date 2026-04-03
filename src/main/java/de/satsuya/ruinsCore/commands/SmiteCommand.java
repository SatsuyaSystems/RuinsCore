package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Smite Command - Schlag einen Spieler mit Blitz
 */
public final class SmiteCommand implements CoreCommand {

    @Override
    public String getName() {
        return "smite";
    }

    @Override
    public String getDescription() {
        return "Schlage einen Spieler mit Blitz.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_SMITE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c/smite <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        LightningStrike lightning = target.getWorld().strikeLightning(target.getLocation());

        sender.sendMessage("§a✓ §b" + target.getName() + " §awurde vom Blitz getroffen!");
        target.sendMessage("§c✗ Du wurdest vom Blitz getroffen!");

        if (!(sender instanceof Player)) {
            return true;
        }

        Player senderPlayer = (Player) sender;
        if (!target.equals(senderPlayer)) {
            senderPlayer.sendMessage("§eSchlagerfolg: " + target.getName());
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

