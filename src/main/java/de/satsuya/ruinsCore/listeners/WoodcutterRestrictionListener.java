package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class WoodcutterRestrictionListener implements Listener {

    private static final Map<Material, Material> LOG_TO_PLANK = new EnumMap<>(Material.class);

    static {
        LOG_TO_PLANK.put(Material.OAK_LOG, Material.OAK_PLANKS);
        LOG_TO_PLANK.put(Material.OAK_WOOD, Material.OAK_PLANKS);
        LOG_TO_PLANK.put(Material.SPRUCE_LOG, Material.SPRUCE_PLANKS);
        LOG_TO_PLANK.put(Material.SPRUCE_WOOD, Material.SPRUCE_PLANKS);
        LOG_TO_PLANK.put(Material.BIRCH_LOG, Material.BIRCH_PLANKS);
        LOG_TO_PLANK.put(Material.BIRCH_WOOD, Material.BIRCH_PLANKS);
        LOG_TO_PLANK.put(Material.JUNGLE_LOG, Material.JUNGLE_PLANKS);
        LOG_TO_PLANK.put(Material.JUNGLE_WOOD, Material.JUNGLE_PLANKS);
        LOG_TO_PLANK.put(Material.ACACIA_LOG, Material.ACACIA_PLANKS);
        LOG_TO_PLANK.put(Material.ACACIA_WOOD, Material.ACACIA_PLANKS);
        LOG_TO_PLANK.put(Material.DARK_OAK_LOG, Material.DARK_OAK_PLANKS);
        LOG_TO_PLANK.put(Material.DARK_OAK_WOOD, Material.DARK_OAK_PLANKS);
        LOG_TO_PLANK.put(Material.MANGROVE_LOG, Material.MANGROVE_PLANKS);
        LOG_TO_PLANK.put(Material.MANGROVE_WOOD, Material.MANGROVE_PLANKS);
        LOG_TO_PLANK.put(Material.CHERRY_LOG, Material.CHERRY_PLANKS);
        LOG_TO_PLANK.put(Material.CHERRY_WOOD, Material.CHERRY_PLANKS);
        LOG_TO_PLANK.put(Material.PALE_OAK_LOG, Material.PALE_OAK_PLANKS);
        LOG_TO_PLANK.put(Material.PALE_OAK_WOOD, Material.PALE_OAK_PLANKS);
        LOG_TO_PLANK.put(Material.CRIMSON_STEM, Material.CRIMSON_PLANKS);
        LOG_TO_PLANK.put(Material.CRIMSON_HYPHAE, Material.CRIMSON_PLANKS);
        LOG_TO_PLANK.put(Material.WARPED_STEM, Material.WARPED_PLANKS);
        LOG_TO_PLANK.put(Material.WARPED_HYPHAE, Material.WARPED_PLANKS);

        LOG_TO_PLANK.put(Material.STRIPPED_OAK_LOG, Material.OAK_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_OAK_WOOD, Material.OAK_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_SPRUCE_LOG, Material.SPRUCE_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_SPRUCE_WOOD, Material.SPRUCE_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_BIRCH_LOG, Material.BIRCH_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_BIRCH_WOOD, Material.BIRCH_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_JUNGLE_LOG, Material.JUNGLE_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_JUNGLE_WOOD, Material.JUNGLE_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_ACACIA_LOG, Material.ACACIA_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_ACACIA_WOOD, Material.ACACIA_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_DARK_OAK_LOG, Material.DARK_OAK_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_DARK_OAK_WOOD, Material.DARK_OAK_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_MANGROVE_LOG, Material.MANGROVE_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_MANGROVE_WOOD, Material.MANGROVE_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_CHERRY_LOG, Material.CHERRY_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_CHERRY_WOOD, Material.CHERRY_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_PALE_OAK_LOG, Material.PALE_OAK_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_PALE_OAK_WOOD, Material.PALE_OAK_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_CRIMSON_STEM, Material.CRIMSON_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_CRIMSON_HYPHAE, Material.CRIMSON_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_WARPED_STEM, Material.WARPED_PLANKS);
        LOG_TO_PLANK.put(Material.STRIPPED_WARPED_HYPHAE, Material.WARPED_PLANKS);
    }

    private final JobService jobService;
    private final int paralysisTicks;
    private final double plankDropChance;

    public WoodcutterRestrictionListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.paralysisTicks = plugin.getConfig().getInt("jobs.holzfaeller.restrictions.paralysis-ticks", 80);
        this.plankDropChance = clamp(plugin.getConfig().getDouble("jobs.holzfaeller.restrictions.plank-drop-chance", 0.35D));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Material broken = event.getBlock().getType();
        Material plankType = LOG_TO_PLANK.get(broken);
        if (plankType == null) {
            return;
        }

        Player player = event.getPlayer();
        if (jobService.hasJob(player.getUniqueId(), JobType.HOLZFAELLER)
                || jobService.hasJob(player.getUniqueId(), JobType.BUILDER)) {
            return;
        }

        applyParalysis(player, paralysisTicks);
        event.setDropItems(false);

        if (ThreadLocalRandom.current().nextDouble() <= plankDropChance) {
            dropPlank(event.getBlock().getLocation(), plankType);
        }
    }

    private void applyParalysis(Player player, int durationTicks) {
        PotionEffect slow = new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 8, false, false, true);
        PotionEffect fatigue = new PotionEffect(PotionEffectType.MINING_FATIGUE, durationTicks, 2, false, false, true);
        player.addPotionEffect(slow);
        player.addPotionEffect(fatigue);
    }

    private void dropPlank(Location location, Material plankType) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.dropItemNaturally(location, new ItemStack(plankType, 1));
    }

    private double clamp(double chance) {
        return Math.max(0.0D, Math.min(1.0D, chance));
    }
}




