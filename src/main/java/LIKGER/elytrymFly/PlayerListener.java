package LIKGER.elytrymFly;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerListener implements Listener {
    private final ElytrymFly plugin;

    public PlayerListener(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!player.isFlying()) return;

        player.setAllowFlight(false);
        player.setFlying(false);
        plugin.getFlyingPlayers().remove(player.getUniqueId());

        for (org.bukkit.potion.PotionEffect effect : plugin.getConfigManager().getHitEffects()) {
            player.addPotionEffect(effect);
        }

        player.sendMessage(plugin.getConfigManager().getMessage("flight-disabled-hit"));
    }
}