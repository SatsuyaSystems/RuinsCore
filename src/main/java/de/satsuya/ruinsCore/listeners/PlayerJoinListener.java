package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobHealthService;
import de.satsuya.ruinsCore.core.util.StaffAlertUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    private final StaffAlertUtil staffAlertUtil;
    private final JobHealthService jobHealthService;

    public PlayerJoinListener(RuinsCore plugin) {
        this.staffAlertUtil = plugin.getStaffAlertUtil();
        this.jobHealthService = plugin.getJobHealthService();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        staffAlertUtil.alert("Join: " + event.getPlayer().getName());
        jobHealthService.sync(event.getPlayer());
    }
}

