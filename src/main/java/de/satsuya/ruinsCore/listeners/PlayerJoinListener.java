package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobHealthService;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.util.StaffAlertUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    private final StaffAlertUtil staffAlertUtil;
    private final JobHealthService jobHealthService;
    private final JobService jobService;

    public PlayerJoinListener(RuinsCore plugin) {
        this.staffAlertUtil = plugin.getStaffAlertUtil();
        this.jobHealthService = plugin.getJobHealthService();
        this.jobService = plugin.getJobService();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        staffAlertUtil.alert("Join: " + event.getPlayer().getName());
        jobHealthService.sync(event.getPlayer());
        
        // Formatiere Join-Nachricht mit Job-Prefix
        String jobPrefix = getJobPrefix(event.getPlayer().getUniqueId());
        Component joinMessage = Component.text(
            jobPrefix + " §a" + event.getPlayer().getName() + " §7hat den Server betreten.",
            NamedTextColor.WHITE
        );
        
        event.joinMessage(joinMessage);
    }

    /**
     * Gibt den Job-Prefix eines Spielers zurück
     */
    private String getJobPrefix(java.util.UUID playerUuid) {
        java.util.Optional<de.satsuya.ruinsCore.core.jobs.JobType> job = jobService.getJob(playerUuid);
        if (job.isPresent()) {
            de.satsuya.ruinsCore.core.jobs.JobColor jobColor = de.satsuya.ruinsCore.core.jobs.JobColor.fromJobType(job.get());
            if (jobColor != null) {
                return jobColor.getDisplayName();
            }
        }
        return "§7[?]";
    }
}

