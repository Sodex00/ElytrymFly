package LIKGER.elytrymFly.config;

import LIKGER.elytrymFly.ElytrymFly;
import LIKGER.elytrymFly.combat.CombatType;
import LIKGER.elytrymFly.flight.DurabilityBreakAction;
import LIKGER.elytrymFly.flight.DurabilityMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ConfigManager {
    private final ElytrymFly plugin;
    private FileConfiguration config;

    public ConfigManager(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public CombatType getCombatType() {
        String raw = config.getString("combat.plugin", "AUTO");
        try {
            return CombatType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Invalid combat.plugin value '" + raw + "', falling back to AUTO.");
            return CombatType.AUTO;
        }
    }

    public boolean isDisableFlightOnTag() {
        return config.getBoolean("combat.disable-flight-on-tag", true);
    }

    public boolean isBlockFlightWhileTagged() {
        return config.getBoolean("combat.block-flight-while-tagged", true);
    }

    public boolean isApplyHitEffectsOnTag() {
        return config.getBoolean("combat.apply-hit-effects-on-tag", true);
    }

    public Set<String> getRestrictedWorlds() {
        List<String> list = config.getStringList("flight.restricted-worlds");
        return new HashSet<>(list);
    }

    public int getToggleCooldownSeconds() {
        return Math.max(0, config.getInt("flight.toggle-cooldown-seconds", 0));
    }

    public boolean isDisableOnGamemodeChange() {
        return config.getBoolean("flight.disable-on-gamemode-change", true);
    }

    public boolean isDurabilityEnabled() {
        return config.getBoolean("durability.enabled", true);
    }

    public DurabilityMode getDurabilityMode() {
        String raw = config.getString("durability.mode", "FIXED");
        try {
            return DurabilityMode.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Invalid durability.mode value '" + raw + "', falling back to FIXED.");
            return DurabilityMode.FIXED;
        }
    }

    public long getDurabilityIntervalTicks() {
        return Math.max(1, config.getLong("durability.interval-ticks", 100));
    }

    public int getDurabilityFixedAmount() {
        return Math.max(0, config.getInt("durability.fixed-amount", 1));
    }

    public double getDurabilityPercentAmount() {
        return Math.max(0.0, config.getDouble("durability.percent-amount", 2.0));
    }

    public boolean isRespectUnbreaking() {
        return config.getBoolean("durability.respect-unbreaking", true);
    }

    public double getMinDurabilityPercentToFly() {
        return Math.max(0.0, config.getDouble("durability.min-durability-percent-to-fly", 0.0));
    }

    public double getLowDurabilityWarningPercent() {
        return Math.max(0.0, config.getDouble("durability.low-durability-warning-percent", 15.0));
    }

    public int getWarningCooldownSeconds() {
        return Math.max(0, config.getInt("durability.warning-cooldown-seconds", 10));
    }

    public DurabilityBreakAction getDurabilityBreakAction() {
        String raw = config.getString("durability.on-break", "DISABLE_FLIGHT");
        try {
            return DurabilityBreakAction.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Invalid durability.on-break value '" + raw + "', falling back to DISABLE_FLIGHT.");
            return DurabilityBreakAction.DISABLE_FLIGHT;
        }
    }

    public List<PotionEffect> getHitEffects() {
        List<PotionEffect> effects = new ArrayList<>();
        List<?> effectList = config.getList("hit-effects");
        if (effectList == null) {
            return effects;
        }

        for (Object obj : effectList) {
            if (!(obj instanceof Map)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> effectMap = (Map<String, Object>) obj;
            String effectName = String.valueOf(effectMap.get("effect"));
            int duration = toInt(effectMap.get("duration"), 60);
            int amplifier = toInt(effectMap.get("amplifier"), 0);

            PotionEffectType type = Registry.EFFECT.get(NamespacedKey.minecraft(effectName.toLowerCase(Locale.ROOT)));
            if (type != null) {
                effects.add(new PotionEffect(type, duration, amplifier));
            } else {
                plugin.getLogger().warning("Invalid potion effect type in hit-effects: " + effectName);
            }
        }
        return effects;
    }

    private int toInt(Object value, int fallback) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return fallback;
    }
}
