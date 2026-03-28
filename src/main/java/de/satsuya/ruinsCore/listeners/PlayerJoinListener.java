package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.LeutnantHealthService;
import de.satsuya.ruinsCore.core.jobs.RitterHealthService;
import de.satsuya.ruinsCore.core.jobs.WacheHealthService;
import de.satsuya.ruinsCore.core.util.StaffAlertUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    private final StaffAlertUtil staffAlertUtil;
    private final LeutnantHealthService leutnantHealthService;
    private final RitterHealthService ritterHealthService;
    private final WacheHealthService wacheHealthService;

    public PlayerJoinListener(RuinsCore plugin) {
        this.staffAlertUtil = plugin.getStaffAlertUtil();
        this.leutnantHealthService = plugin.getLeutnantHealthService();
        this.ritterHealthService = plugin.getRitterHealthService();
        this.wacheHealthService = plugin.getWacheHealthService();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        staffAlertUtil.alert("Join: " + event.getPlayer().getName());
        leutnantHealthService.sync(event.getPlayer());
        ritterHealthService.sync(event.getPlayer());
        wacheHealthService.sync(event.getPlayer());
    }
}

