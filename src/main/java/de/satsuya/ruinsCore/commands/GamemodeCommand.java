package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Gamemode Command - Ändere den Gamemode
 */
public final class GamemodeCommand implements CoreCommand {

    @Override
    public String getName() {
        return "gm";
    }

    @Override
    public String getDescription() {
        return "Ändere deinen Gamemode.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_GAMEMODE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        GameMode gamemode = null;

        // Bestimme den Ziel-Spieler
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
                return true;
            }
            player = (Player) sender;
        } else if (args.length >= 1) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("§cSpieler nicht gefunden.");
                return true;
            }
        }

        // Bestimme den Gamemode
        if (args.length == 0 || args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c/gm <Spieler> <Gamemode> oder /gm <Gamemode>");
                return true;
            }

            String mode = args.length == 0 ? null : args[0];
            gamemode = parseGamemode(mode);
            if (gamemode == null && args.length > 0) {
                sender.sendMessage("§cUnbekannter Gamemode: " + args[0]);
                return true;
            }

            if (gamemode == null) {
                // Cycle durch Gamemodes
                gamemode = cycleGamemode(player.getGameMode());
            }
        } else if (args.length >= 2) {
            gamemode = parseGamemode(args[1]);
            if (gamemode == null) {
                sender.sendMessage("§cUnbekannter Gamemode: " + args[1]);
                return true;
            }
        }

        player.setGameMode(gamemode);
        String modeName = gamemode.name().charAt(0) + gamemode.name().substring(1).toLowerCase();

        if (player.equals(sender)) {
            player.sendMessage("§a✓ Gamemode auf §b" + modeName + " §agesetzt.");
        } else {
            sender.sendMessage("§a✓ Gamemode von §b" + player.getName() + " §aauf §b" + modeName + " §agesetzt.");
            player.sendMessage("§b" + sender.getName() + " §ehat deinen Gamemode auf §b" + modeName + " §egesetzt.");
        }

        return true;
    }

    private GameMode parseGamemode(String input) {
        if (input == null) return null;

        return switch (input.toLowerCase()) {
            case "survival", "s", "0" -> GameMode.SURVIVAL;
            case "creative", "c", "1" -> GameMode.CREATIVE;
            case "adventure", "a", "2" -> GameMode.ADVENTURE;
            case "spectator", "sp", "3" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    private GameMode cycleGamemode(GameMode current) {
        return switch (current) {
            case SURVIVAL -> GameMode.CREATIVE;
            case CREATIVE -> GameMode.ADVENTURE;
            case ADVENTURE -> GameMode.SPECTATOR;
            case SPECTATOR -> GameMode.SURVIVAL;
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Könnte Spielername oder Gamemode sein
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            completions.add("survival");
            completions.add("creative");
            completions.add("adventure");
            completions.add("spectator");
        } else if (args.length == 2) {
            completions.add("survival");
            completions.add("creative");
            completions.add("adventure");
            completions.add("spectator");
        }

        return completions;
    }
}

