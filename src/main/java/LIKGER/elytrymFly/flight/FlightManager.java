package LIKGER.elytrymFly.flight;

import LIKGER.elytrymFly.ElytrymFly;
import LIKGER.elytrymFly.combat.CombatIntegration;
import LIKGER.elytrymFly.config.ConfigManager;
import LIKGER.elytrymFly.config.MessagesManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlightManager {
    private final ElytrymFly plugin;
    private final Set<UUID> flyingPlayers = ConcurrentHashMap.newKeySet();
    private final java.util.Map<UUID, Long> lastToggleMillis = new ConcurrentHashMap<>();
    private CombatIntegration combatIntegration;

    public FlightManager(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    public void setCombatIntegration(CombatIntegration combatIntegration) {
        this.combatIntegration = combatIntegration;
    }

    public CombatIntegration getCombatIntegration() {
        return combatIntegration;
    }

    public boolean isFlying(UUID uuid) {
        return flyingPlayers.contains(uuid);
    }

    public Set<UUID> snapshotFlyingPlayers() {
        return new HashSet<>(flyingPlayers);
    }

    public boolean hasElytraEquipped(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        return chestplate != null && chestplate.getType() == Material.ELYTRA;
    }

    public void toggle(Player player) {
        if (flyingPlayers.contains(player.getUniqueId())) {
            disable(player, "flight-disabled", false);
        } else {
            tryEnable(player);
        }
    }

    private void tryEnable(Player player) {
        ConfigManager cfg = plugin.getConfigManager();
        MessagesManager msg = plugin.getMessagesManager();
        boolean bypass = player.hasPermission("elytrymfly.bypass");

        if (!bypass && !hasElytraEquipped(player)) {
            msg.send(player, "no-elytra");
            return;
        }

        if (!bypass && cfg.getRestrictedWorlds().contains(player.getWorld().getName())) {
            msg.send(player, "flight-blocked-world");
            return;
        }

        if (!bypass) {
            long remainingMillis = remainingCooldownMillis(player, cfg);
            if (remainingMillis > 0) {
                long seconds = (remainingMillis + 999) / 1000;
                msg.send(player, "flight-blocked-cooldown", "%seconds%", seconds);
                return;
            }
        }

        if (!bypass && combatIntegration != null && combatIntegration.isAvailable()
                && cfg.isBlockFlightWhileTagged() && combatIntegration.isInCombat(player)) {
            msg.send(player, "flight-blocked-combat");
            return;
        }

        if (!bypass && cfg.isDurabilityEnabled() && cfg.getMinDurabilityPercentToFly() > 0) {
            ItemStack chestplate = player.getInventory().getChestplate();
            if (DurabilityService.remainingPercent(chestplate) < cfg.getMinDurabilityPercentToFly()) {
                msg.send(player, "flight-blocked-durability");
                return;
            }
        }

        player.setAllowFlight(true);
        player.setFlying(true);
        flyingPlayers.add(player.getUniqueId());
        lastToggleMillis.put(player.getUniqueId(), System.currentTimeMillis());
        msg.send(player, "flight-enabled");
    }

    private long remainingCooldownMillis(Player player, ConfigManager cfg) {
        long cooldownMillis = cfg.getToggleCooldownSeconds() * 1000L;
        if (cooldownMillis <= 0) {
            return 0;
        }
        Long last = lastToggleMillis.get(player.getUniqueId());
        if (last == null) {
            return 0;
        }
        return (last + cooldownMillis) - System.currentTimeMillis();
    }

    /**
     * Called by combat integrations (vanilla hit, CombatLogX, GreatCombat) when a flying player enters combat.
     */
    public void handleCombatTag(Player player, String messageKey) {
        ConfigManager cfg = plugin.getConfigManager();
        if (!cfg.isDisableFlightOnTag() || !isFlying(player.getUniqueId())) {
            return;
        }
        disable(player, messageKey, cfg.isApplyHitEffectsOnTag());
    }

    public void disable(Player player, String messageKey, boolean applyHitEffects) {
        UUID id = player.getUniqueId();
        if (!flyingPlayers.remove(id)) {
            return;
        }

        player.setAllowFlight(false);
        player.setFlying(false);

        if (applyHitEffects) {
            for (PotionEffect effect : plugin.getConfigManager().getHitEffects()) {
                player.addPotionEffect(effect);
            }
        }

        if (messageKey != null) {
            plugin.getMessagesManager().send(player, messageKey);
        }
    }

    public void forceStop(UUID uuid) {
        flyingPlayers.remove(uuid);
    }

    public void clearPlayerData(UUID uuid) {
        flyingPlayers.remove(uuid);
        lastToggleMillis.remove(uuid);
    }
}
