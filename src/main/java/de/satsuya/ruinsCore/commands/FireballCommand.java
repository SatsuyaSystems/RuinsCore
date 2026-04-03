package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Fireball Command - Werfe einen Feuerball
 */
public final class FireballCommand implements CoreCommand {

    @Override
    public String getName() {
        return "fireball";
    }

    @Override
    public String getDescription() {
        return "Werfe einen Feuerball in die Richtung, in die du schaust.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_FIREBALL;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        double speed = 2.0;

        if (args.length > 0) {
            try {
                speed = Double.parseDouble(args[0]);
                speed = Math.max(0.5, Math.min(10.0, speed));
            } catch (NumberFormatException exception) {
                player.sendMessage("§cUngültige Geschwindigkeit.");
                return true;
            }
        }

        Fireball fireball = player.getWorld().spawn(player.getEyeLocation(), Fireball.class);
        Vector direction = player.getLocation().getDirection();
        fireball.setVelocity(direction.multiply(speed));
        fireball.setYield(0.0f);
        fireball.setIsIncendiary(true);

        player.sendMessage("§a✓ Feuerball mit Geschwindigkeit §b" + speed + " §aabgeworfen.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (double i = 0.5; i <= 5.0; i += 0.5) {
                completions.add(String.valueOf(i));
            }
        }

        return completions;
    }
}

