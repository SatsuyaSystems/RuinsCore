package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.report.ReportService;
import de.satsuya.ruinsCore.core.report.ReportGuiService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command für das Report-System
 */
public final class ReportCommand implements CoreCommand {

    private final ReportService reportService;
    private final ReportGuiService reportGuiService;

    public ReportCommand(ReportService reportService, ReportGuiService reportGuiService) {
        this.reportService = reportService;
        this.reportGuiService = reportGuiService;
    }

    @Override
    public String getName() {
        return "report";
    }

    @Override
    public String getDescription() {
        return "Report-System für Spieler";
    }

    @Override
    public String getUsage() {
        return "/report <player> <reason>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c✗ Nur Spieler können diesen Command nutzen!");
            return true;
        }

        Player player = (Player) sender;

        // /report gui - Nur für Supporter
        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
            if (!player.hasPermission("ruinscore.command.report.view")) {
                player.sendMessage("§c✗ Du hast keine Berechtigung!");
                return true;
            }
            reportGuiService.openReportOverviewGui(player);
            return true;
        }

        // /report <player> <reason>
        if (args.length < 2) {
            player.sendMessage("§c✗ Verwendung: /report <player> <reason>");
            return true;
        }

        Player reported = Bukkit.getPlayer(args[0]);
        if (reported == null) {
            player.sendMessage("§c✗ Spieler nicht gefunden!");
            return true;
        }

        if (reported.equals(player)) {
            player.sendMessage("§c✗ Du kannst dich selbst nicht melden!");
            return true;
        }

        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        reportService.createReport(
            player.getUniqueId(), player.getName(),
            reported.getUniqueId(), reported.getName(),
            reason
        );

        player.sendMessage("§a✓ Report gegen " + reported.getName() + " eingereicht!");

        // Benachrichtige alle Spieler mit View-Permission
        String notification = "§c📋 Neuer Report: §f" + reported.getName() + " §c(von §f" + player.getName() + "§c)";
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("ruinscore.command.report.view")) {
                online.sendMessage(notification);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (args[0].startsWith("g")) {
                if (player.hasPermission("ruinscore.command.report.view")) {
                    completions.add("gui");
                }
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.equals(player)) {
                    completions.add(online.getName());
                }
            }
        }

        return completions;
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.REPORT_USE;
    }
}

