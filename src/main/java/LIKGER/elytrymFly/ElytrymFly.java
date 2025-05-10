package LIKGER.elytrymFly;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ElytrymFly extends JavaPlugin {
    private ConfigManager configManager;
    private Set<UUID> flyingPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.setupConfig();

        getCommand("ffly").setExecutor(new FlyCommand(this));
        getCommand("elytrymfly").setExecutor(new AdminCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        new ElytraCheckTask(this).runTaskTimer(this, 0L, 20L);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Set<UUID> getFlyingPlayers() {
        return flyingPlayers;
    }
}