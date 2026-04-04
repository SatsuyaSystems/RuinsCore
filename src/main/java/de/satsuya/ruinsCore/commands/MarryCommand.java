package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.marry.MarryService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command zum Verheiraten und Scheiden von Spielern
 */
public final class MarryCommand implements CoreCommand {

    private final MarryService marryService;
    private final RuinsCore plugin;

    public MarryCommand(RuinsCore plugin) {
        this.plugin = plugin;
        this.marryService = plugin.getMarryService();
    }

    @Override
    public String getName() {
        return "marry";
    }

    @Override
    public String getDescription() {
        return "Verheirate oder scheide Spieler";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.MARRY;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "marry":
                handleMarry(player, args);
                break;
            case "divorce":
                handleDivorce(player, args);
                break;
            case "info":
                handleInfo(player, args);
                break;
            default:
                sendUsage(player);
        }

        return true;
    }

    private void handleMarry(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage("§c/marry marry <Spieler1> <Spieler2>");
            return;
        }

        Player player1 = Bukkit.getPlayer(args[1]);
        Player player2 = Bukkit.getPlayer(args[2]);

        if (player1 == null) {
            player.sendMessage("§c✗ Spieler '" + args[1] + "' nicht gefunden!");
            return;
        }

        if (player2 == null) {
            player.sendMessage("§c✗ Spieler '" + args[2] + "' nicht gefunden!");
            return;
        }

        if (marryService.isMarried(player1.getUniqueId())) {
            player.sendMessage("§c✗ " + player1.getName() + " ist bereits verheiratet!");
            return;
        }

        if (marryService.isMarried(player2.getUniqueId())) {
            player.sendMessage("§c✗ " + player2.getName() + " ist bereits verheiratet!");
            return;
        }

        marryService.marryPlayers(player1.getUniqueId(), player2.getUniqueId());
        marryService.savePartnerName(player1.getUniqueId(), player1.getName());
        marryService.savePartnerName(player2.getUniqueId(), player2.getName());

        String message = "§d💕 " + player1.getName() + " §dund §d" + player2.getName() + " §dsind jetzt verheiratet!";
        Bukkit.broadcast(net.kyori.adventure.text.Component.text(message));

        player1.sendMessage("§d💕 Du bist jetzt mit §d" + player2.getName() + " verheiratet!");
        player2.sendMessage("§d💕 Du bist jetzt mit §d" + player1.getName() + " verheiratet!");

        player.sendMessage("§a✓ Hochzeit durchgeführt!");
    }

    private void handleDivorce(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage("§c/marry divorce <Spieler>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage("§c✗ Spieler '" + args[1] + "' nicht gefunden!");
            return;
        }

        if (!marryService.isMarried(target.getUniqueId())) {
            player.sendMessage("§c✗ " + target.getName() + " ist nicht verheiratet!");
            return;
        }

        marryService.divorcePlayer(target.getUniqueId());

        String message = "§c💔 " + target.getName() + " ist jetzt Single!";
        Bukkit.broadcast(net.kyori.adventure.text.Component.text(message));

        target.sendMessage("§c💔 Du bist jetzt geschieden!");
        player.sendMessage("§a✓ Scheidung durchgeführt!");
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage("§c/marry info <Spieler>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage("§c✗ Spieler '" + args[1] + "' nicht gefunden!");
            return;
        }

        if (!marryService.isMarried(target.getUniqueId())) {
            player.sendMessage("§c✗ " + target.getName() + " ist nicht verheiratet!");
            return;
        }

        String partnerName = marryService.getPartnerName(target.getUniqueId());
        player.sendMessage("§d" + target.getName() + " ist verheiratet mit §d" + partnerName);
    }

    private void sendUsage(Player player) {
        player.sendMessage("§d/marry marry <Spieler1> <Spieler2> - Verheirate zwei Spieler");
        player.sendMessage("§d/marry divorce <Spieler> - Scheiden");
        player.sendMessage("§d/marry info <Spieler> - Zeige Ehe-Information");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("marry");
            completions.add("divorce");
            completions.add("info");
        } else if (args.length >= 2) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        }

        return completions;
    }
}

