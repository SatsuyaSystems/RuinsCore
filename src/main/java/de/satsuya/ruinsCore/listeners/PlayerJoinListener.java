package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.util.StaffAlertUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    private final StaffAlertUtil staffAlertUtil;

    public PlayerJoinListener(RuinsCore plugin) {
        this.staffAlertUtil = plugin.getStaffAlertUtil();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        staffAlertUtil.alert("Join: " + event.getPlayer().getName());
    }
}

