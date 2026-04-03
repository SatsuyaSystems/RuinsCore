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
 * Flyspeed Command - Ändere nur die Flug-Geschwindigkeit
 */
public final class FlyspeedCommand implements CoreCommand {

    @Override
    public String getName() {
        return "flyspeed";
    }

    @Override
    public String getDescription() {
        return "Ändere die Flug-Geschwindigkeit.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_FLYSPEED;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        double speed = 1.0;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c/flyspeed <Geschwindigkeit> [Spieler]");
                return true;
            }
            player = (Player) sender;
        } else if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c/flyspeed <Geschwindigkeit> [Spieler]");
                return true;
            }
            player = (Player) sender;

            try {
                speed = Double.parseDouble(args[0]);
            } catch (NumberFormatException exception) {
                sender.sendMessage("§cUngültige Geschwindigkeit.");
                return true;
            }
        } else {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("§cSpieler nicht gefunden.");
                return true;
            }

            try {
                speed = Double.parseDouble(args[1]);
            } catch (NumberFormatException exception) {
                sender.sendMessage("§cUngültige Geschwindigkeit.");
                return true;
            }
        }

        // Validiere Geschwindigkeit (0.1 - 10.0)
        if (speed < 0.1 || speed > 10.0) {
            sender.sendMessage("§cGeschwindigkeit muss zwischen 0.1 und 10.0 liegen.");
            return true;
        }

        // Normalisiere auf 0.0-1.0 für setFlySpeed
        float normalizedSpeed = (float) ((speed - 0.1) / 9.9);
        normalizedSpeed = Math.max(0.0f, Math.min(1.0f, normalizedSpeed));

        player.setFlySpeed(normalizedSpeed);

        if (player.equals(sender)) {
            player.sendMessage("§a✓ Flug-Geschwindigkeit auf §b" + speed + " §agesetzt.");
        } else {
            sender.sendMessage("§a✓ Flug-Geschwindigkeit von §b" + player.getName() + " §aauf §b" + speed + " §agesetzt.");
            player.sendMessage("§b" + sender.getName() + " §ehat deine Flug-Geschwindigkeit auf §b" + speed + " §egesetzt.");
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
            for (double i = 0.5; i <= 5.0; i += 0.5) {
                completions.add(String.valueOf(i));
            }
        } else if (args.length == 2) {
            for (double i = 0.5; i <= 5.0; i += 0.5) {
                completions.add(String.valueOf(i));
            }
        }

        return completions;
    }
}

