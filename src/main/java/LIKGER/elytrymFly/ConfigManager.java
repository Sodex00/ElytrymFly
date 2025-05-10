package LIKGER.elytrymFly;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigManager {
    private final ElytrymFly plugin;
    private FileConfiguration config;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{6})");

    public ConfigManager(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    public void setupConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getMessage(String path) {
        String message = config.getString("messages." + path, "&cMessage not found");
        if (message.contains("&#")) {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                String hex = matcher.group(1);
                String replacement = "&x" +
                        "&" + hex.charAt(0) +
                        "&" + hex.charAt(1) +
                        "&" + hex.charAt(2) +
                        "&" + hex.charAt(3) +
                        "&" + hex.charAt(4) +
                        "&" + hex.charAt(5);
                matcher.appendReplacement(result, replacement);
            }
            matcher.appendTail(result);
            message = result.toString();
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<PotionEffect> getHitEffects() {
        List<PotionEffect> effects = new ArrayList<>();
        List<?> effectList = config.getList("hit-effects");
        if (effectList == null) {
            effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            effects.add(new PotionEffect(PotionEffectType.WITHER, 60, 0));
            return effects;
        }

        for (Object obj : effectList) {
            if (obj instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> effectMap = (java.util.Map<String, Object>) obj;
                String effectName = (String) effectMap.get("effect");
                int duration = (int) effectMap.getOrDefault("duration", 60);
                int amplifier = (int) effectMap.getOrDefault("amplifier", 0);

                PotionEffectType type = PotionEffectType.getByName(effectName);
                if (type != null) {
                    effects.add(new PotionEffect(type, duration, amplifier));
                } else {
                    plugin.getLogger().warning("Invalid potion effect type: " + effectName);
                }
            }
        }
        return effects;
    }
}