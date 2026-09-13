package LIKGER.elytrymFly.flight;

import LIKGER.elytrymFly.ElytrymFly;
import LIKGER.elytrymFly.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Runs every second: drops players who logged off / lost their allow-flight flag / took off their
 * elytra, and drives durability drain for everyone still legitimately flying.
 */
public class FlightTickTask extends BukkitRunnable {
    private static final long TICK_PERIOD = 20L;

    private final ElytrymFly plugin;
    private long ticksSinceLastDrain = 0L;

    public FlightTickTask(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        FlightManager flightManager = plugin.getFlightManager();
        ConfigManager cfg = plugin.getConfigManager();

        ticksSinceLastDrain += TICK_PERIOD;
        boolean durabilityDue = false;
        if (cfg.isDurabilityEnabled() && ticksSinceLastDrain >= cfg.getDurabilityIntervalTicks()) {
            durabilityDue = true;
            ticksSinceLastDrain = 0L;
        }

        for (UUID uuid : flightManager.snapshotFlyingPlayers()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player == null || !player.isOnline() || !player.getAllowFlight()) {
                flightManager.forceStop(uuid);
                continue;
            }

            if (player.hasPermission("elytrymfly.bypass")) {
                continue;
            }

            ItemStack chestplate = player.getInventory().getChestplate();
            boolean hasElytra = chestplate != null && chestplate.getType() == Material.ELYTRA;

            if (!hasElytra) {
                flightManager.disable(player, "elytra-removed", false);
                continue;
            }

            if (durabilityDue) {
                plugin.getDurabilityService().tick(player, chestplate);
            }
        }
    }
}
