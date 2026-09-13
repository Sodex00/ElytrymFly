package LIKGER.elytrymFly.config;

import LIKGER.elytrymFly.ElytrymFly;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessagesManager {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    private final ElytrymFly plugin;
    private FileConfiguration messages;

    public MessagesManager(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public Component get(String path, Object... placeholders) {
        String raw = messages.getString(path);
        if (raw == null) {
            plugin.getLogger().warning("Missing message in messages.yml: " + path);
            raw = "&cMissing message: " + path;
        }
        return SERIALIZER.deserialize(applyPlaceholders(raw, placeholders));
    }

    public void send(CommandSender target, String path, Object... placeholders) {
        target.sendMessage(get(path, placeholders));
    }

    private String applyPlaceholders(String raw, Object... placeholders) {
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            raw = raw.replace(String.valueOf(placeholders[i]), String.valueOf(placeholders[i + 1]));
        }
        return raw;
    }
}
