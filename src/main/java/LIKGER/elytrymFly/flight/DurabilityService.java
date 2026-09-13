package LIKGER.elytrymFly.flight;

import LIKGER.elytrymFly.ElytrymFly;
import LIKGER.elytrymFly.config.ConfigManager;
import LIKGER.elytrymFly.config.MessagesManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class DurabilityService {
    private final ElytrymFly plugin;
    private final FlightManager flightManager;
    private final Map<UUID, Long> lastWarningMillis = new ConcurrentHashMap<>();

    public DurabilityService(ElytrymFly plugin, FlightManager flightManager) {
        this.plugin = plugin;
        this.flightManager = flightManager;
    }

    public static double remainingPercent(ItemStack item) {
        if (item == null || item.getType() != Material.ELYTRA) {
            return 100.0;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return 100.0;
        }

        short max = item.getType().getMaxDurability();
        if (max <= 0) {
            return 100.0;
        }

        return 100.0 * (max - damageable.getDamage()) / max;
    }

    public void tick(Player player, ItemStack elytra) {
        ConfigManager cfg = plugin.getConfigManager();
        ItemMeta meta = elytra.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return;
        }

        short maxDurability = elytra.getType().getMaxDurability();
        int rawAmount = computeRawAmount(cfg, maxDurability);
        if (rawAmount <= 0) {
            return;
        }

        int unbreakingLevel = elytra.getEnchantmentLevel(Enchantment.UNBREAKING);
        int amount = cfg.isRespectUnbreaking() ? rollUnbreaking(rawAmount, unbreakingLevel) : rawAmount;
        if (amount <= 0) {
            return;
        }

        PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, elytra, amount, amount);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getDamage() <= 0) {
            return;
        }

        int newDamage = damageable.getDamage() + event.getDamage();
        boolean depleted = newDamage >= maxDurability;

        if (depleted && cfg.getDurabilityBreakAction() == DurabilityBreakAction.DISABLE_FLIGHT) {
            // Keep the elytra usable, just out of durability budget for our custom flight.
            newDamage = maxDurability - 1;
        }

        damageable.setDamage(Math.min(newDamage, maxDurability));
        elytra.setItemMeta(damageable);

        if (depleted) {
            handleDepleted(player, cfg);
            return;
        }

        double percentLeft = remainingPercent(elytra);
        if (cfg.getLowDurabilityWarningPercent() > 0 && percentLeft <= cfg.getLowDurabilityWarningPercent()) {
            maybeWarn(player, percentLeft, cfg);
        }
    }

    private int computeRawAmount(ConfigManager cfg, short maxDurability) {
        if (cfg.getDurabilityMode() == DurabilityMode.FIXED) {
            return cfg.getDurabilityFixedAmount();
        }

        double percent = cfg.getDurabilityPercentAmount();
        if (percent <= 0) {
            return 0;
        }
        return Math.max(1, (int) Math.round(maxDurability * (percent / 100.0)));
    }

    private int rollUnbreaking(int rawAmount, int unbreakingLevel) {
        if (unbreakingLevel <= 0) {
            return rawAmount;
        }

        int actual = 0;
        for (int i = 0; i < rawAmount; i++) {
            if (ThreadLocalRandom.current().nextInt(unbreakingLevel + 1) == 0) {
                actual++;
            }
        }
        return actual;
    }

    private void handleDepleted(Player player, ConfigManager cfg) {
        if (cfg.getDurabilityBreakAction() == DurabilityBreakAction.BREAK_ELYTRA) {
            player.getInventory().setChestplate(null);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            flightManager.disable(player, "durability-broken", false);
        } else {
            flightManager.disable(player, "durability-depleted", false);
        }
    }

    private void maybeWarn(Player player, double percentLeft, ConfigManager cfg) {
        UUID id = player.getUniqueId();
        long cooldownMillis = cfg.getWarningCooldownSeconds() * 1000L;
        long now = System.currentTimeMillis();
        Long last = lastWarningMillis.get(id);
        if (last != null && cooldownMillis > 0 && (now - last) < cooldownMillis) {
            return;
        }

        lastWarningMillis.put(id, now);
        MessagesManager msg = plugin.getMessagesManager();
        msg.send(player, "durability-low-warning", "%percent%", Math.round(percentLeft));
    }

    public void clear(UUID uuid) {
        lastWarningMillis.remove(uuid);
    }
}
