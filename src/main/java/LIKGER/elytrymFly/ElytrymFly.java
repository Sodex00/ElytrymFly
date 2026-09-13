package LIKGER.elytrymFly;

import LIKGER.elytrymFly.combat.CombatIntegration;
import LIKGER.elytrymFly.combat.CombatIntegrationFactory;
import LIKGER.elytrymFly.command.AdminCommand;
import LIKGER.elytrymFly.command.FlyCommand;
import LIKGER.elytrymFly.config.ConfigManager;
import LIKGER.elytrymFly.config.MessagesManager;
import LIKGER.elytrymFly.flight.DurabilityService;
import LIKGER.elytrymFly.flight.FlightManager;
import LIKGER.elytrymFly.flight.FlightTickTask;
import LIKGER.elytrymFly.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ElytrymFly extends JavaPlugin {
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private FlightManager flightManager;
    private DurabilityService durabilityService;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.setup();

        messagesManager = new MessagesManager(this);
        messagesManager.setup();

        flightManager = new FlightManager(this);
        durabilityService = new DurabilityService(this, flightManager);

        CombatIntegration combatIntegration = CombatIntegrationFactory.resolve(this, flightManager);
        flightManager.setCombatIntegration(combatIntegration);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getCommand("ffly").setExecutor(new FlyCommand(this));
        AdminCommand adminCommand = new AdminCommand(this);
        getCommand("elytrymfly").setExecutor(adminCommand);
        getCommand("elytrymfly").setTabCompleter(adminCommand);

        new FlightTickTask(this).runTaskTimer(this, 0L, 20L);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public FlightManager getFlightManager() {
        return flightManager;
    }

    public DurabilityService getDurabilityService() {
        return durabilityService;
    }
}
