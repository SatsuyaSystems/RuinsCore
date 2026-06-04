package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.core.classes.RuinClassService;
import de.satsuya.ruinsCore.core.classes.RuinClassType;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.jobs.JobPrefixService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TaufeCommand implements CoreCommand {

    private final RuinClassService classService;
    private final JobPrefixService jobPrefixService;

    public TaufeCommand(RuinClassService classService, JobPrefixService jobPrefixService) {
        this.classService = classService;
        this.jobPrefixService = jobPrefixService;
    }

    @Override
    public String getName() {
        return "taufe";
    }

    @Override
    public String getDescription() {
        return "Tauft einen Spieler (Nur für Priester).";
    }

    @Override
    public String getUsage() {
        return "/taufe <spieler> | /taufe enttaufe <spieler>";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_TAUFE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl ausführen.");
            return true;
        }

        Player player = (Player) sender;
        Optional<RuinClassType> playerClass = classService.getClass(player.getUniqueId());

        if (playerClass.isEmpty() || playerClass.get() != RuinClassType.PRIESTER) {
            player.sendMessage("§cNur Priester können Spieler (ent-)taufen.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cVerwendung: " + getUsage());
            return true;
        }

        boolean unbaptize = args[0].equalsIgnoreCase("enttaufe");
        String targetName;

        if (unbaptize) {
            if (args.length < 2) {
                player.sendMessage("§cVerwendung: /taufe enttaufe <spieler>");
                return true;
            }
            targetName = args[1];
        } else {
            targetName = args[0];
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage("§cDer Spieler " + targetName + " ist nicht online.");
            return true;
        }

        if (unbaptize) {
            if (!classService.isBaptized(target.getUniqueId())) {
                player.sendMessage("§c" + target.getName() + " ist nicht getauft.");
                return true;
            }

            classService.setBaptized(target.getUniqueId(), false);
            jobPrefixService.updatePlayerPrefix(target);
            Bukkit.broadcastMessage("§eDer Priester " + player.getName() + " hat " + target.getName() + " enttauft.");
            return true;
        }

        if (classService.isBaptized(target.getUniqueId())) {
            player.sendMessage("§c" + target.getName() + " ist bereits getauft.");
            return true;
        }

        classService.setBaptized(target.getUniqueId(), true);
        jobPrefixService.updatePlayerPrefix(target);
        Bukkit.broadcastMessage("§eDer Priester " + player.getName() + " hat " + target.getName() + " getauft! ✟");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("enttaufe".startsWith(args[0].toLowerCase())) {
                completions.add("enttaufe");
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("enttaufe")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        }
        return completions;
    }
}
