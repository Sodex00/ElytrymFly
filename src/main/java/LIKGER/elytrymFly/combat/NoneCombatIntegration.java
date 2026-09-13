package LIKGER.elytrymFly.combat;

import LIKGER.elytrymFly.flight.FlightManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Fallback combat detection used when neither CombatLogX nor GreatCombat is installed/selected.
 * Reacts to direct and projectile PvP hits; has no persistent "in combat" state of its own.
 */
public class NoneCombatIntegration implements CombatIntegration, Listener {
    private final Plugin plugin;
    private final FlightManager flightManager;

    public NoneCombatIntegration(Plugin plugin, FlightManager flightManager) {
        this.plugin = plugin;
        this.flightManager = flightManager;
    }

    @Override
    public CombatType getType() {
        return CombatType.NONE;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean isInCombat(Player player) {
        return false;
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getFinalDamage() <= 0) {
            return;
        }

        tagIfPlayer(event.getEntity());
        tagIfPlayer(resolveAttacker(event.getDamager()));
    }

    private Entity resolveAttacker(Entity damager) {
        if (damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            return shooter instanceof Entity ? (Entity) shooter : null;
        }
        return damager;
    }

    private void tagIfPlayer(Entity entity) {
        if (entity instanceof Player player) {
            flightManager.handleCombatTag(player, "flight-disabled-hit");
        }
    }
}
