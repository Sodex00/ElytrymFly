package LIKGER.elytrymFly;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ElytraCheckTask extends BukkitRunnable {
    private final ElytrymFly plugin;

    public ElytraCheckTask(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (UUID uuid : plugin.getFlyingPlayers()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player == null || !player.isOnline() || !player.getAllowFlight()) {
                plugin.getFlyingPlayers().remove(uuid);
                continue;
            }

            if (player.hasPermission("elytrymfly.bypass")) {
                continue;
            }

            ItemStack chestplate = player.getInventory().getChestplate();
            boolean hasElytra = chestplate != null && chestplate.getType() == Material.ELYTRA;

            if (!hasElytra) {
                player.setAllowFlight(false);
                player.setFlying(false);
                plugin.getFlyingPlayers().remove(uuid);
                player.sendMessage(plugin.getConfigManager().getMessage("elytra-removed"));
            }
        }
    }
}
