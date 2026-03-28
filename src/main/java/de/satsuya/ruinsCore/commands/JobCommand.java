package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import de.satsuya.ruinsCore.core.jobs.gui.JobGuiService;
import de.satsuya.ruinsCore.core.permission.PermissionManager;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class JobCommand implements CoreCommand {

    private final PermissionManager permissionManager;
    private final JobService jobService;
    private final JobGuiService jobGuiService;

    public JobCommand(RuinsCore plugin) {
        this.permissionManager = plugin.getPermissionManager();
        this.jobService = plugin.getJobService();
        this.jobGuiService = new JobGuiService(jobService);
    }

    @Override
    public String getName() {
        return "job";
    }

    @Override
    public String getDescription() {
        return "Verwaltet Jobs und Mitglieder.";
    }

    @Override
    public String getUsage() {
        return "/job <assign|remove|list|leader|gui> ...";
    }

    @Override
    public PermissionNode getPermission() {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Nutze: " + getUsage());
            return true;
        }

        String subCommand = args[0].toLowerCase(Locale.ROOT);
        switch (subCommand) {
            case "assign" -> {
                if (!permissionManager.require(sender, PermissionNode.JOB_MANAGE)) {
                    return true;
                }
                handleAssign(sender, args);
            }
            case "remove" -> {
                if (!permissionManager.require(sender, PermissionNode.JOB_MANAGE)) {
                    return true;
                }
                handleRemove(sender, args);
            }
            case "list" -> {
                if (!permissionManager.require(sender, PermissionNode.JOB_MANAGE)) {
                    return true;
                }
                handleList(sender, args);
            }
            case "leader" -> {
                if (!permissionManager.require(sender, PermissionNode.JOB_MANAGE)) {
                    return true;
                }
                handleLeader(sender, args);
            }
            case "gui" -> handleGui(sender, args);
            default -> sender.sendMessage("Unbekannter Unterbefehl. Nutze: " + getUsage());
        }

        return true;
    }

    private void handleAssign(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Nutze: /job assign <spieler> <job>");
            return;
        }

        OfflinePlayer target = resolveKnownPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("Spieler wurde nicht gefunden: " + args[1]);
            return;
        }

        Optional<JobType> jobType = JobType.fromInput(args[2]);
        if (jobType.isEmpty()) {
            sender.sendMessage("Unbekannter Job. Aktuell verfügbar: " + availableJobs());
            return;
        }

        if (!jobService.assignJob(target.getUniqueId(), jobType.get())) {
            sender.sendMessage("Job-Zuweisung ist fehlgeschlagen.");
            return;
        }

        sender.sendMessage(target.getName() + " hat jetzt den Job " + jobType.get().getDisplayName() + ".");
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Nutze: /job remove <spieler>");
            return;
        }

        OfflinePlayer target = resolveKnownPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("Spieler wurde nicht gefunden: " + args[1]);
            return;
        }

        if (!jobService.removeJob(target.getUniqueId())) {
            sender.sendMessage(target.getName() + " hatte keinen Job oder konnte nicht aktualisiert werden.");
            return;
        }

        sender.sendMessage("Job von " + target.getName() + " wurde entfernt.");
    }

    private void handleList(CommandSender sender, String[] args) {
        JobType jobType = JobType.HOLZFAELLER;
        if (args.length >= 2) {
            Optional<JobType> optional = JobType.fromInput(args[1]);
            if (optional.isEmpty()) {
                sender.sendMessage("Unbekannter Job. Aktuell verfügbar: " + availableJobs());
                return;
            }
            jobType = optional.get();
        }

        List<String> names = jobService.getMembers(jobType).stream()
                .map(OfflinePlayer::getName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toList());

        sender.sendMessage("Mitglieder von " + jobType.getDisplayName() + ": "
                + (names.isEmpty() ? "keine" : String.join(", ", names)));
    }

    private void handleGui(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Dieser Befehl ist nur fuer Spieler verfuegbar.");
            return;
        }

        Set<JobType> allowedJobs = getAllowedGuiJobs(player);
        if (allowedJobs.isEmpty()) {
            sender.sendMessage("Du darfst kein Job-GUI öffnen.");
            return;
        }

        if (args.length < 2) {
            jobGuiService.openJobOverviewGui(player, allowedJobs);
            return;
        }

        Optional<JobType> optional = JobType.fromInput(args[1]);
        if (optional.isEmpty()) {
            sender.sendMessage("Unbekannter Job. Aktuell verfügbar: " + availableJobs());
            return;
        }

        if (!allowedJobs.contains(optional.get())) {
            sender.sendMessage("Du darfst diesen Job nicht verwalten.");
            return;
        }

        jobGuiService.openMembersControlGui(player, optional.get());
    }

    private void handleLeader(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Nutze: /job leader <add|remove|list> ...");
            return;
        }

        String mode = args[1].toLowerCase(Locale.ROOT);
        switch (mode) {
            case "add" -> handleLeaderAdd(sender, args);
            case "remove" -> handleLeaderRemove(sender, args);
            case "list" -> handleLeaderList(sender, args);
            default -> sender.sendMessage("Unbekannter Modus. Nutze: /job leader <add|remove|list> ...");
        }
    }

    private void handleLeaderAdd(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("Nutze: /job leader add <spieler> <job>");
            return;
        }

        OfflinePlayer target = resolveKnownPlayer(args[2]);
        if (target == null) {
            sender.sendMessage("Spieler wurde nicht gefunden: " + args[2]);
            return;
        }

        Optional<JobType> jobType = JobType.fromInput(args[3]);
        if (jobType.isEmpty()) {
            sender.sendMessage("Unbekannter Job. Aktuell verfügbar: " + availableJobs());
            return;
        }

        if (!jobService.addLeader(target.getUniqueId(), jobType.get())) {
            sender.sendMessage("Leader konnte nicht hinzugefuegt werden.");
            return;
        }

        sender.sendMessage(target.getName() + " ist nun Leader fuer " + jobType.get().getDisplayName() + ".");
    }

    private void handleLeaderRemove(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("Nutze: /job leader remove <spieler> <job>");
            return;
        }

        OfflinePlayer target = resolveKnownPlayer(args[2]);
        if (target == null) {
            sender.sendMessage("Spieler wurde nicht gefunden: " + args[2]);
            return;
        }

        Optional<JobType> jobType = JobType.fromInput(args[3]);
        if (jobType.isEmpty()) {
            sender.sendMessage("Unbekannter Job. Aktuell verfügbar: " + availableJobs());
            return;
        }

        if (!jobService.removeLeader(target.getUniqueId(), jobType.get())) {
            sender.sendMessage("Leader konnte nicht entfernt werden oder war nicht eingetragen.");
            return;
        }

        sender.sendMessage(target.getName() + " ist kein Leader mehr fuer " + jobType.get().getDisplayName() + ".");
    }

    private void handleLeaderList(CommandSender sender, String[] args) {
        JobType jobType = JobType.HOLZFAELLER;
        if (args.length >= 3) {
            Optional<JobType> optional = JobType.fromInput(args[2]);
            if (optional.isEmpty()) {
                sender.sendMessage("Unbekannter Job. Aktuell verfügbar: " + availableJobs());
                return;
            }
            jobType = optional.get();
        }

        List<String> names = jobService.getLeaders(jobType).stream()
                .map(OfflinePlayer::getName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toList());

        sender.sendMessage("Leader von " + jobType.getDisplayName() + ": "
                + (names.isEmpty() ? "keine" : String.join(", ", names)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterByPrefix(List.of("assign", "remove", "list", "leader", "gui"), args[0]);
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            if (sub.equals("assign") || sub.equals("remove")) {
                List<String> online = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());
                return filterByPrefix(online, args[1]);
            }
            if (sub.equals("list") || sub.equals("gui")) {
                return filterByPrefix(Arrays.stream(JobType.values())
                        .map(JobType::getId)
                        .collect(Collectors.toList()), args[1]);
            }
            if (sub.equals("leader")) {
                return filterByPrefix(List.of("add", "remove", "list"), args[1]);
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("assign")) {
            return filterByPrefix(Arrays.stream(JobType.values())
                    .map(JobType::getId)
                    .collect(Collectors.toList()), args[2]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("leader")
                && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            List<String> online = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            return filterByPrefix(online, args[2]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("leader") && args[1].equalsIgnoreCase("list")) {
            return filterByPrefix(Arrays.stream(JobType.values())
                    .map(JobType::getId)
                    .collect(Collectors.toList()), args[2]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("leader")
                && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            return filterByPrefix(Arrays.stream(JobType.values())
                    .map(JobType::getId)
                    .collect(Collectors.toList()), args[3]);
        }

        return List.of();
    }

    private List<String> filterByPrefix(List<String> values, String typed) {
        String prefix = typed.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();

        for (String value : values) {
            if (value.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                result.add(value);
            }
        }

        return result;
    }

    private OfflinePlayer resolveKnownPlayer(String input) {
        Player online = Bukkit.getPlayerExact(input);
        if (online != null) {
            return online;
        }

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (!offlinePlayer.hasPlayedBefore()) {
                continue;
            }

            String name = offlinePlayer.getName();
            if (name != null && name.equalsIgnoreCase(input)) {
                return offlinePlayer;
            }
        }

        try {
            UUID uuid = UUID.fromString(input);
            OfflinePlayer byUuid = Bukkit.getOfflinePlayer(uuid);
            if (byUuid.hasPlayedBefore() || byUuid.isOnline()) {
                return byUuid;
            }
        } catch (IllegalArgumentException ignored) {
            // Not a UUID, ignore and continue.
        }

        return null;
    }

    private Set<JobType> getAllowedGuiJobs(Player player) {
        if (permissionManager.has(player, PermissionNode.JOB_GUI)
                || permissionManager.has(player, PermissionNode.JOB_MANAGE)) {
            return EnumSet.allOf(JobType.class);
        }

        return jobService.getLeaderJobs(player.getUniqueId());
    }

    private String availableJobs() {
        return Arrays.stream(JobType.values())
                .map(JobType::getId)
                .collect(Collectors.joining(", "));
    }
}



