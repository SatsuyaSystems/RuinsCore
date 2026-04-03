package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.economy.EconomyService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class MoneyCommand implements CoreCommand {

    private final EconomyService economyService;
    private final RuinsCore plugin;

    public MoneyCommand(RuinsCore plugin) {
        this.economyService = plugin.getEconomyService();
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "money";
    }

    @Override
    public String getDescription() {
        return "Zeige dein Guthaben an oder verwalte Spielergelder (Admin).";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_MONEY;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        // Normale /money Anfrage (ohne Argumente)
        if (args.length == 0) {
            showBalance(player);
            return true;
        }

        // Admin-Commands
        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("set")) {
            return handleSet(sender, args);
        }

        if (subCommand.equals("add")) {
            return handleAdd(sender, args);
        }

        if (subCommand.equals("remove")) {
            return handleRemove(sender, args);
        }

        if (subCommand.equals("info")) {
            return handleInfo(sender, args);
        }

        // Normale /money Anfrage (mit Spieler-Argument als Guthaben-Check für Admin)
        if (plugin.getPermissionManager().has(sender, PermissionNode.COMMAND_MONEY_ADMIN)) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                double balance = economyService.getBalance(target.getUniqueId());
                player.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("§6Guthaben von §b" + target.getName() + "§6: §a" + economyService.formatBalance(balance));
                player.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                return true;
            }
        }

        player.sendMessage("§cUnbekannter Subcommand.");
        return true;
    }

    private void showBalance(Player player) {
        double balance = economyService.getBalance(player.getUniqueId());
        player.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§6Dein Guthaben: §a" + economyService.formatBalance(balance));
        player.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private boolean handleSet(CommandSender sender, String[] args) {
        if (!plugin.getPermissionManager().has(sender, PermissionNode.COMMAND_MONEY_ADMIN)) {
            sender.sendMessage("§cDu hast keine Berechtigung für diesen Command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§c/money set <Spieler> <Betrag>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount < 0) {
                sender.sendMessage("§cDer Betrag darf nicht negativ sein.");
                return true;
            }
        } catch (NumberFormatException exception) {
            sender.sendMessage("§cUngültiger Betrag.");
            return true;
        }

        if (economyService.setBalance(target.getUniqueId(), amount)) {
            sender.sendMessage("§a✓ Guthaben von §6" + target.getName() + " §aauf §6" + economyService.formatBalance(amount) + " §agesetzt.");
            target.sendMessage("§6" + sender.getName() + " §ehat dein Guthaben auf §6" + economyService.formatBalance(amount) + " §egesetzt.");
            return true;
        }

        sender.sendMessage("§cOperation fehlgeschlagen.");
        return true;
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (!plugin.getPermissionManager().has(sender, PermissionNode.COMMAND_MONEY_ADMIN)) {
            sender.sendMessage("§cDu hast keine Berechtigung für diesen Command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§c/money add <Spieler> <Betrag>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount <= 0) {
                sender.sendMessage("§cDer Betrag muss größer als 0 sein.");
                return true;
            }
        } catch (NumberFormatException exception) {
            sender.sendMessage("§cUngültiger Betrag.");
            return true;
        }

        if (economyService.addBalance(target.getUniqueId(), amount)) {
            double newBalance = economyService.getBalance(target.getUniqueId());
            sender.sendMessage("§a✓ §6" + economyService.formatBalance(amount) + " §azugegeben. Neues Guthaben: §6" + economyService.formatBalance(newBalance));
            target.sendMessage("§6" + sender.getName() + " §ehat dir §6" + economyService.formatBalance(amount) + " §egegeben.");
            return true;
        }

        sender.sendMessage("§cOperation fehlgeschlagen.");
        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (!plugin.getPermissionManager().has(sender, PermissionNode.COMMAND_MONEY_ADMIN)) {
            sender.sendMessage("§cDu hast keine Berechtigung für diesen Command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§c/money remove <Spieler> <Betrag>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount <= 0) {
                sender.sendMessage("§cDer Betrag muss größer als 0 sein.");
                return true;
            }
        } catch (NumberFormatException exception) {
            sender.sendMessage("§cUngültiger Betrag.");
            return true;
        }

        double currentBalance = economyService.getBalance(target.getUniqueId());
        if (currentBalance < amount) {
            sender.sendMessage("§cSpieler hat nicht genug Geld! Aktuelles Guthaben: " + economyService.formatBalance(currentBalance));
            return true;
        }

        if (economyService.removeBalance(target.getUniqueId(), amount)) {
            double newBalance = economyService.getBalance(target.getUniqueId());
            sender.sendMessage("§a✓ §6" + economyService.formatBalance(amount) + " §aentfernt. Neues Guthaben: §6" + economyService.formatBalance(newBalance));
            target.sendMessage("§6" + sender.getName() + " §ehat dir §6" + economyService.formatBalance(amount) + " §eentzogen.");
            return true;
        }

        sender.sendMessage("§cOperation fehlgeschlagen.");
        return true;
    }

    private boolean handleInfo(CommandSender sender, String[] args) {
        if (!plugin.getPermissionManager().has(sender, PermissionNode.COMMAND_MONEY_ADMIN)) {
            sender.sendMessage("§cDu hast keine Berechtigung für diesen Command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c/money info <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        double balance = economyService.getBalance(target.getUniqueId());
        sender.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§6Spieler: §b" + target.getName());
        sender.sendMessage("§6UUID: §b" + target.getUniqueId());
        sender.sendMessage("§6Guthaben: §a" + economyService.formatBalance(balance));
        sender.sendMessage("§6Status: §b" + (target.isOnline() ? "Online" : "Offline"));
        sender.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!plugin.getPermissionManager().has(sender, PermissionNode.COMMAND_MONEY_ADMIN)) {
            return completions;
        }

        if (args.length == 1) {
            completions.add("set");
            completions.add("add");
            completions.add("remove");
            completions.add("info");
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 3) {
            completions.add("<Betrag>");
        }

        return completions;
    }
}


