package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class FisherLootRestrictionListener implements Listener {

    private static final List<Material> FISH_LOOT = List.of(
            Material.COD,
            Material.SALMON,
            Material.TROPICAL_FISH,
            Material.PUFFERFISH
    );

    private final JobService jobService;

    public FisherLootRestrictionListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        if (!(event.getCaught() instanceof Item caughtItem)) {
            return;
        }

        Player player = event.getPlayer();
        if (jobService.hasJob(player.getUniqueId(), JobType.FISCHER) || player.isOp()) {
            return;
        }

        ItemStack stack = caughtItem.getItemStack();
        if (isFish(stack.getType())) {
            return;
        }

        Material replacement = randomFish();
        caughtItem.setItemStack(new ItemStack(replacement, Math.max(1, stack.getAmount())));
    }

    private boolean isFish(Material material) {
        return FISH_LOOT.contains(material);
    }

    private Material randomFish() {
        int index = ThreadLocalRandom.current().nextInt(FISH_LOOT.size());
        return FISH_LOOT.get(index);
    }
}

