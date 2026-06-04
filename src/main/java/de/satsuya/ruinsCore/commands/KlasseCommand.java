package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.core.classes.RuinClassService;
import de.satsuya.ruinsCore.core.classes.RuinClassType;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.size.SizeService;
import de.satsuya.ruinsCore.core.jobs.JobHealthService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public final class KlasseCommand implements CoreCommand {

    private final RuinClassService classService;
    private final SizeService sizeService;
    private final JobHealthService healthService;
    private final JavaPlugin plugin;

    public KlasseCommand(RuinClassService classService, SizeService sizeService, JobHealthService healthService, JavaPlugin plugin) {
        this.classService = classService;
        this.sizeService = sizeService;
        this.healthService = healthService;
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "klasse";
    }

    @Override
    public String getDescription() {
        return "Verwaltet die Klassen von Spielern.";
    }

    @Override
    public String getUsage() {
        return "/klasse <set|remove> <spieler> [klasse]";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_KLASSE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cVerwendung: " + getUsage());
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        if (action.equals("remove")) {
            classService.removeClass(target.getUniqueId());
            sizeService.setPlayerSize(target.getUniqueId(), sizeService.getDefaultSize());
            sizeService.applyPlayerSize(target);
            target.setWalkSpeed(0.2f);
            target.removePotionEffect(PotionEffectType.INVISIBILITY);
            if (target.getGameMode() != org.bukkit.GameMode.CREATIVE && target.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                target.setFlying(false);
                target.setAllowFlight(false);
            }

            Bukkit.getScheduler().runTask(plugin, () -> healthService.sync(target));

            sender.sendMessage("§aDie Klasse von " + target.getName() + " wurde entfernt.");
            target.sendMessage("§cDeine Klasse wurde entfernt.");
            return true;
        }

        if (action.equals("set")) {
            if (args.length < 3) {
                sender.sendMessage("§cBitte gib eine Klasse an.");
                return true;
            }

            RuinClassType classType;
            try {
                classType = RuinClassType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cUngültige Klasse.");
                return true;
            }

            classService.setClass(target.getUniqueId(), classType);

            float newSize = sizeService.getDefaultSize();
            if (classType == RuinClassType.FEE) {
                newSize = 0.6f;
            } else if (classType == RuinClassType.ZWERG) {
                newSize = 0.8f;
                target.setWalkSpeed(0.22f);
            } else if (classType == RuinClassType.ORK) {
                newSize = 1.1f;
            }

            if (classType != RuinClassType.ZWERG) {
                target.setWalkSpeed(0.2f);
            }

            if (classType != RuinClassType.WALDGEIST) {
                target.removePotionEffect(PotionEffectType.INVISIBILITY);
            }

            if (target.getGameMode() != org.bukkit.GameMode.CREATIVE && target.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                if (classType == RuinClassType.FEE) {
                    target.setAllowFlight(true);
                } else {
                    target.setFlying(false);
                    target.setAllowFlight(false);
                }
            }

            sizeService.setPlayerSize(target.getUniqueId(), newSize);
            sizeService.applyPlayerSize(target);

            Bukkit.getScheduler().runTask(plugin, () -> healthService.sync(target));

            sender.sendMessage("§aDie Klasse von " + target.getName() + " wurde auf " + classType.getDisplayName() + " gesetzt.");
            target.sendMessage("§aDeine Klasse ist nun " + classType.getDisplayName() + ".");
            return true;
        }

        sender.sendMessage("§cVerwendung: " + getUsage());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String[] subCommands = {"set", "remove"};
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            for (RuinClassType type : RuinClassType.values()) {
                if (type.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(type.name());
                }
            }
        }
        return completions;
    }
}
