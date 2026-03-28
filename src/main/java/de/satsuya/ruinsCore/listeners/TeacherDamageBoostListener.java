package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public final class TeacherDamageBoostListener implements Listener {

    private final JobService jobService;
    private final double stickDamageMultiplier;

    public TeacherDamageBoostListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.stickDamageMultiplier = plugin.getConfig().getDouble("jobs.lehrer.bonuses.stick-damage-multiplier", 2.0D);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }

        if (!jobService.hasJob(attacker.getUniqueId(), JobType.LEHRER)) {
            return;
        }

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (weapon.getType() != Material.STICK) {
            return;
        }

        double currentDamage = event.getDamage();
        double boostedDamage = currentDamage * stickDamageMultiplier;
        event.setDamage(boostedDamage);
    }
}

