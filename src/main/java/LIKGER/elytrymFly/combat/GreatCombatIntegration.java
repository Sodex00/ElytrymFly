package LIKGER.elytrymFly.combat;

import LIKGER.elytrymFly.flight.FlightManager;
import dev.enco.greatcombat.api.GreatCombatProvider;
import dev.enco.greatcombat.api.events.CombatJoinEvent;
import dev.enco.greatcombat.api.events.CombatStartEvent;
import dev.enco.greatcombat.api.managers.ICombatManager;
import dev.enco.greatcombat.api.models.IUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class GreatCombatIntegration implements CombatIntegration, Listener {
    private static final String PLUGIN_NAME = "GreatCombat";

    private final Plugin plugin;
    private final FlightManager flightManager;

    public GreatCombatIntegration(Plugin plugin, FlightManager flightManager) {
        this.plugin = plugin;
        this.flightManager = flightManager;
    }

    @Override
    public CombatType getType() {
        return CombatType.GREATCOMBAT;
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin(PLUGIN_NAME) != null && GreatCombatProvider.isLoaded();
    }

    @Override
    public boolean isInCombat(Player player) {
        if (!GreatCombatProvider.isLoaded()) {
            return false;
        }
        ICombatManager manager = GreatCombatProvider.getPlugin().getManager(ICombatManager.class);
        return manager.isInCombat(player.getUniqueId());
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCombatStart(CombatStartEvent event) {
        tagIfPlayer(event.getDamager());
        tagIfPlayer(event.getTarget());
    }

    @EventHandler
    public void onCombatJoin(CombatJoinEvent event) {
        tagIfPlayer(event.getDamager());
        tagIfPlayer(event.getTarget());
    }

    private void tagIfPlayer(IUser user) {
        Player player = user.asPlayer();
        if (player != null) {
            flightManager.handleCombatTag(player, "flight-disabled-combat");
        }
    }
}
