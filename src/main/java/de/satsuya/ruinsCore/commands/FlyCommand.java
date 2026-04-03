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
 * Fly Command - Aktiviere/Deaktiviere Flug-Modus
 */
public final class FlyCommand implements CoreCommand {

    @Override
    public String getName() {
        return "fly";
    }

    @Override
    public String getDescription() {
        return "Aktiviere oder deaktiviere den Flug-Modus.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_FLY;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c/fly [Spieler]");
                return true;
            }
            player = (Player) sender;
        } else {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("§cSpieler nicht gefunden.");
                return true;
            }
        }

        boolean newFlyState = !player.getAllowFlight();
        player.setAllowFlight(newFlyState);
        player.setFlying(newFlyState);

        String state = newFlyState ? "§aaktiviert" : "§cdeaktiviert";

        if (player.equals(sender)) {
            player.sendMessage("§6Flug-Modus " + state + "§6.");
        } else {
            sender.sendMessage("§a✓ Flug-Modus für §b" + player.getName() + " " + state + "§a.");
            player.sendMessage("§b" + sender.getName() + " §ehat deinen Flug-Modus " + state + "§e.");
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

